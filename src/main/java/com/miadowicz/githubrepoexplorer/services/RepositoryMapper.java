package com.miadowicz.githubrepoexplorer.services;

import com.miadowicz.githubrepoexplorer.client.dto.BranchDetailsDto;
import com.miadowicz.githubrepoexplorer.client.dto.BranchDto;
import com.miadowicz.githubrepoexplorer.client.dto.CommitDto;
import com.miadowicz.githubrepoexplorer.client.dto.RepositoryWithDetailsDto;
import com.miadowicz.githubrepoexplorer.client.dto.RepositoryDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RepositoryMapper {
    public RepositoryWithDetailsDto mapToRepositoryFinalResponse(RepositoryDto repository, String username, List<BranchDto> branches) {
        List<BranchDetailsDto> branchDetailsList = branches.stream()
                .map(branch -> {
                    CommitDto commit = branch.commit();
                    if (commit != null) {
                        return new BranchDetailsDto(branch.name(), commit.sha());
                    } else {
                        // Handle the case when commit is null
                        return new BranchDetailsDto(branch.name(), null);
                    }
                })
                .collect(Collectors.toList());

        return new RepositoryWithDetailsDto(
                repository.name(),
                username,
                branchDetailsList
        );
    }
}