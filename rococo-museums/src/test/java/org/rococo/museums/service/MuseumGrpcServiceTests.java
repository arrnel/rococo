package org.rococo.museums.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.grpc.common.page.DirectionGrpc;
import org.rococo.grpc.common.page.PageableGrpc;
import org.rococo.grpc.common.page.SortGrpc;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.IdsType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.countries.CountryGrpcResponse;
import org.rococo.grpc.files.ImageGrpcResponse;
import org.rococo.grpc.museums.*;
import org.rococo.museums.client.CountriesGrpcClient;
import org.rococo.museums.client.FilesGrpcClient;
import org.rococo.museums.data.MuseumEntity;
import org.rococo.museums.data.MuseumRepository;
import org.rococo.museums.ex.CountryNotFoundException;
import org.rococo.museums.ex.MuseumAlreadyExistsException;
import org.rococo.museums.ex.MuseumNotFoundException;
import org.rococo.museums.specs.MuseumSpecs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MuseumGrpcService: Module tests")
class MuseumGrpcServiceTests {

    @Mock
    private MuseumRepository museumRepository;

    @Mock
    private MuseumSpecs museumSpecs;

    @Mock
    private FilesGrpcClient filesClient;

    @Mock
    private CountriesGrpcClient countriesClient;

    @Mock
    private StreamObserver<MuseumGrpcResponse> museumResponseObserver;

    @Mock
    private StreamObserver<MuseumListGrpcResponse> museumListResponseObserver;

    @Mock
    private StreamObserver<MuseumsGrpcResponse> museumsResponseObserver;

    @Mock
    private StreamObserver<Empty> emptyResponseObserver;

    @InjectMocks
    private MuseumGrpcService museumGrpcService;

    private UUID museumId;
    private UUID countryId;
    private MuseumEntity museumEntity;
    private CountryGrpcResponse country;
    private ImageGrpcResponse image;
    private AddMuseumGrpcRequest addRequest;
    private UpdateMuseumGrpcRequest updateRequest;
    private IdType idRequest;
    private NameType titleRequest;
    private MuseumsByIdsGrpcRequest byIdsRequest;
    private MuseumsFilterGrpcRequest filterRequest;

    @BeforeEach
    void setUp() {

        museumId = UUID.randomUUID();
        countryId = UUID.randomUUID();

        museumEntity = MuseumEntity.builder()
                .id(museumId)
                .title("Louvre")
                .description("Famous museum")
                .countryId(countryId)
                .city("Paris")
                .createdDate(LocalDateTime.now())
                .build();

        country = CountryGrpcResponse.newBuilder()
                .setId(countryId.toString())
                .setName("France")
                .build();

        image = ImageGrpcResponse.newBuilder()
                .setEntityId(museumId.toString())
                .setContent(ByteString.copyFromUtf8("image-data"))
                .build();

        addRequest = AddMuseumGrpcRequest.newBuilder()
                .setTitle("Louvre")
                .setDescription("Famous museum")
                .setCountryId(countryId.toString())
                .setCity("Paris")
                .setPhoto("image-data")
                .build();

        updateRequest = UpdateMuseumGrpcRequest.newBuilder()
                .setId(museumId.toString())
                .setTitle("Hermitage")
                .setDescription("Updated description")
                .setCountryId(countryId.toString())
                .setCity("Florence")
                .setPhoto("updated-image")
                .build();

        idRequest = IdType.newBuilder()
                .setId(museumId.toString())
                .build();

        titleRequest = NameType.newBuilder()
                .setName("Louvre")
                .build();

        byIdsRequest = MuseumsByIdsGrpcRequest.newBuilder()
                .setIds(IdsType.newBuilder().addId(museumId.toString()).build())
                .setOriginalPhoto(true)
                .build();

        filterRequest = MuseumsFilterGrpcRequest.newBuilder()
                .setQuery("Louvre")
                .setCountryId(countryId.toString())
                .setCity("Paris")
                .setOriginalPhoto(true)
                .setPageable(PageableGrpc.newBuilder()
                        .setPage(0)
                        .setSize(10)
                        .setSort(SortGrpc.newBuilder()
                                .setOrder("title")
                                .setDirection(DirectionGrpc.ASC)
                                .build())
                        .build())
                .build();

    }

