package org.rococo.tests.mapper;

import org.rococo.grpc.artists.*;
import org.rococo.tests.data.entity.ArtistEntity;
import org.rococo.tests.jupiter.annotation.Artist;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.util.ImageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class ArtistMapper {

    private ArtistMapper() {
    }

    @Nonnull
    public static ArtistEntity fromDTO(ArtistDTO dto) {
        return ArtistEntity.builder()
                .name(dto.getName())
                .biography(dto.getBiography())
                .build();
    }

    @Nonnull
    public static ArtistEntity updateFromDTO(ArtistEntity entity, ArtistDTO dto) {
        return ArtistEntity.builder()
                .id(entity.getId())
                .name(dto.getName())
                .biography(dto.getBiography())
                .createdDate(entity.getCreatedDate())
                .build();
    }

    @Nonnull
    public static ArtistDTO updateFromAnno(ArtistDTO artistDTO, Artist anno) {
        // @formatter:off
        return ArtistDTO.builder()
                .id(artistDTO.getId())
                .name(anno.name().isEmpty()
                        ? artistDTO.getName()
                        : anno.name())
                .biography(anno.bio().isEmpty() && !anno.bioIsEmpty()
                        ? artistDTO.getBiography()
                        : anno.bio().isEmpty()
                                ? null
                                : anno.bio())
                .photo(anno.pathToPhoto().isEmpty()
                        ? artistDTO.getPhoto()
                        : ImageUtil.imageFileToBase64(Path.of(anno.pathToPhoto())))
                .build();
        // @formatter:on
    }


    @Nonnull
    public static ArtistDTO toDTO(ArtistEntity entity) {
        return ArtistDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .biography(entity.getBiography())
                .build();
    }

    @Nonnull
    public static ArtistDTO toDTO(ArtistEntity entity, @Nullable byte[] photo) {
        return ArtistDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .biography(entity.getBiography())
                .photo(photo == null || photo.length == 0
                        ? null
                        : new String(photo, StandardCharsets.UTF_8))
                .build();
    }

    @Nonnull
    public static AddArtistGrpcRequest toGrpcRequest(ArtistDTO dto) {
        return AddArtistGrpcRequest.newBuilder()
                .setName(dto.getName() == null
                        ? ""
                        : dto.getName())
                .setBiography(dto.getBiography() == null
                        ? ""
                        : dto.getBiography())
                .setPhoto(dto.getPhoto() == null
                        ? ""
                        : dto.getPhoto())
                .build();
    }

    @Nonnull
    public static UpdateArtistGrpcRequest toUpdateGrpcRequest(ArtistDTO dto) {
        return UpdateArtistGrpcRequest.newBuilder()
                .setId(dto.getId() == null
                        ? ""
                        : dto.getId().toString())
                .setName(dto.getName() == null
                        ? ""
                        : dto.getName())
                .setBiography(dto.getBiography() == null
                        ? ""
                        : dto.getBiography())
                .setPhoto(dto.getPhoto() == null
                        ? ""
                        : dto.getPhoto())
                .build();
    }

    @Nonnull
    public static ArtistDTO toDTO(ArtistGrpcResponse grpcResponse) {
        return ArtistDTO.builder()
                .id(grpcResponse.getId().isEmpty()
                        ? null
                        : UUID.fromString(grpcResponse.getId()))
                .name(grpcResponse.getName().isEmpty()
                        ? null
                        : grpcResponse.getName())
                .biography(grpcResponse.getBiography().isEmpty()
                        ? null
                        : grpcResponse.getBiography())
                .photo(grpcResponse.getPhoto().isEmpty()
                        ? null
                        : grpcResponse.getPhoto())
                .build();
    }

    @Nonnull
    public static ArtistsFilterGrpcRequest toFilter(@Nullable final String name, Pageable pageable) {
        return ArtistsFilterGrpcRequest.newBuilder()
                .setQuery(name == null
                        ? ""
                        : name)
                .setPageable(PageableMapper.toPageableGrpc(pageable))
                .build();
    }

    @Nonnull
    public static Page<ArtistDTO> toPageDTO(final ArtistsGrpcResponse response) {
        return new PageImpl<>(
                response.getDataList().stream()
                        .map(ArtistMapper::toDTO)
                        .toList(),
                PageRequest.of(response.getCurrentPage(),
                        response.getItemsPerPage()),
                response.getTotalItems()
        );
    }

}

