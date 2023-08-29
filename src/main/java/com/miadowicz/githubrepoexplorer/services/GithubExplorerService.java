package com.miadowicz.githubrepoexplorer.services;
import com.miadowicz.githubrepoexplorer.client.GithubFeignClient;
import com.miadowicz.githubrepoexplorer.dto.BranchDetailsDto;
import com.miadowicz.githubrepoexplorer.dto.BranchDto;
import com.miadowicz.githubrepoexplorer.dto.CommitDto;
import com.miadowicz.githubrepoexplorer.dto.RepositoryWithDetailsDto;
import com.miadowicz.githubrepoexplorer.dto.RepositoryDto;
import com.miadowicz.githubrepoexplorer.exceptions.InternalServerErrorException;
import com.miadowicz.githubrepoexplorer.exceptions.UserNotFoundException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GithubExplorerService {

    private final GithubFeignClient githubFeignClient;

    public GithubExplorerService(GithubFeignClient githubFeignClient) {
        this.githubFeignClient = githubFeignClient;
    }

    public List<RepositoryWithDetailsDto> fetchNonForkRepositoriesWithBranchesAndLastCommit(String username) {
        try {
            //Step 1: Fetch Repositories for user
            List<RepositoryDto> repositories = githubFeignClient.fetchRepositories(username);
            //Step 2: Filter out forked repositories & fetch branches for each repository with last commit & sha
            List<RepositoryWithDetailsDto> result = repositories.stream()
                    .filter(repository -> !repository.isFork())
                    .map(repository -> {
                        List<BranchDto> branches = fetchBranches(username, repository.name());

                        List<BranchDetailsDto> branchDetails = branches.stream()
                                .map(branch -> {
                                    CommitDto lastCommit = branch.commit();
                                    return new BranchDetailsDto(branch.name(), lastCommit.sha());
                                })
                                .toList();

                        return new RepositoryWithDetailsDto(repository.name(), username, branchDetails);
                    })
                    .toList();
            log.debug("Processed repos: {}", result);
            return result;
        } catch (FeignException.NotFound e) {
            log.error("User {} not found", username);
            throw new UserNotFoundException(String.format("User %s not found", username));
        } catch (Exception e) {
            log.error("Error while fetching repositories for user {}", username, e);
            throw new InternalServerErrorException("Error while fetching repositories");
        }
    }

    private List<BranchDto> fetchBranches(String username, String repositoryName) {
        try {
            return githubFeignClient.getBranches(username, repositoryName);
        } catch (FeignException.NotFound e) {
            log.error("Branches {} not found for user {}", repositoryName, username);
            return Collections.emptyList();
        }
    }
}
