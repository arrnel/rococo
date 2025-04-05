package org.rococo.tests.client.gateway;

import io.qameta.allure.Step;
import okhttp3.logging.HttpLoggingInterceptor;
import org.rococo.tests.client.gateway.core.RestClient;
import org.rococo.tests.ex.*;
import org.rococo.tests.model.RestPage;
import org.rococo.tests.model.UserDTO;
import org.springframework.data.domain.Page;
import retrofit2.Response;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Optional;

import static org.rococo.tests.enums.HttpStatus.*;

@ParametersAreNonnullByDefault
public class UsersApiClient extends RestClient {

    private final UsersApi usersApi;

    public UsersApiClient() {
        super(CFG.gatewayUrl(), HttpLoggingInterceptor.Level.HEADERS);
        this.usersApi = create(UsersApi.class);
    }

    @Step("Send request POST:[rococo-users]/api/user")
    public UserDTO createUser(String bearerToken,
                              UserDTO requestBody
    ) {

        var call = usersApi.createUser(bearerToken, requestBody);
        Response<UserDTO> response = null;

        try {
            response = call.execute();
            if (response.isSuccessful()) return response.body();

            var method = call.request().method();
            var path = call.request().url().encodedPath();
            var message = response.errorBody() != null
                    ? response.errorBody().string()
                    : "";

            throw switch (response.code()) {
                case BAD_REQUEST -> new BadRequestException(method, path, message);
                case UNAUTHORIZED -> new UnauthorizedException(method, path);
                case CONFLICT -> new UserAlreadyExistException(requestBody.getUsername());
                default -> new ServiceUnavailableException(SERVICE_NAME, call, response.code(), message);
            };

        } catch (IOException ex) {
            throw unknownException(response, call, ex);
        }

    }

    @Step("Send request GET:[rococo-users]/api/user")
    public Optional<UserDTO> currentUser(String bearerToken) {

        var call = usersApi.currentUser(bearerToken);
        Response<UserDTO> response = null;

        try {
            response = call.execute();
            return switch (response.code()) {
                case OK -> Optional.ofNullable(response.body());
                case NOT_FOUND -> Optional.empty();
                default -> {
                    var message = response.errorBody() != null
                            ? response.errorBody().string()
                            : "";
                    throw new ServiceUnavailableException(SERVICE_NAME, call, response.code(), message);
                }
            };

        } catch (IOException ex) {
            throw unknownException(response, call, ex);
        }
    }

    @Step("Send request GET:[rococo-users]/api/user/all")
    public Page<UserDTO> findAll(int page, int size) {
        var call = usersApi.findAll(page, size);
        Response<RestPage<UserDTO>> response = null;

        try {
            response = call.execute();
            if (response.isSuccessful())
                return response.body();
            var message = response.errorBody() != null
                    ? response.errorBody().string()
                    : "";
            throw new ServiceUnavailableException(SERVICE_NAME, call, response.code(), message);
        } catch (IOException ex) {
            throw unknownException(response, call, ex);
        }
    }

    @Step("Send request PATCH:[rococo-users]/api/user")
    public UserDTO updateUser(String bearerToken, UserDTO requestBody) {

        var call = usersApi.updateUser(bearerToken, requestBody);
        Response<UserDTO> response = null;

        try {
            response = call.execute();
            if (response.isSuccessful()) return response.body();

            var method = call.request().method();
            var path = call.request().url().encodedPath();
            var message = response.errorBody() != null
                    ? response.errorBody().string()
                    : "";

            throw switch (response.code()) {
                case BAD_REQUEST -> new BadRequestException(method, path, message);
                case UNAUTHORIZED -> new UnauthorizedException(method, path);
                case NOT_FOUND -> new ArtistNotFoundException(requestBody.getId());
                case CONFLICT -> new ArtistAlreadyExistException(requestBody.getUsername());
                default -> new ServiceUnavailableException(SERVICE_NAME, call, response.code(), message);
            };

        } catch (IOException ex) {
            throw unknownException(response, call, ex);
        }

    }
}
