package com.miadowicz.githubrepoexplorer.services;

import com.miadowicz.githubrepoexplorer.exceptions.UnsupportedAcceptHeaderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AcceptHeaderValidator {
    private static final MediaType SUPPORTED_MEDIA_TYPE = MediaType.APPLICATION_JSON;

    public void validateAcceptHeader(String acceptHeader) {
        List<MediaType> acceptableMediaTypes = MediaType.parseMediaTypes(acceptHeader);
        boolean isAcceptable = acceptableMediaTypes.stream()
                .anyMatch(mediaType -> mediaType.isCompatibleWith(SUPPORTED_MEDIA_TYPE));

        if (!isAcceptable) {
            log.error("Unsupported accept header: {}", acceptHeader);
            throw new UnsupportedAcceptHeaderException(
                    String.format("Unsupported accept header: %s", acceptHeader),
                    acceptHeader
            );
        }
    }
}