package org.rococo.gateway.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.gateway.client.MuseumsGrpcClient;
import org.rococo.gateway.ex.MuseumNotFoundException;
import org.rococo.gateway.model.countries.CountryDTO;
import org.rococo.gateway.model.countries.LocationResponseDTO;
import org.rococo.gateway.model.museums.MuseumDTO;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MuseumController: Module tests")
class MuseumControllerTests {

    @Mock
    private MuseumsGrpcClient museumsClient;

    @InjectMocks
    private MuseumController museumController;

    @Test
    @DisplayName("FindById: returns museum when it exists")
    void findById_Success() {

        // Data
        final var id = UUID.randomUUID();
        final var expectedMuseum = MuseumDTO.builder()
                .id(id)
                .title("Test Museum")
                .description("Test Description")
                .photo("test-photo.jpg")
                .location(LocationResponseDTO.builder()
                        .city("Test City")
                        .country(CountryDTO.builder()
                                .id(UUID.randomUUID())
                                .name("Test Country")
                                .code("TC")
                                .build())
                        .build())
                .build();

        // Steps
        MuseumDTO result = museumController.findById(expectedMuseum);

        // Assertions
        assertEquals(expectedMuseum, result);

    }

    @Test
    @DisplayName("FindById: throws MuseumNotFoundException when museum does not exist")
    void findById_ThrowsMuseumNotFoundException_WhenMuseumNotFound() {

        // Data
        final var id = UUID.randomUUID();

        // Stubs
        when(museumsClient.findById(id))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(MuseumNotFoundException.class, () -> museumController.museum(id));
        verify(museumsClient).findById(id);

    }

    @Test
    @DisplayName("Delete: removes museum successfully")
    void delete_Success() {

        // Data
        final var id = UUID.randomUUID();
        final var museum = MuseumDTO.builder()
                .id(id)
                .title("Test Museum")
                .description("Test Description")
                .photo("test-photo.jpg")
                .build();

        // Stubs
        doNothing().when(museumsClient).delete(id);

        // Steps
        museumController.delete(museum);

        // Assertions
        verify(museumsClient).delete(id);

    }

}
