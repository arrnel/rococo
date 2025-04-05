package org.rococo.gateway.mapper;

import org.rococo.gateway.model.artists.AddArtistRequestDTO;
import org.rococo.gateway.model.artists.ArtistDTO;
import org.rococo.gateway.model.artists.ArtistFindAllParamsValidationObject;
import org.rococo.gateway.model.artists.UpdateArtistRequestDTO;
import org.rococo.grpc.artists.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    public static AddArtistGrpcRequest toGrpcModel(final AddArtistRequestDTO requestDTO) {
        return AddArtistGrpcRequest.newBuilder()
                .setName(requestDTO.name() == null
                        ? ""
                        : requestDTO.name())
                .setBiography(requestDTO.biography() == null
                        ? ""
                        : requestDTO.biography())
                .build();
    }

    @Nonnull
    public static UpdateArtistGrpcRequest toGrpcModel(final UpdateArtistRequestDTO requestDTO) {
        return UpdateArtistGrpcRequest.newBuilder()
                .setId(requestDTO.id() == null
                        ? ""
                        : requestDTO.id().toString())
                .setName(requestDTO.name() == null
                        ? ""
                        : requestDTO.name())
                .setBiography(requestDTO.biography() == null
                        ? ""
                        : requestDTO.biography())
                .build();
    }

    @Nonnull
    public static ArtistDTO toDTO(final ArtistGrpcResponse grpcResponseModel) {
        return ArtistDTO.builder()
                .id(grpcResponseModel.getId().isEmpty()
                        ? null
                        : UUID.fromString(grpcResponseModel.getId()))
                .name(grpcResponseModel.getName().isEmpty()
                        ? null
                        : grpcResponseModel.getName())
                .biography(grpcResponseModel.getBiography().isEmpty()
                        ? null
                        : grpcResponseModel.getBiography())
                .build();
    }

    @Nonnull
    public static ArtistsFilterGrpcRequest toFilter(@Nullable final String name,
                                                    final Pageable pageable) {
        return ArtistsFilterGrpcRequest.newBuilder()
                .setQuery(name == null
                        ? ""
                        : name)
                .setPageable(
                        PageableMapper.toPageableGrpc(pageable))
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

    public static ArtistFindAllParamsValidationObject toRequestParamObj(final Map<String, String> requestParams, Pageable pageable) {
        return ArtistFindAllParamsValidationObject.builder()
                .name(requestParams.get("name"))
                .pageable(pageable)
                .build();
    }
}
