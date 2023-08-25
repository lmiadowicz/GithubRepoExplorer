package com.miadowicz.githubrepoexplorer.client.dto;

import java.util.List;

public record RepositoryDto(
        String name,
        OwnerDto owner,
        boolean isFork,
        List<BranchDetailsDto> branches
) {
}
