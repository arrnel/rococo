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

    @GET("/repos/arrnel/rococo/issues/{issue_number}")
    @Headers({
            "Accept: application/vnd.github+json"
    })
    Call<JsonNode> getIssue(@Header("User-Agent") String userAgent,
                            @Header("Authorization") String bearerToken,
                            @Path("issue_number") String issueNumber);

}
