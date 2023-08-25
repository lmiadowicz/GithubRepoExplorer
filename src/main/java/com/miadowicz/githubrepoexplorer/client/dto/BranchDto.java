package com.miadowicz.githubrepoexplorer.client.dto;

public record BranchDto(
        String name,
        CommitDto commit
) {
}
