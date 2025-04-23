package org.rococo.countries.service;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.countries.data.CountryEntity;
import org.rococo.countries.data.CountryRepository;
import org.rococo.grpc.common.page.DirectionGrpc;
import org.rococo.grpc.common.page.PageableGrpc;
import org.rococo.grpc.common.page.SortGrpc;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.IdsType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.countries.CountriesGrpcResponse;
import org.rococo.grpc.countries.CountryFilterGrpcRequest;
import org.rococo.grpc.countries.CountryGrpcResponse;
import org.rococo.grpc.countries.CountryListGrpcResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.rococo.countries.data.CountryCode.JP;

@ExtendWith(MockitoExtension.class)
@DisplayName("CountryGrpcService: Module tests")
class CountryGrpcServiceTests {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private StreamObserver<CountryGrpcResponse> countryResponseObserver;

    @Mock
    private StreamObserver<CountryListGrpcResponse> countryListResponseObserver;

    @Mock
    private StreamObserver<CountriesGrpcResponse> countriesResponseObserver;

    @InjectMocks
    private CountryGrpcService countryGrpcService;

    private UUID countryId;
    private CountryEntity countryEntity;
    private IdType idRequest;
    private NameType codeRequest;
    private IdsType idsRequest;
    private CountryFilterGrpcRequest filterRequest;

    @BeforeEach
    void setUp() {
        
        countryId = UUID.randomUUID();

        countryEntity = CountryEntity.builder()
                .id(countryId)
                .name("Japan")
                .code(JP)
                .build();

        idRequest = IdType.newBuilder()
                .setId(countryId.toString())
                .build();

        codeRequest = NameType.newBuilder()
                .setName("JP")
                .build();

        idsRequest = IdsType.newBuilder()
                .addId(countryId.toString())
                .build();

        filterRequest = CountryFilterGrpcRequest.newBuilder()
                .setName("Japan")
                .setPageable(PageableGrpc.newBuilder()
                        .setPage(0)
                        .setSize(10)
                        .setSort(SortGrpc.newBuilder()
                                .setOrder("name")
                                .setDirection(DirectionGrpc.ASC)
                                .build())
                        .build())
                .build();
    }

    @Test
    @DisplayName("FindById: returns country")
    void findById_Success() {
        
        // Stubs
        when(countryRepository.findById(countryId))
                .thenReturn(Optional.of(countryEntity));

        // Steps
        countryGrpcService.findById(idRequest, countryResponseObserver);

        // Assertions
        verify(countryResponseObserver).onNext(any(CountryGrpcResponse.class));
        verify(countryResponseObserver).onCompleted();
        verify(countryResponseObserver, never()).onError(any());
        
    }

    @Test
    @DisplayName("FindById: returns error when country not exists")
    void findById_ReturnsError_IfCountryDoesNotExist() {
        
        // Stubs
        when(countryRepository.findById(countryId))
                .thenReturn(Optional.empty());

        // Steps
        countryGrpcService.findById(idRequest, countryResponseObserver);

        // Assertions
        verify(countryResponseObserver).onError(any(StatusRuntimeException.class));
        verify(countryResponseObserver, never()).onNext(any());
        verify(countryResponseObserver, never()).onCompleted();
        
    }

    @Test
    @DisplayName("FindByCode: returns country")
    void findByCode_Success() {
        
        // Stubs
        when(countryRepository.findByCode(JP))
                .thenReturn(Optional.of(countryEntity));

        // Steps
        countryGrpcService.findByCode(codeRequest, countryResponseObserver);

        // Assertions
        verify(countryResponseObserver).onNext(any(CountryGrpcResponse.class));
        verify(countryResponseObserver).onCompleted();
        verify(countryResponseObserver, never()).onError(any());
        
    }

    @Test
    @DisplayName("FindByCode: returns error when country not exists")
    void findByCode_ReturnsError_IfCountryDoesNotExist() {
        
        // Stubs
        when(countryRepository.findByCode(JP))
                .thenReturn(Optional.empty());

        // Steps
        countryGrpcService.findByCode(codeRequest, countryResponseObserver);

        // Assertions
        verify(countryResponseObserver).onError(any(StatusRuntimeException.class));
        verify(countryResponseObserver, never()).onNext(any());
        verify(countryResponseObserver, never()).onCompleted();
        
    }

    @Test
    @DisplayName("FindAllByIds: returns countries")
    void findAllByIds_Success() {
        
        // Stubs
        when(countryRepository.findAllById(List.of(countryId)))
                .thenReturn(List.of(countryEntity));

        // Steps
        countryGrpcService.findAllByIds(idsRequest, countryListResponseObserver);

        // Assertions
        verify(countryListResponseObserver).onNext(any(CountryListGrpcResponse.class));
        verify(countryListResponseObserver).onCompleted();
        
    }

    @Test
    @DisplayName("FindAllByIds: returns empty list when no countries found")
    void findAllByIds_ReturnsEmptyList_IfNoCountriesFound() {
        
        // Stubs
        when(countryRepository.findAllById(List.of(countryId)))
                .thenReturn(List.of());

        // Steps
        countryGrpcService.findAllByIds(idsRequest, countryListResponseObserver);

        // Assertions
        verify(countryListResponseObserver).onNext(any(CountryListGrpcResponse.class));
        verify(countryListResponseObserver).onCompleted();
        
    }

    @Test
    @DisplayName("FindAll: returns countries")
    void findAll_Success() {
        
        // Data
        Page<CountryEntity> page = new PageImpl<>(List.of(countryEntity), PageRequest.of(0, 10), 1);

        // Stubs
        when(countryRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        // Steps
        countryGrpcService.findAll(filterRequest, countriesResponseObserver);

        // Assertions
        verify(countriesResponseObserver).onNext(any(CountriesGrpcResponse.class));
        verify(countriesResponseObserver).onCompleted();
        
    }

    @Test
    @DisplayName("FindAll: returns empty page when no countries found")
    void findAll_ReturnsEmptyPage_IfNoCountriesFound() {
        
        // Data
        Page<CountryEntity> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        // Stubs
        when(countryRepository.findAll(any(Pageable.class)))
                .thenReturn(emptyPage);

        // Steps
        countryGrpcService.findAll(filterRequest, countriesResponseObserver);

        // Assertions
        verify(countriesResponseObserver).onNext(any(CountriesGrpcResponse.class));
        verify(countriesResponseObserver).onCompleted();
        
    }

}
