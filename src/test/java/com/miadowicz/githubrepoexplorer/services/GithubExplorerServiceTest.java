package com.miadowicz.githubrepoexplorer.services;

import com.miadowicz.githubrepoexplorer.client.dto.BranchDto;
import com.miadowicz.githubrepoexplorer.client.dto.CommitDto;
import com.miadowicz.githubrepoexplorer.client.dto.OwnerDto;
import com.miadowicz.githubrepoexplorer.client.dto.RepositoryWithDetailsDto;
import com.miadowicz.githubrepoexplorer.client.dto.RepositoryDto;
import com.miadowicz.githubrepoexplorer.exceptions.UnsupportedAcceptHeaderException;
import com.miadowicz.githubrepoexplorer.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubExplorerServiceTest {

    @Mock
    private RepositoryFetcher repositoryFetcher;

    @Mock
    private RepositoryMapper repositoryMapper;

    @Mock
    private AcceptHeaderValidator acceptHeaderValidator;

    @InjectMocks
    private GithubExplorerService githubExplorerService;

    @Test
    void should_fetch_non_fork_repositories_with_branches_success() {
        // Arrange
        String acceptHeader = "application/json";
        String username = "test-user";
        RepositoryDto repositoryResponseDto = new RepositoryDto("repo1", new OwnerDto(username), false, null);
        List<RepositoryDto> repositories = Collections.singletonList(repositoryResponseDto);
        BranchDto branchDto = new BranchDto("branch1", new CommitDto("sha1"));

        when(repositoryFetcher.fetchAllRepositories(username)).thenReturn(repositories);
        when(repositoryFetcher.fetchBranchesAndCommits(eq(repositoryResponseDto), eq(username))).thenReturn(Collections.singletonList(branchDto));
        when(repositoryMapper.mapToRepositoryFinalResponse(any(), eq(username), any())).thenReturn(new RepositoryWithDetailsDto("repo1", username, null));

        // Act
        List<RepositoryWithDetailsDto> result = githubExplorerService.getNonForkRepositoriesWithBranches(acceptHeader, username);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).repositoryName()).isEqualTo("repo1");
        assertThat(result.get(0).owner()).isEqualTo(username);

        // Verify the interaction
        verify(repositoryFetcher).fetchBranchesAndCommits(eq(repositoryResponseDto), eq(username));
    }

    @Test
    void should_throw_exception_for_invalid_accept_header() {

        String acceptHeader = "application/xml";
        String username = "test-user";
        doThrow(new UnsupportedAcceptHeaderException("Unsupported accept header", acceptHeader)).when(acceptHeaderValidator).validateAcceptHeader(acceptHeader);


        assertThatThrownBy(() -> githubExplorerService.getNonForkRepositoriesWithBranches(acceptHeader, username))
                .isInstanceOf(UnsupportedAcceptHeaderException.class);
    }

    @Test
    void should_throw_exception_for_user_not_found() {

        String acceptHeader = "application/json";
        String username = "nonexistent-user";
        when(repositoryFetcher.fetchAllRepositories(username)).thenThrow(UserNotFoundException.class);


        assertThatThrownBy(() -> githubExplorerService.getNonForkRepositoriesWithBranches(acceptHeader, username))
                .isInstanceOf(UserNotFoundException.class);
    }
}