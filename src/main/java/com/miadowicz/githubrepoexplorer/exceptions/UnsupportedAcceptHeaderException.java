package com.miadowicz.githubrepoexplorer.exceptions;

import org.springframework.web.HttpMediaTypeNotAcceptableException;

public class UnsupportedAcceptHeaderException extends HttpMediaTypeNotAcceptableException {
    public UnsupportedAcceptHeaderException(String message) {
        super(message);
    }
}