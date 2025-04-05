package org.rococo.gateway.mapper;

import org.rococo.gateway.model.countries.CountryDTO;
import org.rococo.gateway.model.countries.CountryFindAllParamsValidationObject;
import org.rococo.grpc.countries.CountriesGrpcResponse;
import org.rococo.grpc.countries.CountryFilterGrpcRequest;
import org.rococo.grpc.countries.CountryGrpcResponse;
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
    public static CountryDTO toDTO(final CountryGrpcResponse grpcResponseModel) {
        return CountryDTO.builder()
                .id(grpcResponseModel.getId().isEmpty()
                        ? null
                        : UUID.fromString(grpcResponseModel.getId()))
                .name(grpcResponseModel.getName().isEmpty()
                        ? null
                        : grpcResponseModel.getName())
                .code(grpcResponseModel.getCode().isEmpty()
                        ? null
                        : grpcResponseModel.getCode())
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

    public static CountryFindAllParamsValidationObject toRequestParamObj(Pageable pageable) {
        return CountryFindAllParamsValidationObject.builder()
                .pageable(pageable)
                .build();
    }

}
