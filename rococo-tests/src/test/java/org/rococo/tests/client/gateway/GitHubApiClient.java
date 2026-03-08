package org.rococo.tests.client.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Allure;
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

    private static final String GH_TOKEN = System.getenv("GH_TOKEN");
    private static final String GH_TOKEN_NAME = System.getenv("GH_TOKEN_NAME");
    private static final String GH_REPO = System.getenv("GH_REPO_NAME");
    private static final String GH_OWNER = System.getenv("GH_ACCOUNT_NAME");

    private final GitHubApi gitHubApi;

    public GitHubApiClient() {
        super(CFG.gitHubUrl());
        this.gitHubApi = retrofit.create(GitHubApi.class);
        checkCredentials();
    }

    @Nonnull
    public IssueState getIssueState(String issueId) {

        return Allure.step(

                "[API] Send get issue state request. GET: [github-api]/repos/%s/%s/issues/%s"
                        .formatted(GH_OWNER, GH_REPO, issueId),

                () -> {
                    final Response<JsonNode> response;
                    try {
                        response = gitHubApi.getIssue(
                                        GH_TOKEN_NAME,
                                        "Bearer " + GH_TOKEN,
                                        GH_OWNER,
                                        GH_REPO,
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

                });

    }

    private void checkCredentials() {

        if (GH_TOKEN == null)
            throw new InvalidGitHubCredentials("GH_TOKEN not set");

        if (GH_TOKEN_NAME == null)
            throw new InvalidGitHubCredentials("GH_TOKEN_NAME not set");

    }

    public enum IssueState {
        OPEN, CLOSED
    }

}
