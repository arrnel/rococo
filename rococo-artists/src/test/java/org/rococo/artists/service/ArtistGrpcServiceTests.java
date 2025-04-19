package org.rococo.artists.service;

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
import org.rococo.artists.client.FilesGrpcClient;
import org.rococo.artists.data.ArtistEntity;
import org.rococo.artists.data.ArtistRepository;
import org.rococo.artists.ex.ArtistAlreadyExistsException;
import org.rococo.artists.ex.ArtistNotFoundException;
import org.rococo.artists.specs.ArtistSpecs;
import org.rococo.grpc.artists.*;
import org.rococo.grpc.common.page.DirectionGrpc;
import org.rococo.grpc.common.page.PageableGrpc;
import org.rococo.grpc.common.page.SortGrpc;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.IdsType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.files.ImageGrpcResponse;
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
@DisplayName("ArtistGrpcService: Module tests")
class ArtistGrpcServiceTests {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private ArtistSpecs artistSpecs;

    @Mock
    private FilesGrpcClient filesClient;

    @Mock
    private StreamObserver<ArtistGrpcResponse> artistResponseObserver;

    @Mock
    private StreamObserver<ArtistListGrpcResponse> artistListResponseObserver;

    @Mock
    private StreamObserver<ArtistsGrpcResponse> artistsResponseObserver;

    @Mock
    private StreamObserver<Empty> emptyResponseObserver;

    @InjectMocks
    private ArtistGrpcService artistGrpcService;

    private UUID artistId;
    private ArtistEntity artistEntity;
    private ImageGrpcResponse image;
    private AddArtistGrpcRequest addRequest;
    private UpdateArtistGrpcRequest updateRequest;
    private IdType idRequest;
    private NameType nameRequest;
    private ArtistsByIdsGrpcRequest byIdsRequest;
    private ArtistsFilterGrpcRequest filterRequest;

