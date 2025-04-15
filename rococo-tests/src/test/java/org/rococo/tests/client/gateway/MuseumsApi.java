package org.rococo.tests.client.gateway;

import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.model.RestPage;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface MuseumsApi {

    @POST("/api/museum")
    Call<MuseumDTO> add(@Header("Authorization") String bearerToken,
                        @Body MuseumDTO museum);

    @GET("/api/museum/{id}")
    Call<MuseumDTO> findById(@Path("id") UUID id);

    @GET("/api/museum")
    Call<RestPage<MuseumDTO>> findAll(@Query("page") int page,
                                      @Query("size") int size);

    @GET("/api/museum")
    Call<RestPage<MuseumDTO>> findAll(@Query("title") String name,
                                      @Query("page") int page,
                                      @Query("size") int size);

    @PATCH("/api/museum")
    Call<MuseumDTO> update(@Header("Authorization") String bearerToken,
                           @Body MuseumDTO museum);

    @DELETE("/api/museum/{id}")
    Call<Void> delete(@Header("Authorization") String bearerToken,
                      @Path("id") UUID id);

}
