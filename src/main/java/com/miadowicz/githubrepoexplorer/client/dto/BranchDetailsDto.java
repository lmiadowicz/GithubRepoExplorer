package com.miadowicz.githubrepoexplorer.client.dto;

public record BranchDetailsDto(
        String branchName,
        String lastCommitSha
) {
}