    @BeforeEach
    void setUp() {

        artistId = UUID.randomUUID();

        artistEntity = ArtistEntity.builder()
                .id(artistId)
                .name("Leonardo da Vinci")
                .biography("Renaissance artist")
                .createdDate(LocalDateTime.now())
                .build();

        image = ImageGrpcResponse.newBuilder()
                .setEntityId(artistId.toString())
                .setContent(ByteString.copyFromUtf8("image-data"))
                .build();

        addRequest = AddArtistGrpcRequest.newBuilder()
                .setName("Leonardo da Vinci")
                .setBiography("Renaissance artist")
                .setPhoto("image-data")
                .build();

        updateRequest = UpdateArtistGrpcRequest.newBuilder()
                .setId(artistId.toString())
                .setName("Michelangelo")
                .setBiography("Updated biography")
                .setPhoto("updated-image")
                .build();

        idRequest = IdType.newBuilder()
                .setId(artistId.toString())
                .build();

        nameRequest = NameType.newBuilder()
                .setName("Leonardo da Vinci")
                .build();

        byIdsRequest = ArtistsByIdsGrpcRequest.newBuilder()
                .setIds(IdsType.newBuilder().addId(artistId.toString()).build())
                .setOriginalPhoto(true)
                .build();

        filterRequest = ArtistsFilterGrpcRequest.newBuilder()
                .setQuery("Leonardo")
                .setOriginalPhoto(true)
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
    @DisplayName("Add: add new artist")
    void add_Success() {

        // Stubs
        when(artistRepository.findByName("Leonardo da Vinci"))
                .thenReturn(Optional.empty());
        when(artistRepository.save(any(ArtistEntity.class)))
                .thenReturn(artistEntity);

        // Steps
        artistGrpcService.add(addRequest, artistResponseObserver);

        // Assertions
        verify(artistRepository).save(any(ArtistEntity.class));
        verify(filesClient).add(artistId, "image-data");
        verify(artistResponseObserver).onNext(any(ArtistGrpcResponse.class));
        verify(artistResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Add: add artist throws ArtistAlreadyExistsException when name is already taken")
    void add_ThrowsArtistAlreadyExistsException_IfArtistWithSameNameExists() {

        // Stubs
        when(artistRepository.findByName("Leonardo da Vinci"))
                .thenReturn(Optional.of(artistEntity));

        // Steps & Assertions
        assertThrows(ArtistAlreadyExistsException.class, () ->
                artistGrpcService.add(addRequest, artistResponseObserver));

        verify(artistRepository, never()).save(any());
        verify(filesClient, never()).add(any(), any());
        verify(artistResponseObserver, never()).onNext(any());
        verify(artistResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("FindById: returns artist")
    void findById_Success() {

        // Stubs
        when(artistRepository.findById(artistId))
                .thenReturn(Optional.of(artistEntity));
        when(filesClient.findImage(artistId))
                .thenReturn(Optional.of(image));

        // Steps
        artistGrpcService.findById(idRequest, artistResponseObserver);

        // Assertions
        verify(artistResponseObserver).onNext(any(ArtistGrpcResponse.class));
        verify(artistResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("FindById: throws ArtistNotFoundException when artist not exists")
    void findById_ThrowsArtistNotFoundException_IfArtistDoesNotExist() {

        // Stubs
        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(ArtistNotFoundException.class, () ->
                artistGrpcService.findById(idRequest, artistResponseObserver));

        verify(artistResponseObserver, never()).onNext(any());
        verify(artistResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("FindByName: returns artist")
    void findByName_Success() {

        // Stubs
        when(artistRepository.findByName("Leonardo da Vinci"))
                .thenReturn(Optional.of(artistEntity));
        when(filesClient.findImage(artistId))
                .thenReturn(Optional.of(image));

        // Steps
        artistGrpcService.findByName(nameRequest, artistResponseObserver);

        // Assertions
        verify(artistResponseObserver).onNext(any(ArtistGrpcResponse.class));
        verify(artistResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("FindByName: throws ArtistNotFoundException when artist not exists")
    void findByName_ThrowsArtistNotFoundException_IfArtistDoesNotExist() {

        // Stubs
        when(artistRepository.findByName("Leonardo da Vinci"))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(ArtistNotFoundException.class, () ->
                artistGrpcService.findByName(nameRequest, artistResponseObserver));

        verify(artistResponseObserver, never()).onNext(any());
        verify(artistResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("FindByName: throws IllegalArgumentException when name is empty")
    void findByName_ThrowsIllegalArgumentException_IfNameIsEmpty() {

        // Data
        NameType emptyNameRequest = NameType.newBuilder().setName("").build();

        // Steps & Assertions
        assertThrows(IllegalArgumentException.class, () ->
                artistGrpcService.findByName(emptyNameRequest, artistResponseObserver));

        verify(artistRepository, never()).findByName(any());
        verify(filesClient, never()).findImage(any());
        verify(artistResponseObserver, never()).onNext(any());
        verify(artistResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("FindAllByIds: returns artists with expected ids if exists")
    void findAllByIds_Success() {

        // Stubs
        when(artistRepository.findAllById(List.of(artistId)))
                .thenReturn(List.of(artistEntity));
        when(filesClient.findAllByIds(List.of(artistId), true))
                .thenReturn(List.of(image));

        // Steps
        artistGrpcService.findAllByIds(byIdsRequest, artistListResponseObserver);

        // Assertions
        verify(artistListResponseObserver).onNext(any(ArtistListGrpcResponse.class));
        verify(artistListResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("FindAll: returns artists")
    void findAll_Success() {

        // Data
        Page<ArtistEntity> page = new PageImpl<>(List.of(artistEntity), PageRequest.of(0, 10), 1);

        // Stubs
        when(artistRepository.findAll(ArgumentMatchers.<Specification<ArtistEntity>>any(), any(Pageable.class)))
                .thenReturn(page);
        when(filesClient.findAllByIds(List.of(artistId), true))
                .thenReturn(List.of(image));

        // Steps
        artistGrpcService.findAll(filterRequest, artistsResponseObserver);

        // Assertions
        verify(artistsResponseObserver).onNext(any(ArtistsGrpcResponse.class));
        verify(artistsResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: update artist data with same name and send request to update photo")
    void update_Success_IfArtistNameUnchanged() {

        // Stubs
        when(artistRepository.findById(artistId))
                .thenReturn(Optional.of(artistEntity));
        when(artistRepository.findByName("Leonardo da Vinci"))
                .thenReturn(Optional.of(artistEntity));
        when(artistRepository.save(any(ArtistEntity.class)))
                .thenReturn(artistEntity);

        // Data
        UpdateArtistGrpcRequest sameNameRequest = UpdateArtistGrpcRequest.newBuilder()
                .setId(artistId.toString())
                .setName("Leonardo da Vinci")
                .setBiography("Updated biography")
                .setPhoto("updated-image")
                .build();

        // Steps
        artistGrpcService.update(sameNameRequest, artistResponseObserver);

        // Assertions
        verify(artistRepository).save(any(ArtistEntity.class));
        verify(artistResponseObserver).onNext(any(ArtistGrpcResponse.class));
        verify(artistResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: artist data with new name when name is free")
    void update_Success_IfNewNameNotTaken() {

        // Stubs
        when(artistRepository.findById(artistId))
                .thenReturn(Optional.of(artistEntity));
        when(artistRepository.findByName("Michelangelo"))
                .thenReturn(Optional.empty());
        when(artistRepository.save(any(ArtistEntity.class)))
                .thenReturn(artistEntity);

        // Steps
        artistGrpcService.update(updateRequest, artistResponseObserver);

        // Assertions
        verify(artistRepository).save(any(ArtistEntity.class));
        verify(artistResponseObserver).onNext(any(ArtistGrpcResponse.class));
        verify(artistResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: update artist and send request to add photo when request contains photo and rococo-files doesn't have photo")
    void update_Success_IfArtistPhotoNotExists_AndRequestContainsPhoto() {

        // Data
        UpdateArtistGrpcRequest newPhotoRequest = UpdateArtistGrpcRequest.newBuilder()
                .setId(artistId.toString())
                .setName("Michelangelo")
                .setBiography("Updated biography")
                .setPhoto("new-image")
                .build();

        // Stubs
        when(artistRepository.findById(artistId))
                .thenReturn(Optional.of(artistEntity));
        when(artistRepository.findByName("Michelangelo"))
                .thenReturn(Optional.empty());
        when(artistRepository.save(any(ArtistEntity.class)))
                .thenReturn(artistEntity);
        when(filesClient.findImage(artistId))
                .thenReturn(Optional.empty());

        // Steps
        artistGrpcService.update(newPhotoRequest, artistResponseObserver);

        // Assertions
        verify(artistRepository).save(any(ArtistEntity.class));
        verify(filesClient).add(artistId, "new-image");
        verify(artistResponseObserver).onNext(any(ArtistGrpcResponse.class));
        verify(artistResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: update artist and send request to delete photo when request contains photo and rococo-files doesn't have photo")
    void update_Success_IfArtistPhotoExists_AndRequestNotContainsPhoto() {

        // Data
        UpdateArtistGrpcRequest newPhotoRequest = UpdateArtistGrpcRequest.newBuilder()
                .setId(artistId.toString())
                .setName("Michelangelo")
                .setBiography("Updated biography")
                .setPhoto("")
                .build();

        // Stubs
        when(artistRepository.findById(artistId))
                .thenReturn(Optional.of(artistEntity));
        when(artistRepository.findByName("Michelangelo"))
                .thenReturn(Optional.empty());
        when(artistRepository.save(any(ArtistEntity.class)))
                .thenReturn(artistEntity);
        when(filesClient.findImage(artistId))
                .thenReturn(Optional.of(image));

        // Steps
        artistGrpcService.update(newPhotoRequest, artistResponseObserver);

        // Assertions
        verify(artistRepository).save(any(ArtistEntity.class));
        verify(filesClient).delete(artistId);
        verify(artistResponseObserver).onNext(any(ArtistGrpcResponse.class));
        verify(artistResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: throws ArtistAlreadyExistsException when new name is taken")
    void update_ThrowsArtistAlreadyExistsException_IfNewNameTaken() {

        // Stubs
        ArtistEntity otherArtist = new ArtistEntity();
        otherArtist.setId(UUID.randomUUID());
        otherArtist.setName("Michelangelo");

        when(artistRepository.findById(artistId))
                .thenReturn(Optional.of(artistEntity));
        when(artistRepository.findByName("Michelangelo"))
                .thenReturn(Optional.of(otherArtist));

        // Steps & Assertions
        assertThrows(ArtistAlreadyExistsException.class, () ->
                artistGrpcService.update(updateRequest, artistResponseObserver));

        verify(artistRepository, never()).save(any());
        verify(artistResponseObserver, never()).onNext(any());
        verify(artistResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("Update: throws ArtistNotFoundException when artist not found")
    void update_ThrowsArtistNotFoundException_IfArtistNotFound() {

        // Stubs
        when(artistRepository.findById(artistId))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(ArtistNotFoundException.class, () ->
                artistGrpcService.update(updateRequest, artistResponseObserver));

        verify(artistRepository, never()).save(any());
        verify(artistResponseObserver, never()).onNext(any());
        verify(artistResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("RemoveById: deletes artist and sends delete photo request if artist found")
    void removeById_Success() {

        // Steps
        artistGrpcService.removeById(idRequest, emptyResponseObserver);

        // Assertions
        verify(artistRepository).deleteById(artistId);
        verify(filesClient).delete(artistId);
        verify(emptyResponseObserver).onNext(Empty.newBuilder().build());
        verify(emptyResponseObserver).onCompleted();

    }


}
