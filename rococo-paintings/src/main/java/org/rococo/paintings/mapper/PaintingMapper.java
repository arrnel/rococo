package org.rococo.paintings.mapper;

import org.rococo.grpc.artists.ArtistGrpcResponse;
import org.rococo.grpc.artists.ArtistShortGrpcResponse;
import org.rococo.grpc.museums.MuseumGrpcResponse;
import org.rococo.grpc.museums.MuseumShortGrpcResponse;
import org.rococo.grpc.paintings.*;
import org.rococo.paintings.data.PaintingEntity;
import org.rococo.paintings.model.PaintingFilter;
import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class PaintingMapper {

    private PaintingMapper() {
    }

    @Nonnull
    public static PaintingEntity fromGrpcRequest(AddPaintingGrpcRequest request) {
        return PaintingEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription().isEmpty()
                        ? null
                        : request.getDescription())
                .artistId(UUID.fromString(request.getArtistId()))
                .museumId(UUID.fromString(request.getMuseumId()))
                .build();
    }

    @Nonnull
    public static PaintingEntity updateFromGrpcRequest(PaintingEntity entity, UpdatePaintingGrpcRequest request) {
        return PaintingEntity.builder()
                .id(entity.getId())
                .title(request.getTitle())
                .description(request.getDescription().isEmpty()
                        ? null
                        : request.getDescription())
                .artistId(UUID.fromString(request.getArtistId()))
                .museumId(UUID.fromString(request.getMuseumId()))
                .build();
    }

    @Nonnull
    public static PaintingGrpcResponse toGrpcResponse(PaintingEntity entity, ArtistGrpcResponse artist, MuseumGrpcResponse museum, String photo) {
        return PaintingGrpcResponse.newBuilder()
                .setId(entity.getId().toString())
                .setTitle(entity.getTitle())
                .setDescription(entity.getDescription() == null
                        ? ""
                        : entity.getDescription())
                .setArtist(
                        ArtistShortGrpcResponse.newBuilder()
                                .setId(artist.getId())
                                .setName(artist.getName())
                                .setBiography(artist.getBiography())
                                .build())
                .setMuseum(
                        MuseumShortGrpcResponse.newBuilder()
                                .setId(museum.getId())
                                .setTitle(museum.getTitle())
                                .setDescription(museum.getDescription())
                                .setCity(museum.getCity())
                                .setCountry(museum.getCountry())
                                .build())
                .setPhoto(photo)
                .build();
    }

    @Nonnull
    public static PaintingFilter fromGrpcFilter(PaintingsFilterGrpcRequest request) {
        return PaintingFilter.builder()
                .query(request.getQuery().isEmpty()
                        ? null
                        : request.getQuery())
                .artistId(request.getArtistId().isEmpty()
                        ? null
                        : UUID.fromString(request.getArtistId()))
                .museumId(request.getMuseumId().isEmpty()
                        ? null
                        : UUID.fromString(request.getMuseumId()))
                .build();
    }

    @Nonnull
    public static PaintingsGrpcResponse toPageGrpc(Page<PaintingEntity> page, Map<UUID, ArtistGrpcResponse> artists, Map<UUID, MuseumGrpcResponse> museums, Map<UUID, String> photos) {
        return PaintingsGrpcResponse.newBuilder()
                .setCurrentPage(page.getPageable().getPageNumber())
                .setItemsPerPage(page.getSize())
                .setTotalItems(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .addAllData(page.getContent().stream()
                        .map(painting -> PaintingMapper.toGrpcResponse(painting, artists.get(painting.getArtistId()), museums.get(painting.getMuseumId()), photos.get(painting.getId())))
                        .toList())
                .build();
    }

}
