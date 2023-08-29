package com.miadowicz.githubrepoexplorer.dto;

import java.util.List;

public record RepositoryDto(
        String name,
        OwnerDto owner,
        boolean isFork,
        List<BranchDetailsDto> branches
) {
}
