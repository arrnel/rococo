package org.rococo.countries.service;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.rococo.countries.data.CountryCode;
import org.rococo.countries.data.CountryRepository;
import org.rococo.countries.mapper.CountryMapper;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.IdsType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.countries.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static io.grpc.Status.NOT_FOUND;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CountryGrpcService extends CountriesServiceGrpc.CountriesServiceImplBase {

    private final CountryRepository countryRepository;

    @Override
    @Transactional(readOnly = true)
    public void findById(final IdType request, final StreamObserver<CountryGrpcResponse> responseObserver) {

        log.info("Find country by id: {}", request.getId());

        countryRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        country -> {
                            responseObserver.onNext(CountryMapper.toGrpcResponse(country));
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(
                                NOT_FOUND
                                        .withDescription("Country with id = [%s] not found".formatted(request.getId()))
                                        .asRuntimeException())
                );

    }

    @Override
    @Transactional(readOnly = true)
    public void findByCode(final NameType request, final StreamObserver<CountryGrpcResponse> responseObserver) {

        log.info("Find country by code: {}", request.getName());

        countryRepository.findByCode(CountryCode.valueOf(request.getName()))
                .ifPresentOrElse(
                        country -> {
                            responseObserver.onNext(CountryMapper.toGrpcResponse(country));
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(
                                NOT_FOUND
                                        .withDescription("Country with code = [%s] not found".formatted(request.getName()))
                                        .asRuntimeException())
                );

    }

    @Override
    @Transactional(readOnly = true)
    public void findAllByIds(IdsType request, StreamObserver<CountryListGrpcResponse> responseObserver) {

        log.info("Find all countries by ids: {}", request.getIdList());

        var countryIds = request.getIdList().stream()
                .map(UUID::fromString)
                .toList();
        var countries = countryRepository.findAllById(countryIds).stream()
                .map(CountryMapper::toGrpcResponse)
                .toList();

        responseObserver.onNext(
                CountryListGrpcResponse.newBuilder()
                        .addAllCountries(countries)
                        .build());

        responseObserver.onCompleted();

    }

    @Override
    @Transactional(readOnly = true)
    public void findAll(CountryFilterGrpcRequest request, StreamObserver<CountriesGrpcResponse> responseObserver) {

        log.info("Find all countries by params: {}", request);

        responseObserver.onNext(
                CountryMapper.toPageGrpc(
                        countryRepository.findAll(
                                CountryMapper.fromPageableGrpc(request.getPageable()))));

        responseObserver.onCompleted();

    }

}
