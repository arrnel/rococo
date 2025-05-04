package org.rococo.tests.client.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;
import org.rococo.tests.client.gateway.core.RestClient;
import org.rococo.tests.enums.HttpStatus;
import org.rococo.tests.model.allure.AllureProject;
import org.rococo.tests.model.allure.AllureResults;
import retrofit2.Response;
import retrofit2.http.Query;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ParametersAreNonnullByDefault
public class AllureDockerApiClient extends RestClient {

    private final AllureDockerApi allureDockerApi;

    public AllureDockerApiClient() {
        super(CFG.allureDockerUrl(), HttpLoggingInterceptor.Level.NONE);
        this.allureDockerApi = create(AllureDockerApi.class);
    }

    public void uploadResults(String projectId,
                              AllureResults results) {
        final Response<JsonNode> response;
        try {
            response = allureDockerApi.uploadResults(
                    projectId,
                    results
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.OK, response.code());
    }

    public void createProjectIfNotExist(String projectId) {
        int code;
        try {
            code = allureDockerApi.project(
                    projectId
            ).execute().code();
            if (code == HttpStatus.NOT_FOUND) {
                code = allureDockerApi.createProject(
                        new AllureProject(
                                projectId
                        )
                ).execute().code();
                assertEquals(HttpStatus.CREATED, code);
            } else {
                assertEquals(HttpStatus.OK, code);
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public void cleanResults(String projectId) {
        final Response<JsonNode> response;
        try {
            response = allureDockerApi.cleanResults(
                    projectId
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.OK, response.code());
    }

    public void generateReport(@Query("project_id") String projectId) {
        final Response<JsonNode> response;
        try {
            response = allureDockerApi.generateReport(
                    projectId,
                    System.getenv("HEAD_COMMIT_MESSAGE"),
                    System.getenv("BUILD_URL"),
                    System.getenv("EXECUTION_TYPE")
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.OK, response.code());
    }
}
