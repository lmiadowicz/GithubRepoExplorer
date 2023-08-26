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

@Slf4j
@Component
public class RepositoryFetcher {

    private static final int PAGE_SIZE = 100;

    private final GithubFeignClient githubFeignClient;

    public RepositoryFetcher(GithubFeignClient githubFeignClient) {
        this.githubFeignClient = githubFeignClient;
    }

    public List<RepositoryDto> fetchAllRepositories(String username) {
        log.debug("Starting fetching repositories for user: {}", username);
        List<RepositoryDto> allRepositories = new ArrayList<>();
        int page = 1;
        List<RepositoryDto> repositoriesPage;

        do {
            repositoriesPage = fetchPage(username, page);
            allRepositories.addAll(repositoriesPage);
            log.debug("Fetched {} repositories from page {}", repositoriesPage.size(), page);
            page++;
        } while (!repositoriesPage.isEmpty());

        log.info("Successfully fetched {} repositories for user: {}", allRepositories.size(), username);
        return allRepositories;
    }

    private List<RepositoryDto> fetchPage(String username, int page) {
        try {
            return githubFeignClient.getRepositories(username, page, PAGE_SIZE);
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException(String.format("User %s not found.", username));
        } catch (Exception e) {
            handleException(username, e);
            return Collections.emptyList();
        }
    }

    private void handleException(String username, Exception e) {
        log.error("An unexpected error occurred while fetching repositories for user: {}. Exception details: {}",
                username, e.getMessage(), e);
        throw new InternalServerErrorException("An unexpected error occurred. Please try again later.");
    }

    public List<BranchDto> fetchBranchesAndCommits(RepositoryDto repository, String username) {
        log.debug("Fetching branches for repository: {}", repository.name());
        try {
            List<BranchDto> branches = githubFeignClient.getBranches(username, repository.name());
            log.debug("Successfully fetched {} branches for repository {}", branches.size(), repository.name());
            return branches;
        } catch (FeignException.NotFound e) {
            log.warn("No branches found for repository: {}", repository.name());
            return new ArrayList<>();
        } catch (Exception e) {
            handleException(username, e);
            return Collections.emptyList();
        }
    }
}