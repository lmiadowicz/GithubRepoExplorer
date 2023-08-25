package com.miadowicz.githubrepoexplorer.exceptions;

public class InvalidGitHubApiTokenException extends RuntimeException {
    public InvalidGitHubApiTokenException(String message) {
        super(message);
    }
}