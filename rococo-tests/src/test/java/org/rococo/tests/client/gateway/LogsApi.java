package org.rococo.tests.client.gateway;

import okhttp3.ResponseBody;
import org.rococo.tests.model.allure.logService.TestsStatDTO;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface LogsApi {

    @GET("/api/logs/service/{serviceName}")
    Call<ResponseBody> getServiceLogs(@Path("serviceName") String serviceName);

    @GET("/api/logs/service")
    Call<ResponseBody> getAllServicesLogs();

    @DELETE("/api/logs/service")
    Call<Void> clearLogs();

    @POST("/api/stat/tests")
    Call<ResponseBody> addNewTestsStat(@Body TestsStatDTO testsStat);

}
