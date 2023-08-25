package com.miadowicz.githubrepoexplorer.controller;

import com.miadowicz.githubrepoexplorer.client.dto.RepositoryWithDetailsDto;
import com.miadowicz.githubrepoexplorer.services.GithubExplorerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class GithubExplorerController {
    private final GithubExplorerService githubExplorerService;

    @GetMapping("/users/{username}/repositories")
    public ResponseEntity<List<RepositoryWithDetailsDto>> getRepositories(
            @PathVariable String username,
            @RequestHeader(name = "Accept") String acceptHeader) {
        return ResponseEntity.ok(githubExplorerService.getNonForkRepositoriesWithBranches(acceptHeader, username));
    }
}
