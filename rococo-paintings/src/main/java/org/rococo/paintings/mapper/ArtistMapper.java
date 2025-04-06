package org.rococo.paintings.mapper;

import org.rococo.grpc.artists.ArtistGrpcResponse;
import org.rococo.paintings.model.ArtistDTO;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ArtistMapper {

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

}
