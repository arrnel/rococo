package org.rococo.tests.data.rowMapper;

import org.rococo.tests.data.entity.PaintingEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class PaintingRowMapper implements RowMapper<PaintingEntity> {

    public static final PaintingRowMapper INSTANCE = new PaintingRowMapper();

    private PaintingRowMapper() {
    }

    @Nonnull
    @Override
    public PaintingEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return PaintingEntity.builder()
                .id(rs.getObject("id", UUID.class))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .artistId(rs.getObject("artist_id", UUID.class))
                .museumId(rs.getObject("museum_id", UUID.class))
                .createdDate(rs.getObject("created_date", LocalDateTime.class))
                .build();
    }

}
