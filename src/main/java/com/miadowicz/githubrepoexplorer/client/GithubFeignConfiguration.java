package com.miadowicz.githubrepoexplorer.client;

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
    public RequestInterceptor requestInterceptor(/*@Value("${GITHUB_API_TOKEN}") String githubApiToken*/) {
        return template -> {
          String githubApiToken = "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";
            template.header("Authorization", "token %s".formatted(githubApiToken));
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