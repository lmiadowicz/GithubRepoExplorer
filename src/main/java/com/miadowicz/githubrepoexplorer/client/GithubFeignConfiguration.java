package com.miadowicz.githubrepoexplorer.client;

import com.miadowicz.githubrepoexplorer.exceptions.InvalidGitHubApiTokenException;
import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Response;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GithubFeignConfiguration {
    private static final int CONNECT_TIMEOUT_MILLIS = 1000;
    private static final int READ_TIMEOUT_MILLIS = 1000;
    @Bean
    public Decoder decoder() {
        return new JacksonDecoder();
    }

    @Bean
    public RequestInterceptor requestInterceptor(@Value("${gh.api.token}") String githubApiToken) {
        if (githubApiToken == null || githubApiToken.isEmpty()) {
            // Throw custom exception if the GitHub API token is null or empty
            throw new InvalidGitHubApiTokenException("GitHub API token must not be null or empty");
        }

        return template -> {
            template.header("Authorization", String.format("token %s", githubApiToken));
        };
    }
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
    @Bean
    public feign.Client feignClient() {
        return new feign.Client.Default(null, null) {
            @Override
            public Response execute(Request request, Request.Options options) throws IOException {
                options = new Request.Options(CONNECT_TIMEOUT_MILLIS, READ_TIMEOUT_MILLIS);
                return super.execute(request, options);
            }
        };
    }
}