package org.rococo.gateway.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.gateway.client.ArtistsGrpcClient;
import org.rococo.gateway.ex.ArtistAlreadyExistsException;
import org.rococo.gateway.ex.ArtistNotFoundException;
import org.rococo.gateway.ex.BadRequestException;
import org.rococo.gateway.model.artists.AddArtistRequestDTO;
import org.rococo.gateway.model.artists.ArtistDTO;
import org.rococo.gateway.model.artists.UpdateArtistRequestDTO;
import org.rococo.gateway.service.ValidationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArtistsController: Module tests")
class ArtistsControllerTests {

    @Mock
    private ArtistsGrpcClient artistsClient;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private ArtistsController artistsController;

    private final BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "requestDTO");

    private ArtistDTO artist;
    private ArtistDTO updatedArtist;
    private AddArtistRequestDTO addArtistDTO;
    private UpdateArtistRequestDTO updateArtistDTO;
    private UUID artistId;

    @BeforeEach
    void setUp() {

        artistId = UUID.randomUUID();
        artist = ArtistDTO.builder()
                .id(artistId)
                .name("Claude Monet")
                .biography("Biography of Claude Monet")
                .photo("new_image")
                .build();

        addArtistDTO = AddArtistRequestDTO.builder()
                .name("John Doe")
                .biography("Biography of John Doe")
                .photo("new_image")
                .build();

        updatedArtist = ArtistDTO.builder()
                .id(artistId)
                .name("Vincent Van Gogh")
                .biography("Biography of Vincent Van Gogh")
                .photo("updated_image")
                .build();

        updateArtistDTO = UpdateArtistRequestDTO.builder()
                .id(artistId)
                .name("Vincent Van Gogh")
                .biography("Biography of Vincent Van Gogh")
                .photo("updated_image")
                .build();

    }

    @Test
    @DisplayName("Add: creates new artist")
    void add_Success() {

        // Stubs
        when(artistsClient.add(addArtistDTO))
                .thenReturn(artist);
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Steps
        ArtistDTO result = artistsController.add(addArtistDTO, bindingResult);

        // Assertions
        assertEquals(artist, result);
        verify(artistsClient).add(addArtistDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("Add: throws ArtistAlreadyExists when artist name is already taken")
    void add_ThrowArtistAlreadyExistsException_IfArtistNameIsAlreadyTaken() {

        // Stubs
        when(artistsClient.add(addArtistDTO))
                .thenThrow(ArtistAlreadyExistsException.class);

        // Steps & Assertions
        assertThrows(ArtistAlreadyExistsException.class, () -> artistsController.add(addArtistDTO, bindingResult));

        verify(artistsClient).add(addArtistDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("Add: throws BadRequestException when validation fails")
    void add_ValidationFailure() {

        // Data
        bindingResult.addError(new FieldError("requestDTO", "name", "must not be blank"));

        // Stubs
        doThrow(new BadRequestException(List.of())).when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Assertions
        assertThrows(BadRequestException.class, () -> artistsController.add(addArtistDTO, bindingResult));
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);
        verifyNoInteractions(artistsClient);

    }

    @Test
    @DisplayName("FindAll returns paginated artists")
    void findAll_Success() {

        // Data
        final var query = "john";
        final var pageable = PageRequest.of(0, 20, Sort.by("name").ascending());
        final var requestParams = Map.of("name", query);
        final var pageResult = new PageImpl<>(List.of(artist), pageable, 1);

        // Stubs
        when(artistsClient.findAll(query, false, pageable))
                .thenReturn(pageResult);
        doNothing().when(validationService)
                .validateObject(any(), eq("ArtistsFindAllParamsValidationObject"));

        // Steps
        Page<ArtistDTO> result = artistsController.findAll(query, pageable, requestParams);

        // Assertions
        assertEquals(pageResult, result);
        verify(artistsClient).findAll(query, false, pageable);
        verify(validationService).validateObject(any(), eq("ArtistsFindAllParamsValidationObject"));

    }

    @Test
    @DisplayName("Update: updates artist successfully")
    void update_Success() {

        // Stubs
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);
        when(artistsClient.update(updateArtistDTO))
                .thenReturn(updatedArtist);

        // Steps
        ArtistDTO result = artistsController.update(updateArtistDTO, bindingResult);

        // Assertions
        assertEquals(updatedArtist, result);
        verify(artistsClient).update(updateArtistDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("Update: throws ArtistNotFoundException when artist not found")
    void update_ThrowArtistNotFoundException_IfArtistNotFound() {

        // Stubs
        when(artistsClient.update(updateArtistDTO))
                .thenThrow(new ArtistNotFoundException(artistId));

        // Assertions
        assertThrows(ArtistNotFoundException.class, () -> artistsController.update(updateArtistDTO, bindingResult));
        verify(artistsClient).update(updateArtistDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("Update: throws ArtistAlreadyExists when artist name is already taken")
    void update_ThrowArtistAlreadyExistsException_IfArtistNameIsAlreadyTaken() {

        // Stubs
        when(artistsClient.update(updateArtistDTO))
                .thenThrow(new ArtistAlreadyExistsException(updateArtistDTO.name()));

        // Assertions
        assertThrows(ArtistAlreadyExistsException.class, () -> artistsController.update(updateArtistDTO, bindingResult));
        verify(artistsClient).update(updateArtistDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("Update: throws BadRequestException when validation fails")
    void update_ValidationFailure() {

        // Data
        bindingResult.addError(new FieldError("requestDTO", "id", "must not be null"));

        // Stubs
        doThrow(new BadRequestException(List.of())).when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Assertions
        assertThrows(BadRequestException.class, () -> artistsController.update(updateArtistDTO, bindingResult));
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);
        verifyNoInteractions(artistsClient);

    }

}
