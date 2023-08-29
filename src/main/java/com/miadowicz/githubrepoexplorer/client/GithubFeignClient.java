package com.miadowicz.githubrepoexplorer.client;

import com.miadowicz.githubrepoexplorer.dto.BranchDto;
import com.miadowicz.githubrepoexplorer.dto.RepositoryDto;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient(name = "github", configuration = GithubFeignConfiguration.class)
public interface GithubFeignClient {

    @GetMapping("/users/{username}/repos")
    List<RepositoryDto> fetchRepositories(@PathVariable String username);

    @GetMapping("/repos/{username}/{repositoryName}/branches")
    List<BranchDto> getBranches(@PathVariable String username,
                                @PathVariable String repositoryName);


}