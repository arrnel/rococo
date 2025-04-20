package org.rococo.gateway.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.gateway.client.PaintingsGrpcClient;
import org.rococo.gateway.ex.ArtistNotFoundException;
import org.rococo.gateway.ex.MuseumNotFoundException;
import org.rococo.gateway.ex.PaintingAlreadyExistsException;
import org.rococo.gateway.ex.PaintingNotFoundException;
import org.rococo.gateway.model.artists.ArtistDTO;
import org.rococo.gateway.model.artists.ArtistIdDTO;
import org.rococo.gateway.model.countries.CountryDTO;
import org.rococo.gateway.model.countries.LocationResponseDTO;
import org.rococo.gateway.model.museums.MuseumDTO;
import org.rococo.gateway.model.museums.MuseumIdDTO;
import org.rococo.gateway.model.paintings.AddPaintingRequestDTO;
import org.rococo.gateway.model.paintings.PaintingDTO;
import org.rococo.gateway.model.paintings.UpdatePaintingRequestDTO;
import org.rococo.gateway.service.ValidationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaintingsController: Module tests")
class PaintingsControllerTests {

    @Mock
    PaintingsGrpcClient paintingsClient;

    @Mock
    BindingResult bindingResult;

    @Mock
    ValidationService validationService;

    @InjectMocks
    PaintingsController paintingsController;

    private PaintingDTO painting;
    private PaintingDTO updatedPainting;
    private AddPaintingRequestDTO addPaintingDTO;
    private UpdatePaintingRequestDTO updatePaintingDTO;
    private UUID paintingId;
    private UUID artistId;
    private UUID updatedArtistId;
    private UUID museumId;
    private UUID updatedMuseumId;

    @BeforeEach
    void setUp() {

        paintingId = UUID.randomUUID();
        artistId = UUID.randomUUID();
        updatedArtistId = UUID.randomUUID();
        museumId = UUID.randomUUID();
        updatedMuseumId = UUID.randomUUID();

        painting = PaintingDTO.builder()
                .id(paintingId)
                .title("Impression, Sunrise")
                .description("Description of Impression, Sunrise")
                .artist(ArtistDTO.builder()
                        .id(artistId)
                        .name("Claude Monet")
                        .biography("Biography of Claude Monet")
                        .build())
                .museum(MuseumDTO.builder()
                        .id(museumId)
                        .title("Orsay Painting")
                        .description("Description of Orsay Painting")
                        .location(LocationResponseDTO.builder()
                                .city("Paris")
                                .country(CountryDTO.builder()
                                        .id(UUID.randomUUID())
                                        .name("France")
                                        .code("FR")
                                        .build())
                                .build())
                        .build())
                .photo("new_image")
                .build();

        updatedPainting = PaintingDTO.builder()
                .id(paintingId)
                .title("The Starry Night")
                .description("Description of The Starry Night")
                .artist(ArtistDTO.builder()
                        .id(artistId)
                        .name("Vincent Van Gogh")
                        .biography("Biography of Vincent Van Gogh")
                        .build())
                .museum(MuseumDTO.builder()
                        .id(museumId)
                        .title("National Gallery of Art")
                        .description("Description of National Gallery of Art")
                        .location(LocationResponseDTO.builder()
                                .city("New York City")
                                .country(CountryDTO.builder()
                                        .id(UUID.randomUUID())
                                        .name("United States")
                                        .code("US")
                                        .build())
                                .build())
                        .build())
                .photo("new_image")
                .build();

        addPaintingDTO = AddPaintingRequestDTO.builder()
                .title("Impression, Sunrise")
                .description("Description of Impression, Sunrise")
                .artist(new ArtistIdDTO(artistId))
                .museum(new MuseumIdDTO(paintingId))
                .photo("new_image")
                .build();

        updatePaintingDTO = UpdatePaintingRequestDTO.builder()
                .id(paintingId)
                .title("The Starry Night")
                .description("Description of The Starry Night")
                .artist(new ArtistIdDTO(updatedArtistId))
                .museum(new MuseumIdDTO(updatedMuseumId))
                .photo("updated_image")
                .build();

    }

