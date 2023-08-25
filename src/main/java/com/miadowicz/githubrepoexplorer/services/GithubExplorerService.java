package com.miadowicz.githubrepoexplorer.services;
import com.miadowicz.githubrepoexplorer.client.dto.BranchDto;
import com.miadowicz.githubrepoexplorer.client.dto.RepositoryWithDetailsDto;
import com.miadowicz.githubrepoexplorer.client.dto.RepositoryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class GithubExplorerService {

    private final RepositoryFetcher repositoryFetcher;
    private final RepositoryMapper repositoryMapper;
    private final AcceptHeaderValidator acceptHeaderValidator;

    public GithubExplorerService(RepositoryFetcher repositoryFetcher, RepositoryMapper repositoryMapper, AcceptHeaderValidator acceptHeaderValidator) {
        this.acceptHeaderValidator = acceptHeaderValidator;
        this.repositoryFetcher = repositoryFetcher;
        this.repositoryMapper = repositoryMapper;
    }

    public List<RepositoryWithDetailsDto> getNonForkRepositoriesWithBranches(String acceptHeader, String username) {
        acceptHeaderValidator.validateAcceptHeader(acceptHeader);
        List<RepositoryDto> allRepositories = repositoryFetcher.fetchAllRepositories(username);

        return allRepositories.stream()
                .filter(repository -> !repository.isFork())
                .map(repository -> {
                    List<BranchDto> branches = repositoryFetcher.fetchBranchesAndCommits(repository, username);
                    return repositoryMapper.mapToRepositoryFinalResponse(repository, username, branches);
                })
                .toList();
    }
}
