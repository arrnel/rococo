package org.rococo.tests.data.rowMapper;

import org.rococo.tests.data.entity.ImageContentEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class ImageContentRowMapper implements RowMapper<ImageContentEntity> {

    public static final ImageContentRowMapper INSTANCE = new ImageContentRowMapper();

    private ImageContentRowMapper() {
    }

    @Nonnull
    @Override
    public ImageContentEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ImageContentEntity.builder()
                .id(rs.getObject("id", UUID.class))
                .data(rs.getBytes("data"))
                .thumbnailData(rs.getBytes("thumbnail_data"))
                .build();
    }

}
