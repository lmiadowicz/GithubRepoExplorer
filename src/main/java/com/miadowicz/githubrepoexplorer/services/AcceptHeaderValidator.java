package com.miadowicz.githubrepoexplorer.services;

import com.miadowicz.githubrepoexplorer.exceptions.UnsupportedAcceptHeaderException;
import org.springframework.stereotype.Component;

@Component
public class AcceptHeaderValidator {
    private static final String SUPPORTED_MEDIA_TYPE = "application/json";

    public void validateAcceptHeader(String acceptHeader) {
        if (!SUPPORTED_MEDIA_TYPE.equals(acceptHeader)) {
            throw new UnsupportedAcceptHeaderException(
                    String.format("Unsupported accept header: %s", acceptHeader),
                    acceptHeader
            );
        }
    }
}