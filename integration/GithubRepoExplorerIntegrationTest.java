import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.miadowicz.githubrepoexplorer.GithubRepoExplorerApplication;
import com.miadowicz.githubrepoexplorer.dto.RepositoryWithDetailsDto;
import com.miadowicz.githubrepoexplorer.exceptions.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = GithubRepoExplorerApplication.class)
@ActiveProfiles("test")
@ExtendWith(WireMockExtension.class)
@AutoConfigureWebTestClient
public class GithubRepoExplorerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final String WIRE_MOCK_HOST = "http://localhost";

    @RegisterExtension
    public static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort()
                    .withRootDirectory("integration/resources"))
            .build();

    @DynamicPropertySource
    public static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.openfeign.client.config.github.url", () -> WIRE_MOCK_HOST + ":" + wireMockServer.getPort());
    }

    @Test
    void should_test_valid_get_repositories_with_branches_and_last_commit_sha() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathMatching("/users/dawidkorybalski/repos"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("repos.json")));

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/repos/dawidkorybalski/dawidkorybalski/branches"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/repos/dawidkorybalski/python-sample-vscode-flask-tutorial/branches"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("branches.json")));
        webTestClient
                .get()
                .uri("/users/dawidkorybalski/repositories")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepositoryWithDetailsDto.class)
                .consumeWith(response -> {
                    List<RepositoryWithDetailsDto> responseBody = response.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.get(0).owner()).isEqualTo("dawidkorybalski");
                    assertThat(responseBody.get(0).repositoryName()).isEqualTo("dawidkorybalski");
                    assertThat(responseBody.get(1).owner()).isEqualTo("dawidkorybalski");
                    assertThat(responseBody.get(1).repositoryName()).isEqualTo("python-sample-vscode-flask-tutorial");
                    assertThat(responseBody.get(1).branches().get(0).branchName()).isEqualTo("master");
                    assertThat(responseBody.get(1).branches().get(0).lastCommitSha()).isEqualTo("c49c02d17ce0631f4fc1e1d87f4de69deca3ec26");
                    assertThat(responseBody.get(1).branches().get(1).branchName()).isEqualTo("startup-file");
                    assertThat(responseBody.get(1).branches().get(1).lastCommitSha()).isEqualTo("2b0134a2332a557f8a5115fcd12485deada62cae");
                    assertThat(responseBody.get(1).branches().get(2).branchName()).isEqualTo("tutorial");
                    assertThat(responseBody.get(1).branches().get(2).lastCommitSha()).isEqualTo("171e35753b2cc1c7cf78c0f9904dc9e111449d9a");
                });
    }
    @Test
    void should_test_empty_repositories() {
        wireMockServer.stubFor(WireMock.get(urlPathMatching("/users/dawidkorybalski/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("repos-empty.json")));

        webTestClient
                .get()
                .uri("/users/dawidkorybalski/repositories")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepositoryWithDetailsDto.class)
                .consumeWith(response -> {
                    List<RepositoryWithDetailsDto> responseBody = response.getResponseBody();

                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody).isEqualTo(Collections.emptyList());
                });
    }
    @Test
    void should_test_invalid_header_exception() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathMatching("/users/test-user/repos"))
                .willReturn(WireMock.aResponse()
                        .withStatus(406)
                        .withHeader("Content-Type", "application/xml")
                        .withBodyFile("invalid-header.json")));


        webTestClient.get()
                .uri("/users/test-user/repositories")
                .accept(MediaType.APPLICATION_XML)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE) // 406
                .expectBody(ErrorResponse.class)
                .consumeWith(response -> {
                    ErrorResponse errorResponse = response.getResponseBody();
                    assertThat(errorResponse).isNotNull();
                    assertThat(errorResponse.status()).isEqualTo(406);
                    assertThat(errorResponse.message()).isEqualTo("Accept header must be application/json");
                });
    }

    @Test
    void should_test_user_not_found_exception() {
        wireMockServer.stubFor(WireMock.get(urlPathMatching("/users/test-useradsfasdfasdf/repositories"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("user-not-found.json")));
        webTestClient.get()
                .uri("/users/test-useradsfasdfasdf/repositories")
                .header("Accept", "application/json")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)  // Change to expectBody(ErrorResponse.class)
                .consumeWith(response -> {
                    ErrorResponse errorResponse = response.getResponseBody();
                    assertThat(errorResponse).isNotNull();
                    assertThat(errorResponse.status()).isEqualTo(404);
                    assertThat(errorResponse.message()).isEqualTo("User test-useradsfasdfasdf not found");
                });

    }
}