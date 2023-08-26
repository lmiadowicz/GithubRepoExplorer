package com.miadowicz.githubrepoexplorer.services;

import com.miadowicz.githubrepoexplorer.client.dto.BranchDetailsDto;
import com.miadowicz.githubrepoexplorer.client.dto.BranchDto;
import com.miadowicz.githubrepoexplorer.client.dto.CommitDto;
import com.miadowicz.githubrepoexplorer.client.dto.RepositoryWithDetailsDto;
import com.miadowicz.githubrepoexplorer.client.dto.RepositoryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RepositoryMapper {
    public RepositoryWithDetailsDto mapToRepositoryFinalResponse(RepositoryDto repository, String username, List<BranchDto> branches) {
        log.debug("Mapping repository {} for user {}", repository.name(), username);

        List<BranchDetailsDto> branchDetailsList = branches.stream()
                .map(branch -> {
                    CommitDto commit = branch.commit();
                    if (commit != null) {
                        return new BranchDetailsDto(branch.name(), commit.sha());
                    } else {
                        return new BranchDetailsDto(branch.name(), null);
                    }
                })
                .collect(Collectors.toList());

        log.debug("Mapped {} branches for repository {}", branchDetailsList.size(), repository.name());

        return new RepositoryWithDetailsDto(
                repository.name(),
                username,
                branchDetailsList
        );
    }
}