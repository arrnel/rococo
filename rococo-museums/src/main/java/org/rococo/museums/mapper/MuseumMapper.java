package org.rococo.museums.mapper;

import org.rococo.grpc.countries.CountryGrpcResponse;
import org.rococo.grpc.files.ImageGrpcResponse;
import org.rococo.grpc.museums.*;
import org.rococo.museums.data.MuseumEntity;
import org.rococo.museums.model.MuseumFilter;
import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class MuseumMapper {

    private MuseumMapper() {
    }

    @Nonnull
    public static MuseumEntity fromGrpcRequest(AddMuseumGrpcRequest request) {
        return MuseumEntity.builder()
                .title(request.getTitle().isEmpty()
                        ? null
                        : request.getTitle())
                .description(request.getDescription().isEmpty()
                        ? null
                        : request.getDescription())
                .countryId(request.getCountryId().isEmpty()
                        ? null
                        : UUID.fromString(request.getCountryId()))
                .city(request.getCity())
                .build();
    }

    @Nonnull
    public static MuseumEntity updateFromGrpcRequest(MuseumEntity entity, UpdateMuseumGrpcRequest request) {
        return MuseumEntity.builder()
                .id(entity.getId())
                .title(request.getTitle().isEmpty()
                        ? null
                        : request.getTitle())
                .description(request.getDescription().isEmpty()
                        ? null
                        : request.getDescription())
                .countryId(request.getCountryId().isEmpty()
                        ? null
                        : UUID.fromString(request.getCountryId()))
                .city(request.getCity().isEmpty()
                        ? null
                        : request.getCity())
                .build();
    }

    @Nonnull
    public static MuseumGrpcResponse toGrpcResponse(MuseumEntity entity, @Nullable CountryGrpcResponse country, @Nullable ImageGrpcResponse image) {
        return MuseumGrpcResponse.newBuilder()
                .setId(entity.getId() == null
                        ? ""
                        : entity.getId().toString())
                .setTitle(entity.getTitle() == null
                        ? ""
                        : entity.getTitle())
                .setDescription(entity.getDescription() == null
                        ? ""
                        : entity.getDescription())
                .setCountry(country == null
                        ? CountryGrpcResponse.getDefaultInstance()
                        : country)
                .setCity(entity.getCity() == null
                        ? ""
                        : entity.getCity())
                .setPhoto(image == null
                        ? ""
                        : image.getContent().toStringUtf8())
                .build();
    }

    @Nonnull
    public static MuseumsGrpcResponse toPageGrpc(Page<MuseumEntity> page, Map<UUID, CountryGrpcResponse> countries, Map<UUID, ImageGrpcResponse> photos) {
        return MuseumsGrpcResponse.newBuilder()
                .setCurrentPage(page.getPageable().getPageNumber())
                .setItemsPerPage(page.getSize())
                .setTotalItems(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .addAllData(page.getContent().stream()
                        .map(museum -> MuseumMapper.toGrpcResponse(museum, countries.get(museum.getCountryId()), photos.get(museum.getId())))
                        .toList())
                .build();
    }

    public static MuseumFilter fromGrpcFilter(MuseumsFilterGrpcRequest request) {
        return MuseumFilter.builder()
                .query(request.getQuery().isEmpty()
                        ? null
                        : request.getQuery())
                .countryId(request.getCountryId().isEmpty()
                        ? null
                        : UUID.fromString(request.getCountryId()))
                .city(request.getCity().isEmpty()
                        ? null
                        : request.getCountryId())
                .build();
    }

}
