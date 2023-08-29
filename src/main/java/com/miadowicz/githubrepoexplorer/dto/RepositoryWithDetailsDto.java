package com.miadowicz.githubrepoexplorer.dto;

import java.util.List;

public record RepositoryWithDetailsDto(
        String repositoryName,
        String owner,
        List<BranchDetailsDto> branches
) {
}
