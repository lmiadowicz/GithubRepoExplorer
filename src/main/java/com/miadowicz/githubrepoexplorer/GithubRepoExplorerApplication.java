package com.miadowicz.githubrepoexplorer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GithubRepoExplorerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GithubRepoExplorerApplication.class, args);
    }
}
