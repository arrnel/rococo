package org.rococo.gateway.client;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.rococo.gateway.ex.MuseumAlreadyExistsException;
import org.rococo.gateway.ex.MuseumNotFoundException;
import org.rococo.gateway.ex.ServiceUnavailableException;
import org.rococo.gateway.mapper.MuseumMapper;
import org.rococo.gateway.model.museums.AddMuseumRequestDTO;
import org.rococo.gateway.model.museums.MuseumDTO;
import org.rococo.gateway.model.museums.UpdateMuseumRequestDTO;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.museums.MuseumsServiceGrpc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static io.grpc.Status.Code.ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class MuseumsGrpcClient {

    private static final String SERVICE_NAME = "rococo-museums";

    @GrpcClient("grpcMuseumsClient")
    private MuseumsServiceGrpc.MuseumsServiceBlockingStub museumsServiceStub;

    public MuseumDTO add(AddMuseumRequestDTO requestDTO) {

        try {
            return MuseumMapper.toDTO(
                    museumsServiceStub.add(
                            MuseumMapper.toGrpcModel(requestDTO)));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == ALREADY_EXISTS)
                throw new MuseumAlreadyExistsException(requestDTO.title());
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    public Optional<MuseumDTO> findById(UUID id) {

        try {
            var idType = IdType.newBuilder()
                    .setId(id.toString())
                    .build();
            return Optional.of(
                    MuseumMapper.toDTO(
                            museumsServiceStub.findById(idType)));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND)
                return Optional.empty();
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

    public Page<MuseumDTO> findAll(String title, Pageable pageable) {

        try {
            return MuseumMapper.toPageDTO(museumsServiceStub.findAll(
                    MuseumMapper.toFilter(title, pageable)));
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

    public MuseumDTO update(UpdateMuseumRequestDTO requestDTO) {

        try {
            return MuseumMapper.toDTO(
                    museumsServiceStub.update(
                            MuseumMapper.toGrpcModel(requestDTO)));
        } catch (StatusRuntimeException ex) {

            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND)
                throw new MuseumNotFoundException(requestDTO.id());

            if (ex.getStatus().getCode() == Status.Code.ALREADY_EXISTS)
                throw new MuseumAlreadyExistsException(requestDTO.title());

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
