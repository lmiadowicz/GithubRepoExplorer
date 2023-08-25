package com.miadowicz.githubrepoexplorer.services;

import com.miadowicz.githubrepoexplorer.client.GithubFeignClient;
import com.miadowicz.githubrepoexplorer.client.dto.BranchDto;
import com.miadowicz.githubrepoexplorer.client.dto.RepositoryDto;
import com.miadowicz.githubrepoexplorer.exceptions.InternalServerErrorException;
import com.miadowicz.githubrepoexplorer.exceptions.UserNotFoundException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class RepositoryFetcher {

    private final GithubFeignClient githubFeignClient;

    public RepositoryFetcher(GithubFeignClient githubFeignClient) {
        this.githubFeignClient = githubFeignClient;
    }

    public List<RepositoryDto> fetchAllRepositories(String username) {
        log.debug("Fetching repositories for user: {}", username);
        List<RepositoryDto> allRepositories = new ArrayList<>();
        int page = 1;
        List<RepositoryDto> repositoriesPage;

        do {
            try {
                repositoriesPage = githubFeignClient.getRepositories(username, page, 100);
                allRepositories.addAll(repositoriesPage);
                page++;
            } catch (FeignException.NotFound e) {
                throw new UserNotFoundException(String.format("User %s not found.", username));
            } catch (Exception e) {
                log.error("An unexpected error occurred while fetching repositories for user: {}. Exception details: {}"
                        , username, e.getMessage(), e);
                throw new InternalServerErrorException("An unexpected error occurred. Please try again later.");
            }
        } while (!repositoriesPage.isEmpty());

        log.debug("Fetched {} repositories for user: {}", allRepositories.size(), username);
        return allRepositories;
    }

    public List<BranchDto> fetchBranchesAndCommits(RepositoryDto repository, String username) {
        log.debug("Fetching branches for repository: {}", repository.name());
        try {
            return githubFeignClient.getBranches(username, repository.name());
        } catch (FeignException.NotFound e) {
            log.warn("No branches found for repository: {}", repository.name());
            return new ArrayList<>();  // Return an empty list when no branches are found
        } catch (Exception e) {
            log.error("An unexpected error occurred while fetching repositories for user: {}", username);
            throw new InternalServerErrorException("An unexpected error occurred. Please try again later.");
        }
    }
}