package org.rococo.tests.mapper;

import org.rococo.grpc.museums.*;
import org.rococo.tests.data.entity.CountryEntity;
import org.rococo.tests.data.entity.MuseumEntity;
import org.rococo.tests.enums.CountryCode;
import org.rococo.tests.jupiter.annotation.Museum;
import org.rococo.tests.model.CountryDTO;
import org.rococo.tests.model.LocationDTO;
import org.rococo.tests.model.MuseumDTO;
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
public class MuseumMapper {

    private MuseumMapper() {
    }

    @Nonnull
    public static MuseumEntity fromDTO(MuseumDTO dto) {
        return MuseumEntity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .countryId(dto.getLocation().getCountry().getId())
                .city(dto.getLocation().getCity())
                .build();
    }

    @Nonnull
    public static MuseumEntity updateFromDTO(MuseumEntity entity, MuseumDTO dto) {
        return MuseumEntity.builder()
                .id(entity.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .countryId(dto.getLocation().getCountry().getId())
                .city(dto.getLocation().getCity())
                .build();
    }

    @Nonnull
    public static MuseumDTO updateFromAnno(MuseumDTO museumDTO, Museum anno) {
        return MuseumDTO.builder()
                .id(museumDTO.getId())
                .title(anno.title().isEmpty()
                        ? museumDTO.getTitle()
                        : anno.title())
                .description(anno.description().isEmpty() && !anno.descriptionEmpty()
                        ? museumDTO.getDescription()
                        : anno.description())
                .location(LocationDTO.builder()
                        .city(anno.city().isEmpty()
                                ? museumDTO.getLocation().getCity()
                                : anno.city())
                        .country(CountryDTO.builder()
                                .code(anno.countryCode() == CountryCode.EMPTY
                                        ? museumDTO.getLocation().getCountry().getCode()
                                        : anno.countryCode())
                                .build())
                        .build())
                .photo(anno.pathToPhoto().isEmpty()
                        ? ImageUtil.generateImage()
                        : ImageUtil.imageFileToBase64(Path.of(anno.pathToPhoto())))
                .build();
    }

    @Nonnull
    public static MuseumDTO toDTO(MuseumEntity entity) {

        return MuseumDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .location(new LocationDTO(
                        entity.getCity(),
                        new CountryDTO(entity.getCountryId(), null, null)))
                .build();

    }

    @Nonnull
    public static AddMuseumGrpcRequest toGrpcRequest(MuseumDTO dto) {

        return AddMuseumGrpcRequest.newBuilder()
                .setTitle(dto.getTitle() == null
                        ? ""
                        : dto.getTitle())
                .setDescription(dto.getDescription() == null
                        ? ""
                        : dto.getDescription())
                .setCity(dto.getLocation().getCity() == null
                        ? ""
                        : dto.getLocation().getCity())
                .setCountryId(dto.getLocation().getCountry().getId() == null
                        ? ""
                        : dto.getLocation().getCountry().getId().toString())
                .setPhoto(dto.getPhoto() == null
                        ? ""
                        : dto.getPhoto())
                .build();

    }

    @Nonnull
    public static UpdateMuseumGrpcRequest toUpdateGrpcRequest(MuseumDTO dto) {

        return UpdateMuseumGrpcRequest.newBuilder()
                .setId(dto.getId() == null
                        ? ""
                        : dto.getId().toString())
                .setTitle(dto.getTitle() == null
                        ? ""
                        : dto.getTitle())
                .setDescription(dto.getDescription() == null
                        ? ""
                        : dto.getDescription())
                .setCity(dto.getLocation() == null || dto.getLocation().getCity() == null
                        ? ""
                        : dto.getLocation().getCity())
                .setCountryId(dto.getLocation() == null || dto.getLocation().getCountry() == null || dto.getLocation().getCountry().getId() == null
                        ? ""
                        : dto.getLocation().getCountry().getId().toString())
                .setPhoto(dto.getPhoto() == null
                        ? ""
                        : dto.getPhoto())
                .build();

    }

    @Nonnull
    public static MuseumDTO toDTO(MuseumGrpcResponse grpcResponse) {

        return MuseumDTO.builder()
                .id(grpcResponse.getId().isEmpty()
                        ? null
                        : UUID.fromString(grpcResponse.getId()))
                .title(grpcResponse.getTitle().isEmpty()
                        ? null
                        : grpcResponse.getTitle())
                .description(grpcResponse.getDescription().isEmpty()
                        ? null
                        : grpcResponse.getDescription())
                .location(new LocationDTO(
                        grpcResponse.getCity(),
                        CountryMapper.toDTO(grpcResponse.getCountry())))
                .photo(grpcResponse.getPhoto().isEmpty()
                        ? null
                        : grpcResponse.getPhoto())
                .build();

    }

    @Nonnull
    public static MuseumDTO toDTO(MuseumEntity entity, CountryEntity country, @Nullable byte[] image) {
        return MuseumDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .location(
                        new LocationDTO(
                                entity.getCity(),
                                CountryMapper.toDTO(country)))
                .photo(image == null || image.length == 0
                        ? null
                        : new String(image, StandardCharsets.UTF_8))
                .build();
    }

    @Nonnull
    public static MuseumsFilterGrpcRequest toFilter(@Nullable final String name,
                                                    final Pageable pageable) {
        return MuseumsFilterGrpcRequest.newBuilder()
                .setQuery(name == null
                        ? ""
                        : name)
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

}

