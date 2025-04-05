package org.rococo.tests.data.rowMapper;

import org.rococo.tests.data.entity.ImageContentEntity;
import org.rococo.tests.data.entity.ImageMetadataEntity;
import org.rococo.tests.enums.EntityType;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class ImageMetadataRowMapper implements RowMapper<ImageMetadataEntity> {

    public static final ImageMetadataRowMapper INSTANCE = new ImageMetadataRowMapper();

    private ImageMetadataRowMapper() {
    }

    @Nonnull
    @Override
    public ImageMetadataEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ImageMetadataEntity.builder()
                .id(rs.getObject("metadata_id", UUID.class))
                .entityType(EntityType.valueOf(rs.getString("entity_type")))
                .entityId(rs.getObject("entity_id", UUID.class))
                .format(rs.getString("data"))
                .contentHash(rs.getString("hash"))
                .content(ImageContentEntity.builder()
                        .id(rs.getObject("content_id", UUID.class))
                        .data(rs.getBytes("data"))
                        .thumbnailData(rs.getBytes("thumbnail_data"))
                        .build())
                .build();
    }

}
