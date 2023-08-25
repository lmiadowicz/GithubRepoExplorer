package com.miadowicz.githubrepoexplorer.exceptions;

public class UnsupportedAcceptHeaderException extends RuntimeException {

    private final String acceptHeaderValue;

    public UnsupportedAcceptHeaderException(String acceptHeaderValue, String acceptHeader) {
        this.acceptHeaderValue = acceptHeaderValue;
    }

    public String getAcceptHeaderValue() {
        return acceptHeaderValue;
    }
}