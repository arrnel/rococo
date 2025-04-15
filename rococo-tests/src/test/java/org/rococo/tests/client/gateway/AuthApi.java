package org.rococo.tests.client.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface AuthApi {

    @GET("/login")
    Call<ResponseBody> getCookies();

    @FormUrlEncoded
    @POST("/register")
    Call<ResponseBody> register(
            @Field("username") String username,
            @Field("password") String password,
            @Field("passwordSubmit") String passwordConfirmation,
            @Field("_csrf") String csrf
    );

    @GET("/oauth2/authorize")
    Call<Void> authorize(
            @Query("response_type") String responseType,
            @Query("client_id") String clientId,
            @Query("scope") String scope,
            @Query(value = "redirect_uri", encoded = true) String redirectUri,
            @Query("code_challenge") String codeChallenge,
            @Query("code_challenge_method") String codeChallengeMethod);


    @POST("/login")
    @FormUrlEncoded
    Call<Void> login(
            @Field("username") String username,
            @Field("password") String password,
            @Field("_csrf") String csrf);

    @POST("/oauth2/token")
    @FormUrlEncoded
    Call<JsonNode> token(
            @Field("client_id") String clientId,
            @Field(value = "redirect_uri", encoded = true) String redirectUri,
            @Field("grant_type") String grantType,
            @Field("code") String code,
            @Field("code_verifier") String codeVerifier);

}