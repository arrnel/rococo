package org.rococo.gateway.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.gateway.client.ArtistsGrpcClient;
import org.rococo.gateway.ex.ArtistNotFoundException;
import org.rococo.gateway.model.artists.ArtistDTO;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArtistController: Module tests")
class ArtistControllerTests {

    @Mock
    private ArtistsGrpcClient artistsClient;

    @InjectMocks
    private ArtistController artistController;

    private final UUID artistId = UUID.randomUUID();
    private final ArtistDTO artistDTO = ArtistDTO.builder()
            .id(artistId)
            .name("Test Artist")
            .biography("Test Biography")
            .photo("image")
            .build();

    @Test
    @DisplayName("FindById: returns artist when found")
    void findById_Success() {

        // Steps
        final var result = artistController.findById(artistDTO);

        // Assertions
        assertEquals(artistDTO, result);

    }

    @Test
    @DisplayName("FindById: throws ArtistNotFoundException when artist not found")
    void findById_NotFound() {

        // Stubs
        when(artistsClient.findById(artistId))
                .thenReturn(Optional.empty());

        // Assertions
        assertThrows(ArtistNotFoundException.class, () -> artistController.artist(artistId));
        verify(artistsClient).findById(artistId);

    }

    @Test
    @DisplayName("Delete: removes artist successfully")
    void delete_Success() {

        // Stubs
        doNothing().when(artistsClient)
                .delete(artistId);

        // Steps
        artistController.delete(artistDTO);

        // Assertions
        verify(artistsClient).delete(artistId);

    }

}
