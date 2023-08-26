package com.miadowicz.githubrepoexplorer.feature;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasSize;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GithubFeignClientIntegrationTest.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(WireMockExtension.class)
public class GithubFeignClientIntegrationTest {

    private static final String WIRE_MOCK_HOST = "http://localhost";

    @Autowired
    private MockMvc mockMvc;

    @RegisterExtension
    public static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort()
                    .withRootDirectory("src/integration/resources"))
            .build();

    @DynamicPropertySource
    public static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.openfeign.client.config.github.url", () -> WIRE_MOCK_HOST + ":" + wireMockServer.getPort());
    }

    @Test
    void testValidGetRepositories() throws Exception {
        // Stub the Feign client's responses
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/users/dawidkorybalski/repos"))
                .withQueryParam("page", WireMock.equalTo("1"))
                .withQueryParam("perPage", WireMock.equalTo("100"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("repos-page1.json")));

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/users/dawidkorybalski/repos"))
                .withQueryParam("page", WireMock.equalTo("2"))
                .withQueryParam("perPage", WireMock.equalTo("100"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/repos/dawidkorybalski/dawidkorybalski/branches"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/repos/dawidkorybalski/python-sample-vscode-flask-tutorial/branches"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("branch-page1.json")));


        mockMvc.perform(get("/users/dawidkorybalski/repositories")
                        .header("Accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].repositoryName").value("dawidkorybalski"))
                .andExpect(jsonPath("$[0].owner").value("dawidkorybalski"))
                .andExpect(jsonPath("$[0].branches").isArray())
                .andExpect(jsonPath("$[0].branches", hasSize(0)))
                .andExpect(jsonPath("$[1].repositoryName").value("python-sample-vscode-flask-tutorial"))
                .andExpect(jsonPath("$[1].owner").value("dawidkorybalski"))
                .andExpect(jsonPath("$[1].branches").isArray())
                .andExpect(jsonPath("$[1].branches", hasSize(3)));


        // Verify that the expected requests were made to the WireMock server
        wireMockServer.verify(2, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/users/dawidkorybalski/repos")));
        wireMockServer.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/repos/dawidkorybalski/dawidkorybalski/branches")));
        wireMockServer.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/repos/dawidkorybalski/python-sample-vscode-flask-tutorial/branches")));

    }

    @Test
    void testInvalidHeader() throws Exception {
        wireMockServer.stubFor(WireMock.get(urlPathMatching("/users/test-user/repos"))
                .willReturn(aResponse()
                        .withStatus(406)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("invalid-header.json")));

        mockMvc.perform(get("/users/test-user/repositories")
                        .header("Accept", "application/xml"))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.status").value(406))
                .andExpect(jsonPath("$.message").value("Unsupported accept header: application/xml"));
    }

    @Test
    void testUserNotFoundException() throws Exception {
        WireMock.stubFor(WireMock.get(urlPathMatching("/users/test-user/repos"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("user-not-found.json")));
        System.out.println(WireMock.listAllStubMappings());
        mockMvc.perform(get("/users/test-user/repositories")
                        .header("Accept", "application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User test-user not found."));
    }


}