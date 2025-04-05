package org.rococo.gateway.mapper;

import com.google.protobuf.ByteString;
import org.rococo.gateway.model.artists.ArtistDTO;
import org.rococo.gateway.model.museums.MuseumDTO;
import org.rococo.gateway.model.paintings.AddPaintingRequestDTO;
import org.rococo.gateway.model.paintings.PaintingFindAllParamsValidationObject;
import org.rococo.gateway.model.paintings.PaintingDTO;
import org.rococo.gateway.model.paintings.UpdatePaintingRequestDTO;
import org.rococo.grpc.paintings.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class PaintingMapper {

    private PaintingMapper() {
    }

    @Nonnull
    public static AddPaintingGrpcRequest toGrpcModel(final AddPaintingRequestDTO requestDTO) {
        return AddPaintingGrpcRequest.newBuilder()
                .setTitle(requestDTO.title() == null
                        ? ""
                        : requestDTO.title())
                .setDescription(requestDTO.description() == null
                        ? ""
                        : requestDTO.description())
                .setArtistId(requestDTO.artist() == null
                        ? ""
                        : requestDTO.artist().id().toString())
                .setMuseumId(requestDTO.museum() == null
                        ? ""
                        : requestDTO.museum().id().toString())
                .setPhoto(requestDTO.photo() == null
                        ? ByteString.EMPTY
                        : ByteString.copyFrom(requestDTO.photo(), StandardCharsets.UTF_8))
                .build();
    }

    @Nonnull
    public static UpdatePaintingGrpcRequest toGrpcModel(final UpdatePaintingRequestDTO requestDTO
    ) {
        return UpdatePaintingGrpcRequest.newBuilder()
                .setId(requestDTO.id() == null
                        ? ""
                        : requestDTO.id().toString())
                .setTitle(requestDTO.title() == null
                        ? ""
                        : requestDTO.title())
                .setDescription(requestDTO.description() == null
                        ? ""
                        : requestDTO.description())
                .setArtistId(requestDTO.artist() == null
                        ? ""
                        : requestDTO.artist().id().toString())
                .setMuseumId(requestDTO.museum() == null
                        ? ""
                        : requestDTO.museum().id().toString())
                .setPhoto(requestDTO.photo() == null
                        ? ByteString.EMPTY
                        : ByteString.copyFrom(requestDTO.photo(), StandardCharsets.UTF_8))
                .build();
    }

    @Nonnull
    public static PaintingDTO toDTO(final PaintingGrpcResponse grpcResponseModel) {
        return PaintingDTO.builder()
                .id(grpcResponseModel.getId().isEmpty()
                        ? null
                        : UUID.fromString(grpcResponseModel.getId()))
                .title(grpcResponseModel.getTitle().isEmpty()
                        ? null
                        : grpcResponseModel.getTitle())
                .description(grpcResponseModel.getDescription().isEmpty()
                        ? null
                        : grpcResponseModel.getDescription())
                .artist(ArtistDTO.builder()
                        .id(grpcResponseModel.getArtistId().isEmpty()
                                ? null
                                : UUID.fromString(grpcResponseModel.getArtistId()))
                        .build())
                .museum(MuseumDTO.builder()
                        .id(grpcResponseModel.getMuseumId().isEmpty()
                                ? null
                                : UUID.fromString(grpcResponseModel.getMuseumId()))
                        .build())
                .photo(grpcResponseModel.getPhoto().isEmpty()
                        ? null
                        : grpcResponseModel.getPhoto().toString(StandardCharsets.UTF_8))
                .build();
    }

    @Nonnull
    public static PaintingsFilterGrpcRequest toFilter(@Nullable final String name,
                                                      @Nullable final UUID artistId,
                                                      final Pageable pageable
    ) {
        return PaintingsFilterGrpcRequest.newBuilder()
                .setQuery(
                        name == null
                                ? ""
                                : name)
                .setArtistId(
                        artistId == null
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

    public static PaintingFindAllParamsValidationObject toRequestParamObj(final Map<String, String> requestParams, Pageable pageable) {
        return PaintingFindAllParamsValidationObject.builder()
                .title(requestParams.get("title"))
                .artistId(requestParams.get("authorId") == null
                        ? null
                        : UUID.fromString(requestParams.get("authorId")))
                .pageable(pageable)
                .build();
    }

}
