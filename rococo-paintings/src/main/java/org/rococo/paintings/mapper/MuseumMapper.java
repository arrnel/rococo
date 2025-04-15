package org.rococo.paintings.mapper;

import org.rococo.grpc.museums.MuseumGrpcResponse;
import org.rococo.paintings.model.CountryDTO;
import org.rococo.paintings.model.LocationDTO;
import org.rococo.paintings.model.MuseumDTO;

import javax.annotation.Nonnull;
import java.util.UUID;

public class MuseumMapper {

    @Nonnull
    public static MuseumDTO toDTO(final MuseumGrpcResponse grpcResponseModel) {
        return MuseumDTO.builder()
                .id(grpcResponseModel.getId().isEmpty()
                        ? null
                        : UUID.fromString(grpcResponseModel.getId()))
                .title(grpcResponseModel.getTitle().isEmpty()
                        ? null
                        : grpcResponseModel.getTitle())
                .description(grpcResponseModel.getDescription().isEmpty()
                        ? null
                        : grpcResponseModel.getDescription())
                .location(
                        LocationDTO.builder()
                                .city(grpcResponseModel.getCity().isEmpty()
                                        ? null
                                        : grpcResponseModel.getCity())
                                .country(CountryDTO.builder()
                                        .id(grpcResponseModel.getCountry().getId().isEmpty()
                                                ? null
                                                : UUID.fromString(grpcResponseModel.getCountry().getId()))
                                        .build())
                                .build()
                )
                .build();
    }

}
