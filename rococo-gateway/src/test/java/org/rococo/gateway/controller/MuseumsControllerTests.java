package org.rococo.gateway.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.gateway.client.MuseumsGrpcClient;
import org.rococo.gateway.ex.CountryNotFoundException;
import org.rococo.gateway.ex.MuseumAlreadyExistsException;
import org.rococo.gateway.ex.MuseumNotFoundException;
import org.rococo.gateway.model.countries.CountryDTO;
import org.rococo.gateway.model.countries.CountryIdDTO;
import org.rococo.gateway.model.countries.LocationRequestDTO;
import org.rococo.gateway.model.countries.LocationResponseDTO;
import org.rococo.gateway.model.museums.AddMuseumRequestDTO;
import org.rococo.gateway.model.museums.MuseumDTO;
import org.rococo.gateway.model.museums.UpdateMuseumRequestDTO;
import org.rococo.gateway.service.ValidationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MuseumsController: Module tests")
class MuseumsControllerTests {

    @Mock
    private MuseumsGrpcClient museumsClient;

    @Mock
    private ValidationService validationService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private MuseumsController museumsController;

    private MuseumDTO museum;
    private MuseumDTO updatedMuseum;
    private AddMuseumRequestDTO addMuseumDTO;
    private UpdateMuseumRequestDTO updateMuseumDTO;

    @BeforeEach
    void setUp() {

        final var museumId = UUID.randomUUID();
        final var countryId = UUID.randomUUID();
        final var newCountryId = UUID.randomUUID();

        museum = MuseumDTO.builder()
                .id(museumId)
                .title("Orsay Museum")
                .description("Description of Orsay Museum")
                .location(LocationResponseDTO.builder()
                        .city("Paris")
                        .country(CountryDTO.builder()
                                .id(countryId)
                                .name("France")
                                .code("FR")
                                .build())
                        .build())
                .photo("new_image")
                .build();

        addMuseumDTO = AddMuseumRequestDTO.builder()
                .title("Orsay Museum")
                .description("Description of Orsay Museum")
                .location(LocationRequestDTO.builder()
                        .city("Paris")
                        .country(new CountryIdDTO(countryId))
                        .build())
                .photo("new_image")
                .build();

        updateMuseumDTO = UpdateMuseumRequestDTO.builder()
                .id(museumId)
                .title("National Gallery of Art")
                .description("Description of National Gallery of Art")
                .location(LocationRequestDTO.builder()
                        .city("New York City")
                        .country(new CountryIdDTO(newCountryId))
                        .build())
                .photo("updated_image")
                .build();

        updatedMuseum = MuseumDTO.builder()
                .id(museumId)
                .title("National Gallery of Art")
                .description("Description of National Gallery of Art")
                .location(LocationResponseDTO.builder()
                        .city("New York City")
                        .country(CountryDTO.builder()
                                .id(newCountryId)
                                .name("United States")
                                .code("US")
                                .build())
                        .build())
                .photo("updated_image")
                .build();

    }

