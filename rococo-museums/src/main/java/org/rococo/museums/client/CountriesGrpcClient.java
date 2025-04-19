package org.rococo.museums.client;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.rococo.grpc.common.page.PageableGrpc;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.IdsType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.countries.CountriesGrpcResponse;
import org.rococo.grpc.countries.CountriesServiceGrpc;
import org.rococo.grpc.countries.CountryFilterGrpcRequest;
import org.rococo.grpc.countries.CountryGrpcResponse;
import org.rococo.museums.ex.ServiceUnavailableException;
import org.springframework.stereotype.Service;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class CountriesGrpcClient {

    private static final String SERVICE_NAME = "rococo-countries";

    @GrpcClient("grpcCountriesClient")
    private CountriesServiceGrpc.CountriesServiceBlockingStub countriesServiceStub;

    public Optional<CountryGrpcResponse> findById(UUID id) {
        try {
            var country = countriesServiceStub.findById(
                    IdType.newBuilder()
                            .setId(id.toString())
                            .build());
            return Optional.of(country);
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND)
                return Optional.empty();
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    public Optional<CountryGrpcResponse> findByCode(String code) {
        try {
            var country = countriesServiceStub.findByCode(
                    NameType.newBuilder()
                            .setName(code.toUpperCase())
                            .build());
            return Optional.of(country);
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND)
                return Optional.empty();
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    public List<CountryGrpcResponse> findAllByIds(List<UUID> countriesIds) {
        try {
            var idsType = IdsType.newBuilder()
                    .addAllId(countriesIds.stream()
                            .filter(Objects::nonNull)
                            .map(UUID::toString)
                            .toList())
                    .build();
            return countriesServiceStub.findAllByIds(idsType).getCountriesList();
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    public CountriesGrpcResponse findAll(PageableGrpc pageable) {
        try {
            return countriesServiceStub.findAll(
                    CountryFilterGrpcRequest.newBuilder()
                            .setPageable(pageable)
                            .build());
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

}
