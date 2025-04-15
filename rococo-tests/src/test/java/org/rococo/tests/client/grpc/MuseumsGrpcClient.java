package org.rococo.tests.client.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.museums.MuseumsServiceGrpc;
import org.rococo.tests.ex.CountryNotFoundException;
import org.rococo.tests.ex.MuseumAlreadyExistsException;
import org.rococo.tests.ex.MuseumNotFoundException;
import org.rococo.tests.ex.ServiceUnavailableException;
import org.rococo.tests.mapper.MuseumMapper;
import org.rococo.tests.model.MuseumDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static io.grpc.Status.Code.ALREADY_EXISTS;
import static io.grpc.Status.Code.NOT_FOUND;

@ParametersAreNonnullByDefault
public class MuseumsGrpcClient extends GrpcClient {

    private static final String SERVICE_NAME = "rococo-museums";
    private final MuseumsServiceGrpc.MuseumsServiceBlockingStub museumsServiceStub;

    public MuseumsGrpcClient() {
        super(CFG.museumsGrpcHost(), CFG.museumsPort());
        museumsServiceStub = MuseumsServiceGrpc.newBlockingStub(channel);
    }

    public MuseumDTO add(MuseumDTO requestDTO) {
        try {
            var museum = museumsServiceStub.add(MuseumMapper.toGrpcRequest(requestDTO));
            return MuseumMapper.toDTO(museum);
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == ALREADY_EXISTS)
                throw new MuseumAlreadyExistsException(requestDTO.getTitle());
            if (ex.getStatus().getCode() == NOT_FOUND)
                throw new CountryNotFoundException(requestDTO.getLocation().getCountry().getId());
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    public Optional<MuseumDTO> findById(UUID id) {
        try {
            return Optional.of(
                    MuseumMapper.toDTO(
                            museumsServiceStub.findById(
                                    IdType.newBuilder()
                                            .setId(id.toString())
                                            .build())));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() != Status.Code.NOT_FOUND)
                throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
            return Optional.empty();
        }
    }

    public Optional<MuseumDTO> findByTitle(String title) {
        try {
            return Optional.of(
                    MuseumMapper.toDTO(
                            museumsServiceStub.findByTitle(
                                    NameType.newBuilder()
                                            .setName(title)
                                            .build())));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() != Status.Code.NOT_FOUND)
                throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
            return Optional.empty();
        }

    }

    public Page<MuseumDTO> findAll(@Nullable String name, Pageable pageable) {
        try {
            return MuseumMapper.toPageDTO(museumsServiceStub.findAll(
                    MuseumMapper.toFilter(name, pageable)));
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    public MuseumDTO update(MuseumDTO requestDTO) {

        try {
            return MuseumMapper.toDTO(
                    museumsServiceStub.update(
                            MuseumMapper.toUpdateGrpcRequest(requestDTO)));
        } catch (StatusRuntimeException ex) {

            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw Objects.requireNonNull(ex.getStatus().getDescription()).contains("Museum")
                        ? new MuseumNotFoundException(requestDTO.getId())
                        : new CountryNotFoundException(requestDTO.getLocation().getCountry().getId());
            }
            if (ex.getStatus().getCode() == Status.Code.ALREADY_EXISTS)
                throw new MuseumAlreadyExistsException(requestDTO.getTitle());
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

    public void delete(UUID id) {
        try {
            museumsServiceStub.removeById(
                    IdType.newBuilder()
                            .setId(id.toString())
                            .build());
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

}