    @Test
    @DisplayName("Add: added new museum")
    void add_Success() {

        // Stubs
        when(museumsClient.add(addMuseumDTO))
                .thenReturn(museum);
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Steps
        final var result = museumsController.add(addMuseumDTO, bindingResult);

        // Assertions
        assertEquals(museum, result);
        verify(museumsClient).add(addMuseumDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("Add: throws MuseumAlreadyExistException when museum with title exists")
    void add_ThrowsMuseumAlreadyExists_IfMuseumTitleIsAlreadyTaken() {

        // Stubs
        when(museumsClient.add(addMuseumDTO))
                .thenThrow(new MuseumAlreadyExistsException(addMuseumDTO.title()));
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Steps & Assertions
        assertThrows(MuseumAlreadyExistsException.class, () -> museumsController.add(addMuseumDTO, bindingResult));
        verify(museumsClient).add(addMuseumDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("Add: throws CountryNotFoundException when country is not found")
    void add_ThrowsCountryNotFoundException_IfCountryNotFound() {

        // Stubs
        when(museumsClient.add(addMuseumDTO))
                .thenThrow(new CountryNotFoundException(addMuseumDTO.location().country().id()));
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Steps & Assertions
        assertThrows(CountryNotFoundException.class, () -> museumsController.add(addMuseumDTO, bindingResult));
        verify(museumsClient).add(addMuseumDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("FindAll: returns page of museums when valid parameters")
    void findAll_Success() {

        // Data
        final var name = "Test";
        final var pageable = PageRequest.of(0, 9, Sort.by("title").ascending());
        final var requestParams = Map.of("title", name);
        final var expectedPage = new PageImpl<>(List.of(museum), pageable, 1);

        // Stubs
        when(museumsClient.findAll(name, false, pageable))
                .thenReturn(expectedPage);
        doNothing().when(validationService)
                .validateObject(any(), eq("MuseumsFindAllParamsValidationObject"));

        // Steps
        final var result = museumsController.findAll(name, pageable, requestParams);

        // Assertions
        assertEquals(expectedPage, result);
        assertEquals(1, result.getContent().size());
        verify(museumsClient).findAll(name, false, pageable);
        verify(validationService).validateObject(any(), eq("MuseumsFindAllParamsValidationObject"));

    }

    @Test
    @DisplayName("FindAll: returns empty page when no museums found")
    void findAll_ReturnsEmptyPage_WhenNoMuseumsFound() {

        // Data
        final var name = "Test";
        final var pageable = PageRequest.of(0, 9, Sort.by("title").ascending());
        final var requestParams = Map.of("title", name);
        final Page<MuseumDTO> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        // Stubs
        when(museumsClient.findAll(name, false, pageable))
                .thenReturn(emptyPage);
        doNothing().when(validationService)
                .validateObject(any(), eq("MuseumsFindAllParamsValidationObject"));

        // Steps
        final var result = museumsController.findAll(name, pageable, requestParams);

        // Assertions
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(museumsClient).findAll(name, false, pageable);
        verify(validationService).validateObject(any(), eq("MuseumsFindAllParamsValidationObject"));

    }

    @Test
    @DisplayName("Update: modifies museum successfully")
    void update_Success() {

        // Stubs
        when(museumsClient.update(updateMuseumDTO))
                .thenReturn(updatedMuseum);
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Steps
        final var result = museumsController.update(updateMuseumDTO, bindingResult);

        // Assertions
        assertEquals(updatedMuseum, result);
        verify(museumsClient).update(updateMuseumDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("Update: throws MuseumNotFoundException when museum is not found")
    void update_ThrowsMuseumNotFoundException_IfMuseumNotExists() {
        // Data
        final var requestDTO = UpdateMuseumRequestDTO.builder()
                .id(UUID.randomUUID())
                .title("Test Museum")
                .description("Test Description")
                .photo("test-photo.jpg")
                .location(LocationRequestDTO.builder()
                        .city("Test City")
                        .country(new CountryIdDTO(UUID.randomUUID()))
                        .build())
                .build();

        // Stubs
        when(museumsClient.update(requestDTO))
                .thenThrow(new MuseumNotFoundException(requestDTO.id()));
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Steps & Assertions
        assertThrows(MuseumNotFoundException.class, () -> museumsController.update(requestDTO, bindingResult));
        verify(museumsClient).update(requestDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("Update: throws MuseumAlreadyExistException when museum with title exists")
    void update_ThrowsMuseumAlreadyExistsException_IfMuseumTitleIsAlreadyTaken() {

        // Stubs
        when(museumsClient.update(updateMuseumDTO))
                .thenThrow(new MuseumAlreadyExistsException(updateMuseumDTO.title()));
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Steps & Assertions
        assertThrows(MuseumAlreadyExistsException.class, () -> museumsController.update(updateMuseumDTO, bindingResult));
        verify(museumsClient).update(updateMuseumDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

    @Test
    @DisplayName("Update: throws CountryNotFoundException when country is not found")
    void update_CountryNotFound_ThrowsException() {

        // Stubs
        when(museumsClient.update(updateMuseumDTO))
                .thenThrow(new CountryNotFoundException(updateMuseumDTO.location().country().id()));
        doNothing().when(validationService)
                .throwBadRequestExceptionIfErrorsExist(bindingResult);

        // Steps & Assertions
        assertThrows(CountryNotFoundException.class, () -> museumsController.update(updateMuseumDTO, bindingResult));
        verify(museumsClient).update(updateMuseumDTO);
        verify(validationService).throwBadRequestExceptionIfErrorsExist(bindingResult);

    }

}
