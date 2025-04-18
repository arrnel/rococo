package org.rococo.paintings.tests.service;

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
import org.rococo.grpc.artists.ArtistGrpcResponse;
import org.rococo.grpc.artists.ArtistShortGrpcResponse;
import org.rococo.grpc.common.page.DirectionGrpc;
import org.rococo.grpc.common.page.PageableGrpc;
import org.rococo.grpc.common.page.SortGrpc;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.countries.CountryGrpcResponse;
import org.rococo.grpc.files.ImageGrpcResponse;
import org.rococo.grpc.museums.MuseumGrpcResponse;
import org.rococo.grpc.museums.MuseumShortGrpcResponse;
import org.rococo.grpc.paintings.*;
import org.rococo.paintings.client.ArtistsGrpcClient;
import org.rococo.paintings.client.FilesGrpcClient;
import org.rococo.paintings.client.MuseumsGrpcClient;
import org.rococo.paintings.data.PaintingEntity;
import org.rococo.paintings.data.PaintingRepository;
import org.rococo.paintings.ex.ArtistNotFoundException;
import org.rococo.paintings.ex.MuseumNotFoundException;
import org.rococo.paintings.ex.PaintingAlreadyExistsException;
import org.rococo.paintings.ex.PaintingNotFoundException;
import org.rococo.paintings.service.PaintingGrpcService;
import org.rococo.paintings.specs.PaintingSpecs;
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
@DisplayName("PaintingGrpcService: Module tests")
class PaintingGrpcServiceTests {

    @Mock
    private ArtistsGrpcClient artistsClient;

    @Mock
    private MuseumsGrpcClient museumsClient;

    @Mock
    private FilesGrpcClient filesClient;

    @Mock
    private PaintingRepository paintingRepository;

    @Mock
    private PaintingSpecs paintingSpecs;

    @Mock
    private StreamObserver<PaintingGrpcResponse> paintingResponseObserver;

    @Mock
    private StreamObserver<PaintingsGrpcResponse> paintingsResponseObserver;

    @Mock
    private StreamObserver<Empty> emptyResponseObserver;

    @InjectMocks
    private PaintingGrpcService paintingGrpcService;

    private PaintingEntity paintingEntity;
    private PaintingGrpcResponse paintingResponse;
    private ArtistGrpcResponse artistResponse;
    private MuseumGrpcResponse museumResponse;
    private ImageGrpcResponse imageResponse;
    private UUID paintingId;
    private UUID artistId;
    private UUID museumId;

    private IdType idRequest;

    @BeforeEach
    void setUp() {

        paintingId = UUID.randomUUID();
        artistId = UUID.randomUUID();
        museumId = UUID.randomUUID();
        idRequest = IdType.newBuilder()
                .setId(paintingId.toString())
                .build();

        paintingEntity = PaintingEntity.builder()
                .id(paintingId)
                .title("Still Life with Pheasant")
                .description("This still life, painted around 1861, is one of the oldest extant works by Claude Monet")
                .artistId(artistId)
                .museumId(museumId)
                .createdDate(LocalDateTime.now())
                .build();

        artistResponse = ArtistGrpcResponse.newBuilder()
                .setId(artistId.toString())
                .setName("Claude Monet")
                .setBiography("French painter and founder of Impressionism painting")
                .setPhoto("artist-image-1")
                .build();

        museumResponse = MuseumGrpcResponse.newBuilder()
                .setId(museumId.toString())
                .setTitle("Orsay Museum")
                .setDescription("Famous museum")
                .setCity("Paris")
                .setCountry(CountryGrpcResponse.newBuilder()
                        .setId(UUID.randomUUID().toString())
                        .setName("France")
                        .setCode("FR")
                        .build())
                .setPhoto("museum-image-1")
                .build();

        imageResponse = ImageGrpcResponse.newBuilder()
                .setEntityId(paintingId.toString())
                .setContent(ByteString.copyFromUtf8("painting-image-1"))
                .build();

        paintingResponse = PaintingGrpcResponse.newBuilder()
                .setId(paintingId.toString())
                .setTitle(paintingEntity.getTitle())
                .setDescription(paintingEntity.getDescription())
                .setArtist(ArtistShortGrpcResponse.newBuilder()
                        .setId(artistId.toString())
                        .setName(artistResponse.getName())
                        .setBiography(artistResponse.getBiography())
                        .build())
                .setMuseum(MuseumShortGrpcResponse.newBuilder()
                        .setId(museumId.toString())
                        .setTitle(museumResponse.getTitle())
                        .setDescription(museumResponse.getDescription())
                        .setCity(museumResponse.getCity())
                        .setCountry(museumResponse.getCountry())
                        .build())
                .setPhoto(imageResponse.getContent().toStringUtf8())
                .build();

    }


