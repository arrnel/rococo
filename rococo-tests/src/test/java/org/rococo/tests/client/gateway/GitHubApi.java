package org.rococo.tests.client.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface GitHubApi {

    @GET("/repos/{owner}/{repo}/issues/{issue_number}")
    @Headers({
            "Accept: application/vnd.github+json"
    })
    Call<JsonNode> getIssue(@Header("User-Agent") String userAgent,
                            @Header("Authorization") String bearerToken,
                            @Path("owner") String owner,
                            @Path("repo") String repo,
                            @Path("issue_number") String issueNumber);

}
