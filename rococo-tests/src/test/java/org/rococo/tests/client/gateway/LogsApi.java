package org.rococo.tests.client.gateway;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface LogsApi {

    @GET("/api/logs/service/{serviceName}")
    Call<ResponseBody> getServiceLogs(@Path("serviceName") String serviceName);

    @GET("/api/logs/service")
    Call<ResponseBody> getAllServicesLogs();

    @DELETE("/api/logs/service")
    Call<Void> clearLogs();

}
