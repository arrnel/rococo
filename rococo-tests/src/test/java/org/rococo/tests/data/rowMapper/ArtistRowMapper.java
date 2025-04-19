package org.rococo.tests.data.rowMapper;

import org.rococo.tests.data.entity.ArtistEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class ArtistRowMapper implements RowMapper<ArtistEntity> {

    public static final ArtistRowMapper INSTANCE = new ArtistRowMapper();

    private ArtistRowMapper() {
    }

    @Nonnull
    @Override
    public ArtistEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ArtistEntity.builder()
                .id(rs.getObject("id", UUID.class))
                .name(rs.getString("name"))
                .biography(rs.getString("biography"))
                .createdDate(rs.getObject("created_date", LocalDateTime.class))
                .build();
    }

}
