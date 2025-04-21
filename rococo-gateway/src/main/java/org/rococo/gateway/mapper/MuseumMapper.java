package org.rococo.gateway.mapper;

import org.rococo.gateway.model.countries.LocationResponseDTO;
import org.rococo.gateway.model.museums.AddMuseumRequestDTO;
import org.rococo.gateway.model.museums.MuseumDTO;
import org.rococo.gateway.model.museums.MuseumFindAllParamsValidationObject;
import org.rococo.gateway.model.museums.UpdateMuseumRequestDTO;
import org.rococo.grpc.museums.*;
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
public class MuseumMapper {

    private MuseumMapper() {
    }

    @Nonnull
    public static AddMuseumGrpcRequest toGrpcModel(final AddMuseumRequestDTO requestDTO) {
        return AddMuseumGrpcRequest.newBuilder()
                .setTitle(requestDTO.title() == null
                        ? ""
                        : requestDTO.title())
                .setDescription(requestDTO.description() == null
                        ? ""
                        : requestDTO.description())
                .setCountryId(requestDTO.location().country().id() == null
                        ? ""
                        : requestDTO.location().country().id().toString())
                .setCity(requestDTO.location().city() == null
                        ? ""
                        : requestDTO.location().city())
                .setPhoto(requestDTO.photo() == null
                        ? ""
                        : requestDTO.photo())
                .build();
    }

    @Nonnull
    public static UpdateMuseumGrpcRequest toGrpcModel(final UpdateMuseumRequestDTO requestDTO) {
        return UpdateMuseumGrpcRequest.newBuilder()
                .setId(requestDTO.id().toString())
                .setTitle(requestDTO.title() == null
                        ? ""
                        : requestDTO.title())
                .setDescription(requestDTO.description() == null
                        ? ""
                        : requestDTO.description())
                .setCountryId(requestDTO.location().country().id() == null
                        ? ""
                        : requestDTO.location().country().id().toString())
                .setCity(requestDTO.location().city() == null
                        ? ""
                        : requestDTO.location().city())
                .setPhoto(requestDTO.photo() == null
                        ? ""
                        : requestDTO.photo())
                .build();
    }

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
                        LocationResponseDTO.builder()
                                .city(grpcResponseModel.getCity().isEmpty()
                                        ? null
                                        : grpcResponseModel.getCity())
                                .country(CountryMapper.toDTO(grpcResponseModel.getCountry()))
                                .build())
                .photo(grpcResponseModel.getPhoto().isEmpty()
                        ? null
                        : grpcResponseModel.getPhoto())
                .build();
    }

    @Nonnull
    public static MuseumDTO toDTO(final MuseumShortGrpcResponse grpcResponseModel) {
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
                        LocationResponseDTO.builder()
                                .city(grpcResponseModel.getCity().isEmpty()
                                        ? null
                                        : grpcResponseModel.getCity())
                                .country(CountryMapper.toDTO(grpcResponseModel.getCountry()))
                                .build())
                .build();
    }

    @Nonnull
    public static MuseumsFilterGrpcRequest toFilter(@Nullable final String name,
                                                    final boolean isOriginalPhoto,
                                                    final Pageable pageable) {
        return MuseumsFilterGrpcRequest.newBuilder()
                .setQuery(
                        name == null
                                ? ""
                                : name
                )
                .setOriginalPhoto(isOriginalPhoto)
                .setPageable(
                        PageableMapper.toPageableGrpc(pageable))
                .build();
    }

    @Nonnull
    public static Page<MuseumDTO> toPageDTO(final MuseumsGrpcResponse response) {
        return new PageImpl<>(
                response.getDataList().stream()
                        .map(MuseumMapper::toDTO)
                        .toList(),
                PageRequest.of(response.getCurrentPage(),
                        response.getItemsPerPage()),
                response.getTotalItems()
        );
    }

    public static MuseumFindAllParamsValidationObject toRequestParamObj(final Map<String, String> requestParams, Pageable pageable) {
        return MuseumFindAllParamsValidationObject.builder()
                .title(requestParams.get("title"))
                .pageable(pageable)
                .build();
    }

}
