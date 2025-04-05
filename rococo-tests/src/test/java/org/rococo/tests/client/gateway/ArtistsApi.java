package org.rococo.tests.client.gateway;

import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.model.RestPage;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface ArtistsApi {

    @POST("/api/artist")
    Call<ArtistDTO> add(@Header("Authorization") String bearerToken,
                        @Body ArtistDTO artist);

    @GET("/api/artist/{id}")
    Call<ArtistDTO> findById(@Path("id") UUID id);

    @GET("/api/artist")
    Call<RestPage<ArtistDTO>> findAll(@Query("page") int page,
                                      @Query("size") int size);

    @GET("/api/artist")
    Call<RestPage<ArtistDTO>> findAll(@Query("name") String name,
                                      @Query("page") int page,
                                      @Query("size") int size);

    @PATCH("/api/artist")
    Call<ArtistDTO> update(@Header("Authorization") String bearerToken,
                           @Body ArtistDTO artist);

    @DELETE("/api/artist/{id}")
    Call<Void> delete(@Header("Authorization") String bearerToken,
                      @Path("id") UUID id);

}