    @Test
    @DisplayName("Add: add new museum")
    void add_Success() {

        // Stubs
        when(museumRepository.findByTitle("Louvre"))
                .thenReturn(Optional.empty());
        when(countriesClient.findById(countryId))
                .thenReturn(Optional.of(country));
        when(museumRepository.save(any(MuseumEntity.class)))
                .thenReturn(museumEntity);

        // Steps
        museumGrpcService.add(addRequest, museumResponseObserver);

        // Assertions
        verify(museumRepository).save(any(MuseumEntity.class));
        verify(filesClient).add(museumId, "image-data");
        verify(museumResponseObserver).onNext(any(MuseumGrpcResponse.class));
        verify(museumResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Add: throws MuseumAlreadyExistsException when title exists")
    void add_ThrowsMuseumAlreadyExistsException_IfMuseumWithSameTitleExists() {

        // Stubs
        when(museumRepository.findByTitle("Louvre"))
                .thenReturn(Optional.of(museumEntity));

        // Steps & Assertions
        assertThrows(MuseumAlreadyExistsException.class, () ->
                museumGrpcService.add(addRequest, museumResponseObserver));

        verify(museumRepository, never()).save(any());
        verify(filesClient, never()).add(any(), any());
        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("Add: throws CountryNotFoundException when country not found")
    void add_ThrowsCountryNotFoundException_IfCountryNotFound() {

        // Stubs
        when(museumRepository.findByTitle("Louvre"))
                .thenReturn(Optional.empty());
        when(countriesClient.findById(countryId))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(CountryNotFoundException.class, () ->
                museumGrpcService.add(addRequest, museumResponseObserver));

        verify(museumRepository, never()).save(any());
        verify(filesClient, never()).add(any(), any());
        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("FindById: returns museum")
    void findById_Success() {

        // Stubs
        when(museumRepository.findById(museumId))
                .thenReturn(Optional.of(museumEntity));
        when(countriesClient.findById(countryId))
                .thenReturn(Optional.of(country));
        when(filesClient.findImage(museumId))
                .thenReturn(Optional.of(image));

        // Steps
        museumGrpcService.findById(idRequest, museumResponseObserver);

        // Assertions
        verify(museumResponseObserver).onNext(any(MuseumGrpcResponse.class));
        verify(museumResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("FindById: throws MuseumNotFoundException when museum not exists")
    void findById_ThrowsMuseumNotFoundException_IfMuseumDoesNotExist() {

        // Stubs
        when(museumRepository.findById(museumId))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(MuseumNotFoundException.class, () ->
                museumGrpcService.findById(idRequest, museumResponseObserver));

        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("FindByTitle: returns museum")
    void findByTitle_Success() {

        // Stubs
        when(museumRepository.findByTitle("Louvre"))
                .thenReturn(Optional.of(museumEntity));
        when(countriesClient.findById(countryId))
                .thenReturn(Optional.of(country));
        when(filesClient.findImage(museumId))
                .thenReturn(Optional.of(image));

        // Steps
        museumGrpcService.findByTitle(titleRequest, museumResponseObserver);

        // Assertions
        verify(museumResponseObserver).onNext(any(MuseumGrpcResponse.class));
        verify(museumResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("FindByTitle: throws MuseumNotFoundException when museum not exists")
    void findByTitle_ThrowsMuseumNotFoundException_IfMuseumDoesNotExist() {

        // Stubs
        when(museumRepository.findByTitle("Louvre"))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(MuseumNotFoundException.class, () ->
                museumGrpcService.findByTitle(titleRequest, museumResponseObserver));

        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("FindAllByIds: returns museums by expected ids")
    void findAllByIds_Success() {

        // Stubs
        when(museumRepository.findAllById(List.of(museumId)))
                .thenReturn(List.of(museumEntity));
        when(countriesClient.findAllByIds(List.of(countryId)))
                .thenReturn(List.of(country));
        when(filesClient.findAllByIds(List.of(museumId), true))
                .thenReturn(List.of(image));

        // Steps
        museumGrpcService.findAllByIds(byIdsRequest, museumListResponseObserver);

        // Assertions
        verify(museumListResponseObserver).onNext(any(MuseumListGrpcResponse.class));
        verify(museumListResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("FindAll: returns museums")
    void findAll_Success() {

        // Data
        final Page<MuseumEntity> page = new PageImpl<>(List.of(museumEntity), PageRequest.of(0, 10), 1);

        // Stubs
        when(museumRepository.findAll(ArgumentMatchers.<Specification<MuseumEntity>>any(), any(Pageable.class)))
                .thenReturn(page);
        when(countriesClient.findAllByIds(List.of(countryId)))
                .thenReturn(List.of(country));
        when(filesClient.findAllByIds(List.of(museumId), true))
                .thenReturn(List.of(image));

        // Steps
        museumGrpcService.findAll(filterRequest, museumsResponseObserver);

        // Assertions
        verify(museumsResponseObserver).onNext(any(MuseumsGrpcResponse.class));
        verify(museumsResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: update museum with same title and send request to update photo")
    void update_Success_IfMuseumTitleUnchanged() {

        // Data
        final var sameTitleRequest = UpdateMuseumGrpcRequest.newBuilder()
                .setId(museumId.toString())
                .setTitle("Louvre")
                .setDescription("Updated description")
                .setCountryId(countryId.toString())
                .setCity("Paris")
                .setPhoto("updated-image")
                .build();

        // Stubs
        when(museumRepository.findById(museumId))
                .thenReturn(Optional.of(museumEntity));
        when(museumRepository.findByTitle(sameTitleRequest.getTitle()))
                .thenReturn(Optional.of(museumEntity));
        when(countriesClient.findById(countryId))
                .thenReturn(Optional.of(country));
        when(museumRepository.save(any(MuseumEntity.class)))
                .thenReturn(museumEntity);
        when(filesClient.findImage(museumId))
                .thenReturn(Optional.of(image));

        // Steps
        museumGrpcService.update(sameTitleRequest, museumResponseObserver);

        // Assertions
        verify(museumRepository).save(any(MuseumEntity.class));
        verify(filesClient).update(museumId, sameTitleRequest.getPhoto());
        verify(museumResponseObserver).onNext(any(MuseumGrpcResponse.class));
        verify(museumResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: update museum with new not taken title")
    void update_Success_IfNewMuseumTitleNotTaken() {

        // Stubs
        when(museumRepository.findById(museumId))
                .thenReturn(Optional.of(museumEntity));
        when(museumRepository.findByTitle(updateRequest.getTitle()))
                .thenReturn(Optional.empty());
        when(countriesClient.findById(countryId))
                .thenReturn(Optional.of(country));
        when(museumRepository.save(any(MuseumEntity.class)))
                .thenReturn(museumEntity);
        when(filesClient.findImage(museumId))
                .thenReturn(Optional.of(image));

        // Steps
        museumGrpcService.update(updateRequest, museumResponseObserver);

        // Assertions
        verify(museumRepository).save(any(MuseumEntity.class));
        verify(filesClient).update(museumId, "updated-image");
        verify(museumResponseObserver).onNext(any(MuseumGrpcResponse.class));
        verify(museumResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: updates museum and send request to remove photo when request doesn't have photo and rococo-files have photo")
    void update_Success_IfMuseumPhotoExists_AndRequestNotContainsPhoto() {

        // Data
        final var noPhotoRequest = UpdateMuseumGrpcRequest.newBuilder()
                .setId(museumId.toString())
                .setTitle("Hermitage")
                .setDescription("Updated description")
                .setCountryId(countryId.toString())
                .setCity("Florence")
                .setPhoto("")
                .build();

        // Stubs
        when(museumRepository.findById(museumId))
                .thenReturn(Optional.of(museumEntity));
        when(museumRepository.findByTitle("Hermitage"))
                .thenReturn(Optional.empty());
        when(countriesClient.findById(countryId))
                .thenReturn(Optional.of(country));
        when(museumRepository.save(any(MuseumEntity.class)))
                .thenReturn(museumEntity);
        when(filesClient.findImage(museumId))
                .thenReturn(Optional.of(image));

        // Steps
        museumGrpcService.update(noPhotoRequest, museumResponseObserver);

        // Assertions
        verify(museumRepository).save(any(MuseumEntity.class));
        verify(filesClient).delete(museumId);
        verify(museumResponseObserver).onNext(any(MuseumGrpcResponse.class));
        verify(museumResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: update museum and send request to add photo when request contains photo and rococo-files doesn't have photo")
    void update_Success_IfMuseumPhotoNotExists_AndRequestContainsPhoto() {

        // Data
        final var newPhotoRequest = UpdateMuseumGrpcRequest.newBuilder()
                .setId(museumId.toString())
                .setTitle("Hermitage")
                .setDescription("Updated description")
                .setCountryId(countryId.toString())
                .setCity("Florence")
                .setPhoto("new-image")
                .build();

        // Stubs
        when(museumRepository.findById(museumId))
                .thenReturn(Optional.of(museumEntity));
        when(museumRepository.findByTitle("Hermitage"))
                .thenReturn(Optional.empty());
        when(countriesClient.findById(countryId))
                .thenReturn(Optional.of(country));
        when(museumRepository.save(any(MuseumEntity.class)))
                .thenReturn(museumEntity);
        when(filesClient.findImage(museumId))
                .thenReturn(Optional.empty());

        // Steps
        museumGrpcService.update(newPhotoRequest, museumResponseObserver);

        // Assertions
        verify(museumRepository).save(any(MuseumEntity.class));
        verify(filesClient).add(museumId, "new-image");
        verify(museumResponseObserver).onNext(any(MuseumGrpcResponse.class));
        verify(museumResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: throws MuseumAlreadyExistsException when new title is taken")
    void update_ThrowsMuseumAlreadyExistsException_IfNewTitleTaken() {

        // Stubs
        final var otherMuseum = MuseumEntity.builder()
                .id(UUID.randomUUID())
                .title("Hermitage")
                .build();

        when(museumRepository.findById(museumId))
                .thenReturn(Optional.of(museumEntity));
        when(museumRepository.findByTitle(otherMuseum.getTitle()))
                .thenReturn(Optional.of(otherMuseum));

        // Steps & Assertions
        assertThrows(MuseumAlreadyExistsException.class, () ->
                museumGrpcService.update(updateRequest, museumResponseObserver));

        verify(museumRepository, never()).save(any());
        verify(filesClient, never()).update(any(), any());
        verify(filesClient, never()).add(any(), any());
        verify(filesClient, never()).delete(any());
        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("Update: throws MuseumNotFoundException when museum not found")
    void update_ThrowsMuseumNotFoundException_IfMuseumNotFound() {

        // Stubs
        when(museumRepository.findById(museumId))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(MuseumNotFoundException.class, () ->
                museumGrpcService.update(updateRequest, museumResponseObserver));

        verify(museumRepository, never()).save(any());
        verify(filesClient, never()).update(any(), any());
        verify(filesClient, never()).add(any(), any());
        verify(filesClient, never()).delete(any());
        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("Update: throws CountryNotFoundException when country not found")
    void update_ThrowsCountryNotFoundException_IfCountryNotFound() {

        // Stubs
        when(museumRepository.findById(museumId))
                .thenReturn(Optional.of(museumEntity));
        when(museumRepository.findByTitle("Hermitage"))
                .thenReturn(Optional.empty());
        when(countriesClient.findById(countryId))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(CountryNotFoundException.class, () ->
                museumGrpcService.update(updateRequest, museumResponseObserver));

        verify(museumRepository, never()).save(any());
        verify(filesClient, never()).update(any(), any());
        verify(filesClient, never()).add(any(), any());
        verify(filesClient, never()).delete(any());
        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("RemoveById: deletes museum and sends delete photo request when museum found")
    void removeById_Success() {

        // Steps
        museumGrpcService.removeById(idRequest, emptyResponseObserver);

        // Assertions
        verify(museumRepository).deleteById(museumId);
        verify(filesClient).delete(museumId);
        verify(emptyResponseObserver).onNext(Empty.newBuilder().build());
        verify(emptyResponseObserver).onCompleted();

    }

}
