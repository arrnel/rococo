package org.rococo.tests.client.gateway;

import io.qameta.allure.Step;
import okhttp3.logging.HttpLoggingInterceptor;
import org.rococo.tests.client.gateway.core.RestClient;
import org.rococo.tests.ex.*;
import org.rococo.tests.model.PaintingDTO;
import org.rococo.tests.model.RestPage;
import org.springframework.data.domain.Page;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.rococo.tests.enums.HttpStatus.*;

@ParametersAreNonnullByDefault
public class PaintingsApiClient extends RestClient {

    private final PaintingsApi paintingsApi;

    public PaintingsApiClient() {
        super(CFG.gatewayUrl(), HttpLoggingInterceptor.Level.HEADERS);
        this.paintingsApi = create(PaintingsApi.class);
    }

    @Nonnull
    @Step("Send add painting request. POST:[rococo-paintings]/api/painting")
    public PaintingDTO add(String bearerToken,
                           PaintingDTO requestBody
    ) {
        var call = paintingsApi.add(bearerToken, requestBody);
        Response<PaintingDTO> response = null;

        try {
            response = call.execute();
            if (response.isSuccessful()) return response.body();

            var statusCode = response.code();
            var message = response.errorBody() != null
                    ? response.errorBody().string()
                    : null;

            throw switch (response.code()) {
                case BAD_REQUEST -> new BadRequestException(call, message);
                case UNAUTHORIZED -> new UnauthorizedException(call);
                case NOT_FOUND -> message.contains(requestBody.getArtist().getId().toString())
                        ? new ArtistNotFoundException(requestBody.getArtist().getId())
                        : new MuseumNotFoundException(requestBody.getMuseum().getId());
                case CONFLICT -> new PaintingAlreadyExistsException(requestBody.getTitle());
                default -> new ServiceUnavailableException(SERVICE_NAME, call, statusCode, message);
            };

        } catch (IOException ex) {
            throw unknownException(response, call, ex);
        }
    }

    @Nonnull
    @Step("Send request GET:[rococo-paintings]/api/painting")
    public Optional<PaintingDTO> findById(UUID id) {
        var call = paintingsApi.findById(id);
        Response<PaintingDTO> response = null;

        try {
            response = call.execute();
            var statusCode = response.code();
            return switch (statusCode) {
                case OK -> Optional.ofNullable(response.body());
                case NOT_FOUND -> Optional.empty();
                default -> {
                    var message = response.errorBody() != null
                            ? response.errorBody().string()
                            : null;
                    throw new ServiceUnavailableException(SERVICE_NAME, call, statusCode, message);
                }
            };

        } catch (IOException ex) {
            throw unknownException(response, call, ex);
        }
    }

    @Nonnull
    @Step("Send find all artist with partial title request. GET:[rococo-paintings]/api/painting")
    public Page<PaintingDTO> findAll(@Nullable String title, int page, int size) {
        var call = title == null
                ? paintingsApi.findAll(page, size)
                : paintingsApi.findAll(title, page, size);
        Response<RestPage<PaintingDTO>> response = null;
        try {
            response = call.execute();
            if (response.isSuccessful())
                return response.body();
            var message = response.errorBody() != null
                    ? response.errorBody().string()
                    : null;
            throw new ServiceUnavailableException(SERVICE_NAME, call, response.code(), message);
        } catch (IOException ex) {
            throw unknownException(response, call, ex);
        }
    }

    @Nonnull
    @Step("Send find all artist paintings request. GET:[rococo-paintings]/api/painting")
    public Page<PaintingDTO> findAll(UUID artistId, int page, int size) {
        var call = paintingsApi.findAll(artistId, page, size);
        Response<RestPage<PaintingDTO>> response = null;
        try {
            response = call.execute();
            if (response.isSuccessful())
                return response.body();
            var message = response.errorBody() != null
                    ? response.errorBody().string()
                    : null;
            throw new ServiceUnavailableException(SERVICE_NAME, call, response.code(), message);
        } catch (IOException ex) {
            throw unknownException(response, call, ex);
        }
    }

    @Nonnull
    @Step("Send update painting request. PATCH:[rococo-paintings]/api/painting")
    public PaintingDTO update(String bearerToken, PaintingDTO requestBody) {
        var call = paintingsApi.update(bearerToken, requestBody);
        Response<PaintingDTO> response = null;

        try {
            response = call.execute();
            if (response.isSuccessful()) return response.body();

            var message = response.errorBody() != null
                    ? response.errorBody().string()
                    : "";
            throw switch (response.code()) {
                case BAD_REQUEST -> new BadRequestException(call, message);
                case UNAUTHORIZED -> new UnauthorizedException(call);
                case NOT_FOUND -> {
                    if (message.contains(requestBody.getId().toString())) {
                        yield new PaintingNotFoundException(requestBody.getId());
                    } else if (message.contains(requestBody.getArtist().getId().toString())) {
                        yield new ArtistNotFoundException(requestBody.getArtist().getId());
                    } else {
                        yield new MuseumNotFoundException(requestBody.getMuseum().getId());
                    }
                }
                case CONFLICT -> new PaintingAlreadyExistsException(requestBody.getTitle());
                default -> new ServiceUnavailableException(SERVICE_NAME, call, response.code(), message);
            };

        } catch (IOException ex) {
            throw unknownException(response, call, ex);
        }

    }

    @Step("Send delete painting request. DELETE:[rococo-paintings]/api/painting")
    public void delete(String bearerToken, UUID id) {
        var call = paintingsApi.delete(bearerToken, id);
        Response<Void> response = null;

        try {
            response = call.execute();
            if (response.isSuccessful()) return;

            var message = response.errorBody() != null
                    ? response.errorBody().string()
                    : "";
            throw switch (response.code()) {
                case BAD_REQUEST -> new BadRequestException(call, message);
                case UNAUTHORIZED -> new UnauthorizedException(call);
                case NOT_FOUND -> new PaintingNotFoundException(id);
                default -> new ServiceUnavailableException(SERVICE_NAME, call, response.code(), message);
            };

        } catch (IOException ex) {
            throw unknownException(response, call, ex);
        }
    }

}
