package com.miadowicz.githubrepoexplorer.client;

import com.miadowicz.githubrepoexplorer.client.dto.BranchDto;
import com.miadowicz.githubrepoexplorer.client.dto.RepositoryDto;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = "github", configuration = GithubFeignConfiguration.class)
public interface GithubFeignClient {

    @GetMapping("/users/{username}/repos")
    List<RepositoryDto> getRepositories(@PathVariable String username,
                                        @RequestParam(defaultValue = "1", required = false) int page,
                                        @RequestParam(defaultValue = "10", required = false) int perPage);

    @GetMapping("/repos/{username}/{repositoryName}/branches")
    List<BranchDto> getBranches(@PathVariable String username,
                                @PathVariable String repositoryName);


}