package org.rococo.tests.client.gateway;

import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.model.RestPage;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface PaintingsApi {

    @POST("/api/painting")
    Call<PaintingDTO> add(@Header("Authorization") String bearerToken,
                          @Body PaintingDTO painting);

    @GET("/api/painting/{id}")
    Call<PaintingDTO> findById(@Path("id") UUID id);

    @GET("/api/painting")
    Call<RestPage<PaintingDTO>> findAll(@Query("page") int page,
                                        @Query("size") int size);

    @GET("/api/painting")
    Call<RestPage<PaintingDTO>> findAll(@Query("title") String name,
                                        @Query("page") int page,
                                        @Query("size") int size);

    @GET("/api/painting")
    Call<RestPage<PaintingDTO>> findAll(@Query("authorId") UUID artistId,
                                        @Query("page") int page,
                                        @Query("size") int size);

    @PATCH("/api/painting")
    Call<PaintingDTO> update(@Header("Authorization") String bearerToken,
                             @Body PaintingDTO painting);

    @DELETE("/api/painting/{id}")
    Call<Void> delete(@Header("Authorization") String bearerToken,
                      @Path("id") UUID id);

}
