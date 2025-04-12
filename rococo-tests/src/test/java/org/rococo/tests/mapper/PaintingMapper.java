package org.rococo.tests.mapper;

import org.rococo.grpc.paintings.*;
import org.rococo.tests.data.entity.PaintingEntity;
import org.rococo.tests.jupiter.annotation.Painting;
import org.rococo.tests.model.ArtistDTO;
import org.rococo.tests.model.MuseumDTO;
import org.rococo.tests.model.PaintingDTO;
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
public class PaintingMapper {

    private PaintingMapper() {
    }

    @Nonnull
    public static PaintingEntity toEntity(PaintingDTO dto) {
        return PaintingEntity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .artistId(dto.getArtist().getId())
                .museumId(dto.getMuseum().getId())
                .build();
    }

    @Nonnull
    public static PaintingEntity updateFromDTO(PaintingEntity entity, PaintingDTO request) {
        return PaintingEntity.builder()
                .id(entity.getId())
                .title(request.getTitle())
                .description(request.getDescription())
                .artistId(request.getArtist().getId())
                .museumId(request.getMuseum().getId())
                .build();
    }

    @Nonnull
    public static PaintingDTO updateFromAnno(PaintingDTO paintingDTO, Painting anno) {

        return PaintingDTO.builder()
                .id(paintingDTO.getId())
                .title(anno.title().isEmpty()
                        ? paintingDTO.getTitle()
                        : anno.title())
                .description(anno.description().isEmpty() && !anno.descriptionEmpty()
                        ? paintingDTO.getDescription()
                        : anno.description())
                .artist(ArtistMapper.updateFromAnno(paintingDTO.getArtist(), anno.artist()))
                .museum(MuseumMapper.updateFromAnno(paintingDTO.getMuseum(), anno.museum()))
                .photo(anno.pathToPhoto().isEmpty()
                        ? ImageUtil.generateImage()
                        : ImageUtil.imageFileToBase64(Path.of(anno.pathToPhoto())))
                .build();
    }


    @Nonnull
    public static PaintingDTO toDTO(PaintingEntity entity) {
        return PaintingDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .artist(ArtistDTO.builder()
                        .id(entity.getArtistId())
                        .build())
                .museum(MuseumDTO.builder()
                        .id(entity.getMuseumId())
                        .build())
                .build();
    }

    @Nonnull
    public static PaintingDTO toDTO(PaintingEntity entity, @Nullable byte[] photo) {
        return PaintingDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .artist(ArtistDTO.builder()
                        .id(entity.getArtistId())
                        .build())
                .museum(MuseumDTO.builder()
                        .id(entity.getMuseumId())
                        .build())
                .photo(photo == null
                        ? null
                        : new String(photo, StandardCharsets.UTF_8))
                .build();
    }

    @Nonnull
    public static PaintingDTO toDTO(PaintingEntity entity,
                                    @Nullable byte[] photo,
                                    ArtistDTO artistDTO,
                                    MuseumDTO museumDTO
    ) {
        return toDTO(entity)
                .setArtist(artistDTO)
                .setMuseum(museumDTO)
                .setPhoto(photo == null || photo.length == 0
                        ? null
                        : new String(photo, StandardCharsets.UTF_8));
    }

    @Nonnull
    public static PaintingDTO toDTO(PaintingGrpcResponse grpcResponse) {
        return PaintingDTO.builder()
                .id(grpcResponse.getId().isEmpty()
                        ? null
                        : UUID.fromString(grpcResponse.getId()))
                .title(grpcResponse.getTitle().isBlank()
                        ? null
                        : grpcResponse.getTitle())
                .description(grpcResponse.getDescription().isBlank()
                        ? null
                        : grpcResponse.getDescription())
                .artist(grpcResponse.getArtist().getId().isBlank()
                        ? null
                        : ArtistDTO.builder()
                        .id(UUID.fromString(grpcResponse.getArtist().getId()))
                        .build())
                .museum(grpcResponse.getMuseum().getId().isBlank()
                        ? null
                        : MuseumDTO.builder()
                        .id(UUID.fromString(grpcResponse.getMuseum().getId()))
                        .build())
                .build();
    }

    @Nonnull
    public static AddPaintingGrpcRequest toGrpcRequest(PaintingDTO dto) {
        return AddPaintingGrpcRequest.newBuilder()
                .setTitle(dto.getTitle() == null
                        ? ""
                        : dto.getTitle())
                .setDescription(dto.getDescription() == null
                        ? ""
                        : dto.getDescription())
                .setArtistId(
                        dto.getArtist() == null || dto.getArtist().getId() == null
                                ? ""
                                : dto.getArtist().getId().toString()
                )
                .setMuseumId(dto.getMuseum() == null || dto.getMuseum().getId() == null
                        ? ""
                        : dto.getMuseum().getId().toString())
                .build();
    }

    @Nonnull
    public static UpdatePaintingGrpcRequest toUpdateGrpcRequest(PaintingDTO dto) {
        return UpdatePaintingGrpcRequest.newBuilder()
                .setId(dto.getId() == null
                        ? ""
                        : dto.getId().toString())
                .setTitle(dto.getTitle() == null
                        ? ""
                        : dto.getTitle())
                .setDescription(dto.getDescription() == null
                        ? ""
                        : dto.getDescription())
                .setArtistId(
                        dto.getArtist() == null || dto.getArtist().getId() == null
                                ? ""
                                : dto.getArtist().getId().toString()
                )
                .setMuseumId(dto.getMuseum() == null || dto.getMuseum().getId() == null
                        ? ""
                        : dto.getMuseum().getId().toString())
                .build();
    }

    @Nonnull
    public static PaintingsFilterGrpcRequest toFilter(@Nullable final String name,
                                                      @Nullable final UUID artistId,
                                                      final Pageable pageable) {
        return PaintingsFilterGrpcRequest.newBuilder()
                .setQuery(name == null
                        ? ""
                        : name)
                .setArtistId(artistId == null
                        ? ""
                        : artistId.toString())
                .setPageable(
                        PageableMapper.toPageableGrpc(pageable))
                .build();
    }

    @Nonnull
    public static Page<PaintingDTO> toPageDTO(final PaintingsGrpcResponse response) {
        return new PageImpl<>(
                response.getDataList().stream()
                        .map(PaintingMapper::toDTO)
                        .toList(),
                PageRequest.of(response.getCurrentPage(),
                        response.getItemsPerPage()),
                response.getTotalItems()
        );
    }

}

