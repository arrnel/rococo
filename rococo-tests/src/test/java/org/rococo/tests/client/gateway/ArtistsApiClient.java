package org.rococo.tests.client.gateway;

import io.qameta.allure.Step;
import okhttp3.logging.HttpLoggingInterceptor;
import org.rococo.tests.client.gateway.core.RestClient;
import org.rococo.tests.ex.*;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.model.RestPage;
import org.springframework.data.domain.Page;
import retrofit2.Response;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.rococo.tests.enums.HttpStatus.*;

@ParametersAreNonnullByDefault
public class ArtistsApiClient extends RestClient {

    private static final String SERVICE_NAME = "rococo-gateway";

    private final ArtistsApi artistsApi;

    public ArtistsApiClient() {
        super(CFG.gatewayUrl(), HttpLoggingInterceptor.Level.HEADERS);
        this.artistsApi = create(ArtistsApi.class);
    }

    @Step("Send request POST:[rococo-artists]/api/artist")
    public ArtistDTO add(String bearerToken,
                         ArtistDTO requestBody
    ) {

        var call = artistsApi.add(bearerToken, requestBody);
        Response<ArtistDTO> response = null;

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
                case CONFLICT -> new ArtistAlreadyExistException(requestBody.getName());
                default -> new ServiceUnavailableException(SERVICE_NAME, call, response.code(), message);
            };

        } catch (IOException ex) {
            throw unknownException(response, call, ex);
        }
    }

    @Step("Send request GET:[rococo-artists]/api/artist")
    public Optional<ArtistDTO> findById(UUID id) {

        var call = artistsApi.findById(id);
        Response<ArtistDTO> response = null;

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

    @Step("Send request GET:[rococo-artists]/api/artist")
    public Page<ArtistDTO> findAll(@Nullable String name, int page, int size) {

        var call = name == null
                ? artistsApi.findAll(page, size)
                : artistsApi.findAll(name, page, size);
        Response<RestPage<ArtistDTO>> response = null;
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

    @Step("Send request PATCH:[rococo-artists]/api/artist")
    public ArtistDTO update(String bearerToken, ArtistDTO requestBody) {

        var call = artistsApi.update(bearerToken, requestBody);
        Response<ArtistDTO> response = null;

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
                case CONFLICT -> new ArtistAlreadyExistException(requestBody.getName());
                default -> new ServiceUnavailableException(SERVICE_NAME, call, response.code(), message);
            };

        } catch (IOException ex) {
            throw unknownException(response, call, ex);
        }

    }

    @Step("Send request DELETE:[rococo-artists]/api/artist")
    public void delete(String bearerToken, UUID id) {

        var call = artistsApi.delete(bearerToken, id);
        Response<Void> response = null;

        try {
            response = call.execute();

            if (response.isSuccessful()) return;

            var method = call.request().method();
            var path = call.request().url().encodedPath();
            var message = response.errorBody() != null
                    ? response.errorBody().string()
                    : "";

            throw switch (response.code()) {
                case BAD_REQUEST -> new BadRequestException(method, path, message);
                case UNAUTHORIZED -> new UnauthorizedException(method, path);
                case NOT_FOUND -> new ArtistNotFoundException(id);
                default -> new ServiceUnavailableException(SERVICE_NAME, call, response.code(), message);
            };

        } catch (IOException ex) {
            throw unknownException(response, call, ex);
        }

    }

}