    @Test
    @DisplayName("Add: creates painting successfully")
    void add_Success() {

        // Stubs
        when(paintingsClient.add(addPaintingDTO))
                .thenReturn(painting);
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Steps
        final var result = paintingsController.add(addPaintingDTO, bindingResult);

        // Assertions
        assertEquals(painting, result);
        verify(paintingsClient).add(addPaintingDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("Add: throws PaintingAlreadyExistsException when painting with title exists")
    void add_ThrowsPaintingAlreadyExistsException_IfPaintingTitleIsAlreadyTaken() {

        // Stubs
        when(paintingsClient.add(addPaintingDTO))
                .thenThrow(new PaintingAlreadyExistsException(addPaintingDTO.title()));
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Steps & Assertions
        assertThrows(PaintingAlreadyExistsException.class, () -> paintingsController.add(addPaintingDTO, bindingResult));

        verify(paintingsClient).add(addPaintingDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("Add: throws ArtistNotFoundException when artist is not found")
    void add_ThrowsArtistNotFoundException_IfArtistNotFound() {

        // Stubs
        when(paintingsClient.add(addPaintingDTO))
                .thenThrow(new ArtistNotFoundException(artistId));
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Steps & Assertions
        assertThrows(ArtistNotFoundException.class, () -> paintingsController.add(addPaintingDTO, bindingResult));

        verify(paintingsClient).add(addPaintingDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("Add: throws MuseumNotFoundException when museum is not found")
    void add_ThrowsMuseumNotFoundException_IfMuseumNotFound() {

        // Stubs
        when(paintingsClient.add(addPaintingDTO))
                .thenThrow(new MuseumNotFoundException(museumId));
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Steps & Assertions
        assertThrows(MuseumNotFoundException.class, () -> paintingsController.add(addPaintingDTO, bindingResult));

        verify(paintingsClient).add(addPaintingDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("FindAll: returns paintings successfully")
    void findAll_Success() {

        // Data
        final var pageable = PageRequest.of(0, 9);
        Page<PaintingDTO> expectedPage = new PageImpl<>(Collections.singletonList(painting), pageable, 1);

        // Stubs
        when(paintingsClient.findAll(painting.getTitle(), artistId, false, pageable))
                .thenReturn(expectedPage);
        doNothing().when(validationService)
                .validateObject(any(), eq("PaintingsFindAllParamsValidationObject"));

        // Steps
        final var result = paintingsController.findAll(painting.getTitle(), artistId, pageable, Map.of("title", painting.getTitle()));

        // Assertions
        assertEquals(expectedPage, result);
        verify(paintingsClient).findAll(painting.getTitle(), artistId, false, pageable);
        verify(validationService).validateObject(any(), eq("PaintingsFindAllParamsValidationObject"));

    }

    @Test
    @DisplayName("Update: updates painting successfully")
    void update_Success() {

        // Stubs
        when(paintingsClient.update(updatePaintingDTO))
                .thenReturn(updatedPainting);
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Steps
        PaintingDTO result = paintingsController.update(updatePaintingDTO, bindingResult);

        // Assertions
        assertEquals(updatedPainting, result);
        verify(paintingsClient).update(updatePaintingDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("Update: throws PaintingNotFoundException when painting is not found")
    void update_ThrowsPaintingNotFoundException_IfPaintingNotFound() {

        // Stubs
        when(paintingsClient.update(updatePaintingDTO))
                .thenThrow(new PaintingNotFoundException(paintingId));
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Steps & Assertions
        assertThrows(PaintingNotFoundException.class, () -> paintingsController.update(updatePaintingDTO, bindingResult));

        verify(paintingsClient).update(updatePaintingDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("Update: throws PaintingAlreadyExistsException when painting with title exists")
    void Update_ThrowsPaintingAlreadyExistsException_IfPaintingTitleIsAlreadyTaken() {

        // Data
        final var id = UUID.randomUUID();
        final var requestDTO = UpdatePaintingRequestDTO.builder()
                .id(id)
                .title("Test Painting")
                .description("Test Description")
                .photo("test-photo.jpg")
                .artist(ArtistIdDTO.builder().id(UUID.randomUUID()).build())
                .museum(MuseumIdDTO.builder().id(UUID.randomUUID()).build())
                .build();


        // Stubs
        when(paintingsClient.update(requestDTO))
                .thenThrow(new PaintingAlreadyExistsException(requestDTO.title()));
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Steps & Assertions
        assertThrows(PaintingAlreadyExistsException.class, () -> paintingsController.update(requestDTO, bindingResult));

        verify(paintingsClient).update(requestDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("Update: throws ArtistNotFoundException when artist is not found")
    void update_ThrowsArtistNotFoundException_IfArtistNotFound() {

        // Stubs
        when(paintingsClient.update(updatePaintingDTO))
                .thenThrow(new ArtistNotFoundException(updatedArtistId));
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Steps & Assertions
        assertThrows(ArtistNotFoundException.class, () -> paintingsController.update(updatePaintingDTO, bindingResult));

        verify(paintingsClient).update(updatePaintingDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("Update: throws MuseumNotFoundException when museum is not found")
    void Update_ThrowsMuseumNotFoundException_IfMuseumNotFound() {

        // Stubs
        when(paintingsClient.update(updatePaintingDTO))
                .thenThrow(new MuseumNotFoundException(updatedMuseumId));
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Steps & Assertions
        assertThrows(MuseumNotFoundException.class, () -> paintingsController.update(updatePaintingDTO, bindingResult));

        verify(paintingsClient).update(updatePaintingDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

}
