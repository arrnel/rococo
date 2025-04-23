package org.rococo.files.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.files.data.entity.EntityType;
import org.rococo.files.data.entity.ImageContentEntity;
import org.rococo.files.data.entity.ImageFilter;
import org.rococo.files.data.entity.ImageMetadataEntity;
import org.rococo.files.data.repository.ImageMetadataRepository;
import org.rococo.files.specs.ImageSpecs;
import org.rococo.files.specs.value.EqualEnumSpec;
import org.rococo.files.specs.value.InUUIDSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Sql("/sql/images.sql")
@Transactional
@DataJpaTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        ImageSpecs.class,
        InUUIDSpec.class,
        EqualEnumSpec.class
})
@DisplayName("ImageMetadataRepository: Integration tests")
class ImageMetadataRepositoryIT {

    @Autowired
    ImageMetadataRepository metadataRepository;

    @Autowired
    ImageSpecs imageSpecs;

    private final ImageMetadataEntity expectedImage = ImageMetadataEntity.builder()
            .id(UUID.fromString("667078a1-964d-4086-8d0f-e4f487a6b35a"))
            .entityType(EntityType.MUSEUM)
            .entityId(UUID.fromString("c4dd91c2-2c1c-4f5d-bdb8-4eee66f2b354"))
            .format("png")
            .contentHash("c6afb7421eb61816cd54698493ff17573e020431c7b8486c39ceb9b3c07cc54b")
            .content(ImageContentEntity.builder()
                    .id(UUID.fromString("08141894-56fa-4b96-9524-6323c45e3dc0"))
                    .data("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+ip1sAAAAASUVORK5CYII=".getBytes(Charset.defaultCharset()))
                    .thumbnailData("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mO89B8AAqkB05ycXjIAAAAASUVORK5CYII=".getBytes(Charset.defaultCharset()))
                    .build())
            .createdDate(LocalDateTime.of(2024, 1, 1, 11, 15))
            .build();

    @Test
    @DisplayName("FindByEntityTypeAndEntityId: returns metadata")
    void findByEntityTypeAndEntityId_ReturnsImageMetadata() {

        // Steps
        var result = metadataRepository.findByEntityTypeAndEntityId(
                expectedImage.getEntityType(),
                expectedImage.getEntityId()
        ).orElse(ImageMetadataEntity.empty());

        // Assertions
        assertAll(
                () -> assertEquals(expectedImage.getId(), result.getId()),
                () -> assertEquals(expectedImage.getEntityType(), result.getEntityType()),
                () -> assertEquals(expectedImage.getEntityId(), result.getEntityId()),
                () -> assertEquals(expectedImage.getFormat(), result.getFormat()),
                () -> assertEquals(expectedImage.getContentHash(), result.getContentHash()),
                () -> assertArrayEquals(expectedImage.getContent().getData(), result.getContent().getData()),
                () -> assertArrayEquals(expectedImage.getContent().getThumbnailData(), result.getContent().getThumbnailData())
        );

    }

    @Test
    @DisplayName("FindByEntityTypeAndEntityId: returnsEmpty when metadata not found by entity_type and entity_id")
    void findByEntityTypeAndEntityId_ReturnsEmpty() {

        // Steps
        var result = metadataRepository.findByEntityTypeAndEntityId(
                EntityType.MUSEUM,
                UUID.randomUUID()
        );

        // Assertions
        assertTrue(result.isEmpty());

    }

    @ParameterizedTest(name = "Case: {0}")
    @MethodSource("findAllImageMetadata_ArgumentsProvider")
    @DisplayName("FindAllByCriteria: returns ImageMetadata")
    void findAllByCriteria_ReturnsImageMetadata(
            String caseName,
            ImageFilter filter,
            List<UUID> expectedImageMetadataIds
    ) {

        // Data
        final Specification<ImageMetadataEntity> specs = imageSpecs.findByCriteria(filter);
        final Pageable pageable = PageRequest.of(0, 10);

        // Steps
        Page<ImageMetadataEntity> result = metadataRepository.findAll(specs, pageable);
        List<UUID> actualImageMetadataIds = result.getContent().stream()
                .map(ImageMetadataEntity::getId)
                .toList();

        // Assertions
        assertAll(
                () -> assertEquals(expectedImageMetadataIds.size(), actualImageMetadataIds.size()),
                () -> assertTrue(actualImageMetadataIds.containsAll(expectedImageMetadataIds))
        );

    }

    static Stream<Arguments> findAllImageMetadata_ArgumentsProvider() {
        return Stream.of(
                Arguments.of("by entity type",
                        ImageFilter.builder()
                                .entityType(EntityType.MUSEUM)
                                .build(),
                        List.of(
                                UUID.fromString("667078a1-964d-4086-8d0f-e4f487a6b35a"),
                                UUID.fromString("06709a0d-4495-4939-bb61-f8b7d445cde4")
                        )
                ),
                Arguments.of("by entity type and entity id",
                        ImageFilter.builder()
                                .entityType(EntityType.MUSEUM)
                                .entityIds(new UUID[]{UUID.fromString("e3e2bd19-e5b8-4e6f-bdac-0e8117bcb7c9")})
                                .build(),
                        List.of(
                                UUID.fromString("06709a0d-4495-4939-bb61-f8b7d445cde4")
                        )
                )
        );
    }

}
