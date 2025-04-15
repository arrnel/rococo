package org.rococo.artists.mapper;

import org.rococo.artists.data.ArtistEntity;
import org.rococo.artists.model.ArtistFilter;
import org.rococo.grpc.artists.*;
import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class ArtistMapper {

    private ArtistMapper() {
    }

    @Nonnull
    public static ArtistEntity fromGrpcRequest(AddArtistGrpcRequest request) {
        return ArtistEntity.builder()
                .name(request.getName().isEmpty()
                        ? null
                        : request.getName())
                .biography(request.getBiography().isEmpty()
                        ? null
                        : request.getBiography())
                .build();
    }

    @Nonnull
    public static ArtistEntity updateFromGrpcRequest(ArtistEntity entity, UpdateArtistGrpcRequest request) {
        return ArtistEntity.builder()
                .id(entity.getId())
                .name(request.getName().isEmpty()
                        ? null
                        : request.getName())
                .biography(request.getBiography().isEmpty()
                        ? null
                        : request.getBiography())
                .build();
    }

    @Nonnull
    public static ArtistGrpcResponse toGrpcResponse(ArtistEntity entity, @Nullable String photo) {
        return ArtistGrpcResponse.newBuilder()
                .setId(entity.getId().toString())
                .setName(entity.getName() == null
                        ? ""
                        : entity.getName())
                .setBiography(entity.getBiography() == null
                        ? ""
                        : entity.getBiography())
                .setPhoto(photo == null
                        ? ""
                        : photo)
                .build();
    }

    @Nonnull
    public static ArtistFilter fromGrpcFilter(ArtistsFilterGrpcRequest request) {
        return ArtistFilter.builder()
                .query(request.getQuery())
                .build();
    }

    @Nonnull
    public static ArtistsGrpcResponse toPageGrpc(Page<ArtistEntity> page, Map<UUID, String> photos) {
        return ArtistsGrpcResponse.newBuilder()
                .setCurrentPage(page.getPageable().getPageNumber())
                .setItemsPerPage(page.getSize())
                .setTotalItems(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .addAllData(page.getContent().stream()
                        .map(artist -> ArtistMapper.toGrpcResponse(artist, photos.get(artist.getId())))
                        .toList())
                .build();
    }

}