    @Test
    @DisplayName("Add: add new painting")
    void add_Success() {

        // Data
        final var request = AddPaintingGrpcRequest.newBuilder()
                .setTitle(paintingEntity.getTitle())
                .setDescription(paintingEntity.getDescription())
                .setArtistId(artistId.toString())
                .setMuseumId(museumId.toString())
                .setPhoto("painting-image-1")
                .build();

        // Stubs
        when(paintingRepository.findByTitle(paintingEntity.getTitle()))
                .thenReturn(Optional.empty());
        when(artistsClient.findById(artistId))
                .thenReturn(Optional.of(artistResponse));
        when(museumsClient.findById(museumId))
                .thenReturn(Optional.of(museumResponse));
        when(paintingRepository.save(any(PaintingEntity.class)))
                .thenReturn(paintingEntity);

        // Steps
        paintingGrpcService.add(request, paintingResponseObserver);

        // Assertions
        verify(paintingRepository).save(any(PaintingEntity.class));
        verify(filesClient).add(paintingId, "painting-image-1");
        verify(paintingResponseObserver).onNext(paintingResponse);
        verify(paintingResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Add: throws PaintingAlreadyExistsException when painting exists")
    void add_ThrowsPaintingAlreadyExistsException_IfExistPaintingWithSameTitle() {

        // Data
        final var request = AddPaintingGrpcRequest.newBuilder()
                .setTitle(paintingEntity.getTitle())
                .setDescription(paintingEntity.getDescription())
                .setArtistId(artistId.toString())
                .setMuseumId(museumId.toString())
                .setPhoto("painting-image-1")
                .build();

        // Stubs
        when(paintingRepository.findByTitle(paintingEntity.getTitle()))
                .thenReturn(Optional.of(paintingEntity));

        // Steps & Assertions
        assertThrows(PaintingAlreadyExistsException.class, () ->
                paintingGrpcService.add(request, paintingResponseObserver));

        verify(paintingRepository, never()).save(any());
        verify(filesClient, never()).add(any(), any());
        verify(paintingResponseObserver, never()).onNext(any());

    }

    @Test
    @DisplayName("Add: throws ArtistNotFoundException when artist not found")
    void add_ThrowsArtistNotFoundException_IfArtistNotFound() {

        // Data
        final var request = AddPaintingGrpcRequest.newBuilder()
                .setTitle(paintingEntity.getTitle())
                .setDescription(paintingEntity.getDescription())
                .setArtistId(artistId.toString())
                .setMuseumId(museumId.toString())
                .setPhoto("painting-image-1")
                .build();

        // Stubs
        when(paintingRepository.findByTitle(paintingEntity.getTitle()))
                .thenReturn(Optional.empty());
        when(artistsClient.findById(paintingEntity.getArtistId()))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(ArtistNotFoundException.class, () ->
                paintingGrpcService.add(request, paintingResponseObserver));

        verify(paintingRepository, never()).save(any());
        verify(filesClient, never()).add(any(), any());
        verify(paintingResponseObserver, never()).onNext(any());

    }

    @Test
    @DisplayName("Add: throws MuseumNotFoundException when museum not found")
    void add_ThrowsMuseumNotFoundException_IfMuseumNotFound() {

        // Data
        final var request = AddPaintingGrpcRequest.newBuilder()
                .setTitle(paintingEntity.getTitle())
                .setDescription(paintingEntity.getDescription())
                .setArtistId(artistId.toString())
                .setMuseumId(museumId.toString())
                .setPhoto("painting-image-1")
                .build();

        // Stubs
        when(paintingRepository.findByTitle(paintingEntity.getTitle()))
                .thenReturn(Optional.empty());
        when(artistsClient.findById(paintingEntity.getArtistId()))
                .thenReturn(Optional.of(artistResponse));
        when(museumsClient.findById(paintingEntity.getMuseumId()))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(MuseumNotFoundException.class, () ->
                paintingGrpcService.add(request, paintingResponseObserver));

        verify(paintingRepository, never()).save(any());
        verify(filesClient, never()).add(any(), any());
        verify(paintingResponseObserver, never()).onNext(any());

    }

    @Test
    @DisplayName("FindById: returns painting")
    void findById_Success() {

        // Data
        final var request = IdType.newBuilder()
                .setId(paintingId.toString())
                .build();

        // Stubs
        when(paintingRepository.findById(paintingId))
                .thenReturn(Optional.of(paintingEntity));
        when(artistsClient.findById(artistId))
                .thenReturn(Optional.of(artistResponse));
        when(museumsClient.findById(museumId))
                .thenReturn(Optional.of(museumResponse));
        when(filesClient.findImage(paintingId))
                .thenReturn(Optional.of(imageResponse));

        // Steps
        paintingGrpcService.findById(request, paintingResponseObserver);

        // Assertions
        verify(paintingResponseObserver).onNext(paintingResponse);
        verify(paintingResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("FindById: throws PaintingNotFoundException when painting not found")
    void findById_ThrowsPaintingNotFoundException_IfPaintingNotFound() {

        // Data
        final var request = IdType.newBuilder()
                .setId(paintingId.toString())
                .build();

        // Stub
        when(paintingRepository.findById(paintingId))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(PaintingNotFoundException.class, () ->
                paintingGrpcService.findById(request, paintingResponseObserver));

        verify(paintingResponseObserver, never()).onNext(any());

    }

    @Test
    @DisplayName("FindByTitle: returns painting")
    void findByTitle_Success() {

        // Data
        final var request = NameType.newBuilder()
                .setName(paintingEntity.getTitle())
                .build();

        // Stubs
        when(paintingRepository.findByTitle(paintingEntity.getTitle()))
                .thenReturn(Optional.of(paintingEntity));
        when(artistsClient.findById(artistId))
                .thenReturn(Optional.of(artistResponse));
        when(museumsClient.findById(museumId))
                .thenReturn(Optional.of(museumResponse));
        when(filesClient.findImage(paintingId))
                .thenReturn(Optional.of(imageResponse));

        // Steps
        paintingGrpcService.findByTitle(request, paintingResponseObserver);

        // Assertions
        verify(paintingResponseObserver).onNext(paintingResponse);
        verify(paintingResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("FindByTitle: throws PaintingNotFoundException when painting not found")
    void findByTitle_NotFound_ThrowsException() {

        // Data
        final var request = NameType.newBuilder()
                .setName(paintingEntity.getTitle())
                .build();

        // Stub
        when(paintingRepository.findByTitle(paintingEntity.getTitle()))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(PaintingNotFoundException.class, () ->
                paintingGrpcService.findByTitle(request, paintingResponseObserver));

        verify(paintingResponseObserver, never()).onNext(any());

    }

    @Test
    @DisplayName("FindAll returns paintings")
    void findAll_Success() {

        // Data
        final var isOriginalPhoto = false;
        final var paintings = List.of(paintingEntity);
        final var page = new PageImpl<>(paintings, PageRequest.of(0, 10), paintings.size());
        final var request = PaintingsFilterGrpcRequest.newBuilder()
                .setOriginalPhoto(isOriginalPhoto)
                .setPageable(PageableGrpc.newBuilder()
                        .setPage(0)
                        .setSize(10)
                        .setSort(SortGrpc.newBuilder()
                                .setOrder("title")
                                .setDirection(DirectionGrpc.ASC)
                                .build())
                        .build())
                .build();

        // Stubs
        when(paintingRepository.findAll(ArgumentMatchers.<Specification<PaintingEntity>>any(), any(Pageable.class)))
                .thenReturn(page);
        when(artistsClient.findAllByIds(List.of(artistId)))
                .thenReturn(List.of(artistResponse));
        when(museumsClient.findAllByIds(List.of(museumId)))
                .thenReturn(List.of(museumResponse));
        when(filesClient.findAllByIds(List.of(paintingId), isOriginalPhoto))
                .thenReturn(List.of(imageResponse));

        paintingGrpcService.findAll(request, paintingsResponseObserver);

        verify(paintingsResponseObserver).onNext(any(PaintingsGrpcResponse.class));
        verify(paintingsResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: update painting data and send request to update photo")
    void update_Success_IfPaintingPhotoExists_AndRequestContainsPhoto() {

        // Data
        final var newArtist = ArtistGrpcResponse.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setName("Pablo Picasso")
                .setBiography("Biography of Pablo Picasso")
                .setPhoto("Photo of Pablo Picasso")
                .build();

        final var newMuseum = MuseumGrpcResponse.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setTitle("Tate Gallery")
                .setDescription("Biography of Tate Gallery")
                .setCity("London")
                .setCountry(CountryGrpcResponse.newBuilder()
                        .setId(UUID.randomUUID().toString())
                        .setName("United Kingdom")
                        .setCode("UK")
                        .build())
                .setPhoto("Photo of Tate Gallery")
                .build();

        final var updatePaintingRequest = UpdatePaintingGrpcRequest.newBuilder()
                .setId(paintingId.toString())
                .setTitle("Girl in a Chemise")
                .setDescription("Description of painting \"Girl in a Chemise\"")
                .setArtistId(newArtist.getId())
                .setMuseumId(newMuseum.getId())
                .setPhoto("updated-image")
                .build();

        final var updatedPaintingEntity = PaintingEntity.builder()
                .id(paintingId)
                .title(updatePaintingRequest.getTitle())
                .description(updatePaintingRequest.getDescription())
                .artistId(UUID.fromString(newArtist.getId()))
                .museumId(UUID.fromString(newMuseum.getId()))
                .build();

        var updatedPaintingResponse = PaintingGrpcResponse.newBuilder()
                .setId(paintingId.toString())
                .setTitle(updatePaintingRequest.getTitle())
                .setDescription(updatePaintingRequest.getDescription())
                .setArtist(ArtistShortGrpcResponse.newBuilder()
                        .setId(newArtist.getId())
                        .setName(newArtist.getName())
                        .setBiography(newArtist.getBiography())
                        .build())
                .setMuseum(MuseumShortGrpcResponse.newBuilder()
                        .setId(newMuseum.getId())
                        .setTitle(newMuseum.getTitle())
                        .setDescription(newMuseum.getDescription())
                        .setCity(newMuseum.getCity())
                        .setCountry(newMuseum.getCountry())
                        .build())
                .setPhoto(updatePaintingRequest.getPhoto())
                .build();

        // Stubs
        when(paintingRepository.findById(paintingId))
                .thenReturn(Optional.of(paintingEntity));
        when(paintingRepository.findByTitle(updatePaintingRequest.getTitle()))
                .thenReturn(Optional.empty());
        when(artistsClient.findById(UUID.fromString(newArtist.getId())))
                .thenReturn(Optional.of(newArtist));
        when(museumsClient.findById(UUID.fromString(newMuseum.getId())))
                .thenReturn(Optional.of(newMuseum));
        when(filesClient.findImage(paintingId))
                .thenReturn(Optional.of(imageResponse));
        when(paintingRepository.save(any(PaintingEntity.class)))
                .thenReturn(updatedPaintingEntity);

        // Steps
        paintingGrpcService.update(updatePaintingRequest, paintingResponseObserver);

        // Assertions
        verify(filesClient).update(paintingId, "updated-image");
        verify(paintingRepository).save(any(PaintingEntity.class));
        verify(paintingResponseObserver).onNext(updatedPaintingResponse);
        verify(paintingResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: update painting and send request to delete photo when request not contains photo and rococo-files have photo")
    void update_Success_IfPaintingPhotoExists_AndRequestNotContainsPhoto() {

        // Data
        final var newArtist = ArtistGrpcResponse.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setName("Pablo Picasso")
                .setBiography("Biography of Pablo Picasso")
                .setPhoto("Photo of Pablo Picasso")
                .build();

        final var newMuseum = MuseumGrpcResponse.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setTitle("Tate Gallery")
                .setDescription("Biography of Tate Gallery")
                .setCity("London")
                .setCountry(CountryGrpcResponse.newBuilder()
                        .setId(UUID.randomUUID().toString())
                        .setName("United Kingdom")
                        .setCode("UK")
                        .build())
                .setPhoto("Photo of Tate Gallery")
                .build();

        final var updatePaintingRequest = UpdatePaintingGrpcRequest.newBuilder()
                .setId(paintingId.toString())
                .setTitle("Girl in a Chemise")
                .setDescription("Description of painting \"Girl in a Chemise\"")
                .setArtistId(newArtist.getId())
                .setMuseumId(newMuseum.getId())
                .setPhoto("")
                .build();

        final var updatedPaintingEntity = PaintingEntity.builder()
                .id(paintingId)
                .title(updatePaintingRequest.getTitle())
                .description(updatePaintingRequest.getDescription())
                .artistId(UUID.fromString(newArtist.getId()))
                .museumId(UUID.fromString(newMuseum.getId()))
                .build();

        var updatedPaintingResponse = PaintingGrpcResponse.newBuilder()
                .setId(paintingId.toString())
                .setTitle(updatePaintingRequest.getTitle())
                .setDescription(updatePaintingRequest.getDescription())
                .setArtist(ArtistShortGrpcResponse.newBuilder()
                        .setId(newArtist.getId())
                        .setName(newArtist.getName())
                        .setBiography(newArtist.getBiography())
                        .build())
                .setMuseum(MuseumShortGrpcResponse.newBuilder()
                        .setId(newMuseum.getId())
                        .setTitle(newMuseum.getTitle())
                        .setDescription(newMuseum.getDescription())
                        .setCity(newMuseum.getCity())
                        .setCountry(newMuseum.getCountry())
                        .build())
                .setPhoto(updatePaintingRequest.getPhoto())
                .build();

        // Stubs
        when(paintingRepository.findById(paintingId))
                .thenReturn(Optional.of(paintingEntity));
        when(paintingRepository.findByTitle(updatePaintingRequest.getTitle()))
                .thenReturn(Optional.empty());
        when(artistsClient.findById(UUID.fromString(newArtist.getId())))
                .thenReturn(Optional.of(newArtist));
        when(museumsClient.findById(UUID.fromString(newMuseum.getId())))
                .thenReturn(Optional.of(newMuseum));
        when(filesClient.findImage(paintingId))
                .thenReturn(Optional.of(imageResponse));
        when(paintingRepository.save(any(PaintingEntity.class)))
                .thenReturn(updatedPaintingEntity);

        // Steps
        paintingGrpcService.update(updatePaintingRequest, paintingResponseObserver);

        // Assertions
        verify(filesClient).delete(paintingId);
        verify(paintingRepository).save(any(PaintingEntity.class));
        verify(paintingResponseObserver).onNext(updatedPaintingResponse);
        verify(paintingResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: painting and send request to add new photo when request contains photo and rococo-files doesn't have photo")
    void update_Success_IfPaintingPhotoNotExists_AndRequestContainsPhoto() {

        // Data
        final var newArtist = ArtistGrpcResponse.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setName("Pablo Picasso")
                .setBiography("Biography of Pablo Picasso")
                .setPhoto("Photo of Pablo Picasso")
                .build();

        final var newMuseum = MuseumGrpcResponse.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setTitle("Tate Gallery")
                .setDescription("Biography of Tate Gallery")
                .setCity("London")
                .setCountry(CountryGrpcResponse.newBuilder()
                        .setId(UUID.randomUUID().toString())
                        .setName("United Kingdom")
                        .setCode("UK")
                        .build())
                .setPhoto("Photo of Tate Gallery")
                .build();

        final var updatePaintingRequest = UpdatePaintingGrpcRequest.newBuilder()
                .setId(paintingId.toString())
                .setTitle("Girl in a Chemise")
                .setDescription("Description of painting \"Girl in a Chemise\"")
                .setArtistId(newArtist.getId())
                .setMuseumId(newMuseum.getId())
                .setPhoto("user-photo")
                .build();

        final var updatedPaintingEntity = PaintingEntity.builder()
                .id(paintingId)
                .title(updatePaintingRequest.getTitle())
                .description(updatePaintingRequest.getDescription())
                .artistId(UUID.fromString(newArtist.getId()))
                .museumId(UUID.fromString(newMuseum.getId()))
                .build();

        var updatedPaintingResponse = PaintingGrpcResponse.newBuilder()
                .setId(paintingId.toString())
                .setTitle(updatePaintingRequest.getTitle())
                .setDescription(updatePaintingRequest.getDescription())
                .setArtist(ArtistShortGrpcResponse.newBuilder()
                        .setId(newArtist.getId())
                        .setName(newArtist.getName())
                        .setBiography(newArtist.getBiography())
                        .build())
                .setMuseum(MuseumShortGrpcResponse.newBuilder()
                        .setId(newMuseum.getId())
                        .setTitle(newMuseum.getTitle())
                        .setDescription(newMuseum.getDescription())
                        .setCity(newMuseum.getCity())
                        .setCountry(newMuseum.getCountry())
                        .build())
                .setPhoto(updatePaintingRequest.getPhoto())
                .build();

        // Stubs
        when(paintingRepository.findById(paintingId))
                .thenReturn(Optional.of(paintingEntity));
        when(paintingRepository.findByTitle(updatePaintingRequest.getTitle()))
                .thenReturn(Optional.empty());
        when(artistsClient.findById(UUID.fromString(newArtist.getId())))
                .thenReturn(Optional.of(newArtist));
        when(museumsClient.findById(UUID.fromString(newMuseum.getId())))
                .thenReturn(Optional.of(newMuseum));
        when(filesClient.findImage(paintingId))
                .thenReturn(Optional.empty());
        when(paintingRepository.save(any(PaintingEntity.class)))
                .thenReturn(updatedPaintingEntity);

        // Steps
        paintingGrpcService.update(updatePaintingRequest, paintingResponseObserver);

        // Assertions
        verify(filesClient).add(paintingId, updatePaintingRequest.getPhoto());
        verify(paintingRepository).save(any(PaintingEntity.class));
        verify(paintingResponseObserver).onNext(updatedPaintingResponse);
        verify(paintingResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: throws PaintingNotFoundException when painting not found")
    void update_ThrowsPaintingNotFoundException_IfPaintingNotFound() {

        // Data
        UpdatePaintingGrpcRequest request = UpdatePaintingGrpcRequest.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setTitle("Girl in a Chemise")
                .setDescription("Description of painting \"Girl in a Chemise\"")
                .setArtistId(UUID.randomUUID().toString())
                .setMuseumId(UUID.randomUUID().toString())
                .setPhoto("updated-image")
                .build();

        // Stubs
        when(paintingRepository.findById(UUID.fromString(request.getId())))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(PaintingNotFoundException.class, () ->
                paintingGrpcService.update(request, paintingResponseObserver));

        verify(paintingRepository, never()).save(any());
        verify(paintingResponseObserver, never()).onNext(any());

    }

    @Test
    @DisplayName("Update: painting throws PaintingAlreadyExistsException when painting title is already taken")
    void update_ThrowsPaintingAlreadyExistsException_IfExistPaintingWithSameTitle() {

        // Data
        final var request = UpdatePaintingGrpcRequest.newBuilder()
                .setId(paintingEntity.getId().toString())
                .setTitle(paintingEntity.getTitle())
                .build();

        var anotherPaintingWithSameTitle = PaintingEntity.builder()
                .id(UUID.randomUUID())
                .title(paintingEntity.getTitle())
                .build();

        // Stubs
        when(paintingRepository.findById(paintingId))
                .thenReturn(Optional.of(paintingEntity));
        when(paintingRepository.findByTitle(paintingEntity.getTitle()))
                .thenReturn(Optional.of(anotherPaintingWithSameTitle));

        // Steps & Assertions
        assertThrows(PaintingAlreadyExistsException.class, () ->
                paintingGrpcService.update(request, paintingResponseObserver));

        verify(paintingRepository, never()).save(any());
        verify(filesClient, never()).add(any(), any());
        verify(paintingResponseObserver, never()).onNext(any());

    }

    @Test
    @DisplayName("Update: painting throws ArtistNotFoundException when artist not found")
    void update_ThrowsArtistNotFoundException_IfArtistNotFound() {

        // Data
        final var request = UpdatePaintingGrpcRequest.newBuilder()
                .setId(paintingEntity.getId().toString())
                .setTitle(paintingEntity.getTitle())
                .setDescription(paintingEntity.getDescription())
                .setArtistId(artistId.toString())
                .setMuseumId(museumId.toString())
                .setPhoto("painting-image-1")
                .build();

        // Stubs
        when(paintingRepository.findById(paintingId))
                .thenReturn(Optional.of(paintingEntity));
        when(paintingRepository.findByTitle(paintingEntity.getTitle()))
                .thenReturn(Optional.of(paintingEntity));
        when(artistsClient.findById(artistId))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(ArtistNotFoundException.class, () ->
                paintingGrpcService.update(request, paintingResponseObserver));

        verify(paintingRepository, never()).save(any());
        verify(filesClient, never()).add(any(), any());
        verify(paintingResponseObserver, never()).onNext(any());

    }

    @Test
    @DisplayName("Update: painting throws MuseumNotFoundException when museum not found")
    void update_ThrowsMuseumNotFoundException_IfMuseumNotFound() {

        // Data
        final var request = UpdatePaintingGrpcRequest.newBuilder()
                .setId(paintingId.toString())
                .setTitle(paintingEntity.getTitle())
                .setDescription(paintingEntity.getDescription())
                .setArtistId(artistId.toString())
                .setMuseumId(museumId.toString())
                .setPhoto("painting-image-1")
                .build();

        // Stubs
        when(paintingRepository.findById(paintingEntity.getId()))
                .thenReturn(Optional.of(paintingEntity));
        when(paintingRepository.findByTitle(paintingEntity.getTitle()))
                .thenReturn(Optional.of(paintingEntity));
        when(artistsClient.findById(artistId))
                .thenReturn(Optional.of(artistResponse));
        when(museumsClient.findById(museumId))
                .thenReturn(Optional.empty());


        // Steps & Assertions
        assertThrows(MuseumNotFoundException.class, () ->
                paintingGrpcService.update(request, paintingResponseObserver));

        verify(paintingRepository, never()).save(any());
        verify(filesClient, never()).add(any(), any());
        verify(paintingResponseObserver, never()).onNext(any());

    }

    @Test
    @DisplayName("RemoveById: Delete user")
    void removeById_Success() {


        // Steps
        paintingGrpcService.removeById(idRequest, emptyResponseObserver);

        // Assertions
        verify(paintingRepository).deleteById(paintingId);
        verify(filesClient).delete(paintingId);
        verify(emptyResponseObserver).onNext(Empty.newBuilder().build());
        verify(emptyResponseObserver).onCompleted();

    }

}
