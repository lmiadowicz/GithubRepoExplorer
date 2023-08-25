package com.miadowicz.githubrepoexplorer.client.dto;

import java.util.List;

public record RepositoryWithDetailsDto(
        String repositoryName,
        String owner,
        List<BranchDetailsDto> branches
) {
}
