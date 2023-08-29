package com.miadowicz.githubrepoexplorer.controller;

import com.miadowicz.githubrepoexplorer.dto.RepositoryWithDetailsDto;
import com.miadowicz.githubrepoexplorer.exceptions.UnsupportedAcceptHeaderException;
import com.miadowicz.githubrepoexplorer.services.GithubExplorerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class GithubExplorerController {
    private final GithubExplorerService githubExplorerService;

    @GetMapping(value = "/users/{username}/repositories")
    public ResponseEntity<List<RepositoryWithDetailsDto>> getRepositories(
            @PathVariable String username,
            @RequestHeader(name = "Accept", required = false) String acceptHeader) throws UnsupportedAcceptHeaderException {
        log.info("Received request for user: {}, Accept header: {}", username, acceptHeader);
        if (acceptHeader != null && !acceptHeader.equals("application/json")) {
            log.warn("Unsupported Accept header: {}", acceptHeader);
            throw new UnsupportedAcceptHeaderException("Accept header must be application/json");
        }
        log.info("Returning repositories for user with last commit & sha: {}", username);
        return ResponseEntity.ok(githubExplorerService.fetchNonForkRepositoriesWithBranchesAndLastCommit(username));
    }
}