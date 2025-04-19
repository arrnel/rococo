package org.rococo.museums.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.countries.CountryGrpcResponse;
import org.rococo.grpc.files.ImageGrpcResponse;
import org.rococo.grpc.museums.*;
import org.rococo.museums.client.CountriesGrpcClient;
import org.rococo.museums.client.FilesGrpcClient;
import org.rococo.museums.data.MuseumEntity;
import org.rococo.museums.data.MuseumRepository;
import org.rococo.museums.ex.CountryNotFoundException;
import org.rococo.museums.ex.MuseumAlreadyExistsException;
import org.rococo.museums.ex.MuseumNotFoundException;
import org.rococo.museums.mapper.MuseumMapper;
import org.rococo.museums.mapper.PageableMapper;
import org.rococo.museums.specs.MuseumSpecs;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class MuseumGrpcService extends MuseumsServiceGrpc.MuseumsServiceImplBase {

    private final MuseumRepository museumRepository;
    private final MuseumSpecs museumSpecs;
    private final FilesGrpcClient filesClient;
    private final CountriesGrpcClient countriesClient;

    @Override
    @Transactional
    public void add(AddMuseumGrpcRequest request, StreamObserver<MuseumGrpcResponse> responseObserver) {

        log.info("Add new museum: {}", request);

        museumRepository.findByTitle(request.getTitle())
                .ifPresentOrElse(
                        museum -> {
                            throw new MuseumAlreadyExistsException(request.getTitle());
                        },
                        () -> {
                            var countryId = UUID.fromString(request.getCountryId());
                            var country = countriesClient.findById(countryId)
                                    .orElseThrow(() -> new CountryNotFoundException(countryId));

                            var museum = museumRepository.save(
                                    MuseumMapper.fromGrpcRequest(request));
                            filesClient.add(museum.getId(), request.getPhoto());

                            responseObserver.onNext(
                                    MuseumMapper.toGrpcResponse(
                                            museum,
                                            country,
                                            ImageGrpcResponse.newBuilder()
                                                    .setEntityId(museum.getId().toString())
                                                    .setContent(ByteString.copyFromUtf8(request.getPhoto()))
                                                    .build()));
                            responseObserver.onCompleted();
                        });

    }

    @Override
    @Transactional(readOnly = true)
    public void findById(IdType request, StreamObserver<MuseumGrpcResponse> responseObserver) {

        log.info("Find museum by id: {}", request.getId());

        museumRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        museum -> {
                            var country = countriesClient.findById(museum.getCountryId())
                                    .orElse(CountryGrpcResponse.getDefaultInstance());
                            var photo = filesClient.findImage(museum.getId())
                                    .orElse(ImageGrpcResponse.getDefaultInstance());
                            responseObserver.onNext(MuseumMapper.toGrpcResponse(museum, country, photo));
                            responseObserver.onCompleted();
                        },
                        () -> {
                            throw new MuseumNotFoundException(UUID.fromString(request.getId()));
                        }
                );

    }

    @Override
    @Transactional(readOnly = true)
    public void findByTitle(NameType request, StreamObserver<MuseumGrpcResponse> responseObserver) {

        log.info("Find museum by title: {}", request.getName());

        museumRepository.findByTitle(request.getName())
                .ifPresentOrElse(
                        museum -> {
                            var country = countriesClient.findById(museum.getCountryId()).orElse(null);
                            var photo = filesClient.findImage(museum.getId()).orElse(null);
                            responseObserver.onNext(MuseumMapper.toGrpcResponse(museum, country, photo));
                            responseObserver.onCompleted();
                        },
                        () -> {
                            throw new MuseumNotFoundException(request.getName());
                        }
                );


    }

    @Override
    @Transactional(readOnly = true)
    public void findAllByIds(MuseumsByIdsGrpcRequest request, StreamObserver<MuseumListGrpcResponse> responseObserver) {

        var isOriginalText = request.getOriginalPhoto()
                ? "original"
                : "thumbnail";
        log.info("Find all museums with {} photos by params: {}", isOriginalText, request);

        var museumIds = request.getIds().getIdList().stream()
                .map(UUID::fromString)
                .distinct()
                .toList();

        var museumEntities = museumRepository.findAllById(museumIds);

        var countryIds = museumEntities.stream()
                .map(MuseumEntity::getCountryId)
                .distinct()
                .toList();

        var countryMap = countriesClient.findAllByIds(countryIds).stream()
                .collect(Collectors.toMap(
                        country -> UUID.fromString(country.getId()),
                        country -> country));

        var photoMap = filesClient.findAllByIds(museumIds, request.getOriginalPhoto()).stream()
                .collect(Collectors.toMap(
                        photo -> UUID.fromString(photo.getEntityId()),
                        photo -> photo));

        var grpcMuseums = museumEntities.stream()
                .map(museum -> MuseumMapper
                        .toGrpcResponse(
                                museum,
                                countryMap.getOrDefault(museum.getCountryId(), CountryGrpcResponse.getDefaultInstance()),
                                photoMap.getOrDefault(museum.getId(), ImageGrpcResponse.getDefaultInstance())))
                .toList();

        responseObserver.onNext(MuseumListGrpcResponse.newBuilder()
                .addAllMuseums(grpcMuseums)
                .build());

        responseObserver.onCompleted();

    }

    @Override
    @Transactional(readOnly = true)
    public void findAll(MuseumsFilterGrpcRequest request, StreamObserver<MuseumsGrpcResponse> responseObserver) {

        var isOriginalText = request.getOriginalPhoto()
                ? "original"
                : "thumbnail";
        log.info("Find all museums with {} photos by params: {}", isOriginalText, request);

        var museumEntities = museumRepository.findAll(
                museumSpecs.findByCriteria(
                        MuseumMapper.fromGrpcFilter(request)),
                PageableMapper.fromPageableGrpc(request.getPageable()));

        var countryIds = museumEntities.stream()
                .map(MuseumEntity::getCountryId)
                .distinct()
                .toList();

        var museumIds = museumEntities.stream()
                .map(MuseumEntity::getId)
                .distinct()
                .toList();

        var countryMap = countriesClient.findAllByIds(countryIds).stream()
                .collect(Collectors.toMap(
                        country -> UUID.fromString(country.getId()),
                        country -> country));

        var photoMap = filesClient.findAllByIds(museumIds, request.getOriginalPhoto()).stream()
                .collect(Collectors.toMap(
                        photo -> UUID.fromString(photo.getEntityId()),
                        photo -> photo));

        responseObserver.onNext(
                MuseumMapper.toPageGrpc(
                        museumRepository.findAll(
                                museumSpecs.findByCriteria(
                                        MuseumMapper.fromGrpcFilter(request)),
                                PageableMapper.fromPageableGrpc(request.getPageable())),
                        countryMap,
                        photoMap
                ));

        responseObserver.onCompleted();

    }

    @Override
    @Transactional
    public void update(UpdateMuseumGrpcRequest request, StreamObserver<MuseumGrpcResponse> responseObserver) {

        log.info("Update museum: {}", request);

        museumRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        museum -> {
                            museumRepository.findByTitle(request.getTitle())
                                    .ifPresent(m -> {
                                        if (!m.getId().equals(museum.getId()))
                                            throw new MuseumAlreadyExistsException(request.getTitle());
                                    });

                            var countryId = UUID.fromString(request.getCountryId());
                            var country = countriesClient.findById(countryId)
                                    .orElseThrow(() -> new CountryNotFoundException(countryId));

                            var updatedMuseum = museumRepository.save(
                                    MuseumMapper.updateFromGrpcRequest(museum, request));

                            var existPhoto = filesClient.findImage(museum.getId());
                            if (existPhoto.isPresent() && !request.getPhoto().isEmpty()) {
                                filesClient.update(museum.getId(), request.getPhoto());
                            } else if (existPhoto.isPresent() && request.getPhoto().isEmpty()) {
                                filesClient.delete(museum.getId());
                            } else if (existPhoto.isEmpty() && !request.getPhoto().isEmpty()) {
                                filesClient.add(museum.getId(), request.getPhoto());
                            }

                            responseObserver.onNext(
                                    MuseumMapper.toGrpcResponse(
                                            updatedMuseum,
                                            country,
                                            ImageGrpcResponse.newBuilder()
                                                    .setEntityId(museum.getId().toString())
                                                    .setContent(ByteString.copyFromUtf8(request.getPhoto()))
                                                    .build()
                                    ));
                            responseObserver.onCompleted();
                        },
                        () -> {
                            throw new MuseumNotFoundException(UUID.fromString(request.getId()));
                        }
                );

    }

    @Override
    @Transactional
    public void removeById(IdType request, StreamObserver<com.google.protobuf.Empty> responseObserver) {

        log.info("Delete museum by id: {}", request.getId());

        museumRepository.findById(UUID.fromString(request.getId()))
                .ifPresent(museum -> {
                    filesClient.delete(museum.getId());
                    museumRepository.delete(museum);
                });

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();

    }

}
