package com.miadowicz.githubrepoexplorer.dto;

public record BranchDetailsDto(
        String branchName,
        String lastCommitSha
) {
}
