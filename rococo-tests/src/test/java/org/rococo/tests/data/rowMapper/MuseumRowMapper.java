package org.rococo.tests.data.rowMapper;

import org.rococo.tests.data.entity.MuseumEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class MuseumRowMapper implements RowMapper<MuseumEntity> {

    public static final MuseumRowMapper INSTANCE = new MuseumRowMapper();

    private MuseumRowMapper() {
    }

    @Nonnull
    @Override
    public MuseumEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return MuseumEntity.builder()
                .id(rs.getObject("id", UUID.class))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .countryId(rs.getObject("country_id", UUID.class))
                .city(rs.getString("city"))
                .build();
    }

}
