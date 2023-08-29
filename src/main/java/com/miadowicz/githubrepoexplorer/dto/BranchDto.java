package com.miadowicz.githubrepoexplorer.dto;

public record BranchDto(
        String name,
        CommitDto commit
) {
}
