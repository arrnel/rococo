package org.rococo.tests.client.gateway;

import io.qameta.allure.Step;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.rococo.tests.client.gateway.core.RestClient;
import org.rococo.tests.model.ServiceName;
import org.rococo.tests.model.allure.logService.AllureHistoryDTO;
import retrofit2.Call;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@ParametersAreNonnullByDefault
public class LogsApiClient extends RestClient {

    private static final String ALL_SERVICES_LOG_FILENAME = "rococo_all_services_logs";
    private static final String ARCHIVE_EXT = ".zip";
    private static final String LOG_EXT = ".log";

    private final LogsApi logsApi;

    public LogsApiClient() {
        super(CFG.logsUrl(), HttpLoggingInterceptor.Level.BODY);
        this.logsApi = create(LogsApi.class);
    }

    @Step("Send request GET:[rococo-logs]/api/logs/service/{{service_name}}")
    public File downloadServiceLogs(ServiceName serviceName) {
        var name = serviceName.getServiceName();
        return downloadLogFile(logsApi.getServiceLogs(name), name, LOG_EXT);
    }

    @Step("Send request GET:[rococo-logs]/api/logs/service/all")
    public File downloadAllServicesLogs() {
        return downloadLogFile(logsApi.getAllServicesLogs(), ALL_SERVICES_LOG_FILENAME, ARCHIVE_EXT);
    }

    @Step("Send request DELETE:[rococo-logs]/api/logs/service")
    public void clearLogs() {
        logsApi.clearLogs();
    }

    private File downloadLogFile(Call<ResponseBody> call, String fileName, String suffix) {

        try {

            var response = call.execute();
            if (!response.isSuccessful())
                throw new IOException("Failed to download logs. Unexpected status code: " + response.code());

            try (var responseBody = response.body()) {
                if (responseBody == null)
                    throw new IOException("Response body is null");

                Path downloadPath = Files.createTempFile(fileName, suffix);

                try (InputStream is = responseBody.byteStream()) {
                    Files.copy(is, downloadPath, StandardCopyOption.REPLACE_EXISTING);
                    return downloadPath.toFile();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Unknown error while downloading logs.", ex);
        }

    }

}
