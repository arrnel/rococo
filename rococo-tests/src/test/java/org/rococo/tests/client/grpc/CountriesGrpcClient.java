package org.rococo.tests.client.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.countries.CountriesServiceGrpc;
import org.rococo.tests.enums.CountryCode;
import org.rococo.tests.ex.ServiceUnavailableException;
import org.rococo.tests.mapper.CountryMapper;
import org.rococo.tests.model.CountryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@ParametersAreNonnullByDefault
public class CountriesGrpcClient extends GrpcClient {

    private static final String SERVICE_NAME = "rococo-countries";
    private final CountriesServiceGrpc.CountriesServiceBlockingStub countriesServiceStub;

    public CountriesGrpcClient() {
        super(CFG.countriesGrpcHost(), CFG.countriesPort());
        countriesServiceStub = CountriesServiceGrpc.newBlockingStub(channel);
    }

    public Optional<CountryDTO> findById(UUID id) {
        try {
            return Optional.of(
                    CountryMapper.toDTO(
                            countriesServiceStub.findById(
                                    IdType.newBuilder()
                                            .setId(id.toString())
                                            .build())));
        } catch (StatusRuntimeException ex) {

            if (ex.getStatus().getCode() != Status.Code.NOT_FOUND)
                throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());

            return Optional.empty();

        }
    }

    public Optional<CountryDTO> findByCode(CountryCode code) {
        if (code == CountryCode.EMPTY) throw new IllegalArgumentException("Country code cannot be empty");
        try {
            return Optional.of(
                    CountryMapper.toDTO(
                            countriesServiceStub.findByCode(
                                    NameType.newBuilder()
                                            .setName(code.name())
                                            .build())));
        } catch (StatusRuntimeException ex) {

            if (ex.getStatus().getCode() != Status.Code.NOT_FOUND)
                throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());

            return Optional.empty();

        }
    }

    public Page<CountryDTO> findAll(Pageable pageable) {
        try {
            return CountryMapper.toPageDTO(
                    countriesServiceStub.findAll(
                            CountryMapper.toFilter(pageable)));
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

}
