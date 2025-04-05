package org.rococo.tests.client.gateway;

import org.rococo.tests.model.RestPage;
import org.rococo.tests.model.UserDTO;
import retrofit2.Call;
import retrofit2.http.*;

public interface UsersApi {

    @POST("/api/user")
    Call<UserDTO> createUser(@Header("Authorization") String bearerToken,
                             @Body UserDTO requestDTO);

    @GET("/api/user")
    Call<UserDTO> currentUser(@Header("Authorization") String bearerToken);

    @GET("/api/user/all")
    Call<RestPage<UserDTO>> findAll(@Query("page") int page,
                                    @Query("size") int size);

    @PATCH("/api/user")
    Call<UserDTO> updateUser(@Header("Authorization") String bearerToken,
                             @Body UserDTO requestDTO);

}
