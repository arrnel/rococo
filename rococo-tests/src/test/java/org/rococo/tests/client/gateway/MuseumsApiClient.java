package org.rococo.tests.client.gateway;

import io.qameta.allure.Step;
import okhttp3.logging.HttpLoggingInterceptor;
import org.rococo.tests.client.gateway.core.RestClient;
import org.rococo.tests.ex.*;
import org.rococo.tests.model.MuseumDTO;
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
public class MuseumsApiClient extends RestClient {

    private final MuseumsApi museumsApi;

    public MuseumsApiClient() {
        super(CFG.gatewayUrl(), HttpLoggingInterceptor.Level.HEADERS);
        this.museumsApi = create(MuseumsApi.class);
    }

    @Step("Send request POST:[rococo-museums]/api/museum")
    public MuseumDTO add(String bearerToken,
                         MuseumDTO requestBody
    ) {
        var call = museumsApi.add(bearerToken, requestBody);
        Response<MuseumDTO> response = null;

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
                case NOT_FOUND -> new CountryNotFoundException(requestBody.getLocation().getCountry().getId());
                case CONFLICT -> new MuseumAlreadyExistsException(requestBody.getTitle());
                default -> new ServiceUnavailableException(SERVICE_NAME, call, response.code(), message);
            };

        } catch (IOException ex) {
            throw unknownException(response, call, ex);
        }
    }

    @Step("Send request GET:[rococo-museums]/api/museum")
    public Optional<MuseumDTO> findById(UUID id) {
        var call = museumsApi.findById(id);
        Response<MuseumDTO> response = null;

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

    @Step("Send request GET:[rococo-museums]/api/museum")
    public Page<MuseumDTO> findAll(@Nullable String title, int page, int size) {
        var call = title == null
                ? museumsApi.findAll(page, size)
                : museumsApi.findAll(title, page, size);
        Response<RestPage<MuseumDTO>> response = null;
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

    @Step("Send request PATCH:[rococo-museums]/api/museum")
    public MuseumDTO update(String bearerToken, MuseumDTO requestBody) {
        var call = museumsApi.update(bearerToken, requestBody);
        Response<MuseumDTO> response = null;

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
                case NOT_FOUND -> response.message().contains(requestBody.getId().toString())
                        ? new MuseumNotFoundException(requestBody.getId())
                        : new CountryNotFoundException(requestBody.getLocation().getCountry().getId());
                case CONFLICT -> new MuseumAlreadyExistsException(requestBody.getTitle());
                default -> new ServiceUnavailableException(SERVICE_NAME, call, response.code(), message);
            };

        } catch (IOException ex) {
            throw unknownException(response, call, ex);
        }
    }

    @Step("Send request DELETE:[rococo-museums]/api/museum")
    public void delete(String bearerToken, UUID id) {

        var call = museumsApi.delete(bearerToken, id);
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
                case NOT_FOUND -> new MuseumNotFoundException(id);
                default -> new ServiceUnavailableException(SERVICE_NAME, call, response.code(), message);
            };

        } catch (IOException ex) {
            throw unknownException(response, call, ex);
        }
    }

}
