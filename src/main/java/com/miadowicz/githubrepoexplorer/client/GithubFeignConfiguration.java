package com.miadowicz.githubrepoexplorer.client;

import com.miadowicz.githubrepoexplorer.exceptions.InvalidGitHubApiTokenException;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class GithubFeignConfiguration {

    @Bean
    public Decoder decoder() {
        return new JacksonDecoder();
    }

    @Bean
    public RequestInterceptor requestInterceptor(@Value("${gh.api.token}") String githubApiToken) {
        if (githubApiToken == null || githubApiToken.isEmpty()) {
            // Throw custom exception if the GitHub API token is null or empty
            log.error("GitHub API token is null or empty");
            throw new InvalidGitHubApiTokenException("GitHub API token must not be null or empty");
        }
        log.info("Configuring RequestInterceptor with GitHub API token");
        return template ->
            template.header("Authorization", String.format("token %s", githubApiToken));
    }
}