package org.rococo.tests.mapper;

import org.rococo.grpc.countries.CountriesGrpcResponse;
import org.rococo.grpc.countries.CountryFilterGrpcRequest;
import org.rococo.grpc.countries.CountryGrpcResponse;
import org.rococo.tests.data.entity.CountryEntity;
import org.rococo.tests.enums.CountryCode;
import org.rococo.tests.model.CountryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class CountryMapper {

    private CountryMapper() {
    }

    @Nonnull
    public static CountryEntity fromDTO(CountryDTO dto) {
        return CountryEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .code(dto.getCode())
                .build();
    }

    @Nonnull
    public static CountryEntity updateFromDTO(CountryEntity entity, CountryDTO request) {
        return CountryEntity.builder()
                .id(entity.getId())
                .name(request.getName())
                .code(request.getCode())
                .build();
    }

    @Nonnull
    public static CountryDTO toDTO(CountryEntity entity) {
        return CountryDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .build();
    }

    @Nonnull
    public static CountryDTO toDTO(CountryGrpcResponse entity) {
        return CountryDTO.builder()
                .id(entity.getId().isEmpty()
                        ? null
                        : UUID.fromString(entity.getId()))
                .name(entity.getName())
                .code(CountryCode.valueOf(entity.getCode()))
                .build();
    }

    @Nonnull
    public static CountryFilterGrpcRequest toFilter(final Pageable pageable) {
        return CountryFilterGrpcRequest.newBuilder()
                .setPageable(
                        PageableMapper.toPageableGrpc(pageable))
                .build();
    }

    @Nonnull
    public static Page<CountryDTO> toPageDTO(final CountriesGrpcResponse response) {
        return new PageImpl<>(
                response.getDataList().stream()
                        .map(CountryMapper::toDTO)
                        .toList(),
                PageRequest.of(response.getCurrentPage(),
                        response.getItemsPerPage()),
                response.getTotalItems()
        );
    }

}

