package org.rococo.gateway.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.gateway.client.PaintingsGrpcClient;
import org.rococo.gateway.ex.PaintingNotFoundException;
import org.rococo.gateway.model.artists.ArtistDTO;
import org.rococo.gateway.model.countries.CountryDTO;
import org.rococo.gateway.model.countries.LocationResponseDTO;
import org.rococo.gateway.model.museums.MuseumDTO;
import org.rococo.gateway.model.paintings.PaintingDTO;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaintingController: Module tests")
class PaintingControllerTests {

    @Mock
    private PaintingsGrpcClient paintingsClient;

    @InjectMocks
    private PaintingController paintingController;

    private PaintingDTO painting;
    private UUID paintingId;

    @BeforeEach
    void setUp() {

        paintingId = UUID.randomUUID();

        painting = PaintingDTO.builder()
                .id(paintingId)
                .title("Impression, Sunrise")
                .description("Description of Impression, Sunrise")
                .artist(ArtistDTO.builder()
                        .id(UUID.randomUUID())
                        .name("Claude Monet")
                        .biography("Biography of Claude Monet")
                        .build())
                .museum(MuseumDTO.builder()
                        .id(UUID.randomUUID())
                        .title("Orsay Museum")
                        .description("Description of Orsay Museum")
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

    }

    @Test
    @DisplayName("FindById: returns painting when found")
    void findById_Success() {

        // Steps
        PaintingDTO result = paintingController.findById(painting);

        // Assertions
        assertEquals(painting, result);

    }

    @Test
    @DisplayName("FindById: throws PaintingNotFoundException when painting not found")
    void findById_PaintingNotFound_ThrowsException() {

        // Stubs
        when(paintingsClient.findById(paintingId))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(PaintingNotFoundException.class, () -> paintingController.painting(paintingId));
        verify(paintingsClient).findById(paintingId);

    }

    @Test
    @DisplayName("Delete: removes painting successfully")
    void delete_Success() {

        // Stubs
        doNothing().when(paintingsClient)
                .delete(paintingId);

        // Steps
        paintingController.delete(painting);

        // Assertions
        verify(paintingsClient).delete(paintingId);

    }

    @Test
    @DisplayName("Delete: throws PaintingNotFoundException when painting not found")
    void delete_PaintingNotFound_ThrowsException() {

        // Stubs
        when(paintingsClient.findById(paintingId))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(PaintingNotFoundException.class, () -> paintingController.painting(paintingId));

        verify(paintingsClient).findById(paintingId);
        verify(paintingsClient, never()).delete(paintingId);

    }

}
