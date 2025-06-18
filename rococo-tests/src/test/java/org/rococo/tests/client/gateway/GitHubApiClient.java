package org.rococo.tests.client.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Step;
import org.apache.commons.lang3.EnumUtils;
import org.rococo.tests.client.gateway.core.RestClient;
import org.rococo.tests.enums.HttpStatus;
import org.rococo.tests.ex.InvalidGitHubCredentials;
import org.rococo.tests.ex.UnknownIssueStatusException;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class GitHubApiClient extends RestClient {

    private static final String GITHUB_TOKEN = System.getenv("GITHUB_TOKEN");
    private static final String GITHUB_TOKEN_NAME = System.getenv("GITHUB_TOKEN_NAME");

    private final GitHubApi gitHubApi;

    public GitHubApiClient() {
        super(CFG.gitHubUrl());
        this.gitHubApi = retrofit.create(GitHubApi.class);
        checkCredentials();
    }

    @Nonnull
    @Step("[API] Send get issue state request. GET: [github-api]/repos/arrnel/rococo/issues/{issueId}")
    public IssueState getIssueState(String issueId) {
        final Response<JsonNode> response;
        try {
            response = gitHubApi.getIssue(
                            GITHUB_TOKEN_NAME,
                            "Bearer " + GITHUB_TOKEN,
                            issueId
                    )
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.OK, response.code());

        var issueStatus = Objects.requireNonNull(response.body()).get("state").asText();
        return Optional.ofNullable(
                        EnumUtils.getEnumIgnoreCase(IssueState.class, issueStatus.toUpperCase())
                )
                .orElseThrow(() -> new UnknownIssueStatusException(issueStatus));
    }

    private void checkCredentials() {

        if (GITHUB_TOKEN == null)
            throw new InvalidGitHubCredentials("GITHUB_TOKEN not set");

        if (GITHUB_TOKEN_NAME == null)
            throw new InvalidGitHubCredentials("GITHUB_TOKEN_NAME not set");

    }

    public enum IssueState {
        OPEN, CLOSED
    }

}
