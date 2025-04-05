package org.rococo.tests.data.rowMapper;

import org.rococo.tests.data.entity.CountryEntity;
import org.rococo.tests.enums.CountryCode;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class CountryRowMapper implements RowMapper<CountryEntity> {

    public static final CountryRowMapper INSTANCE = new CountryRowMapper();

    private CountryRowMapper() {
    }

    @Nonnull
    @Override
    public CountryEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return CountryEntity.builder()
                .id(rs.getObject("id", UUID.class))
                .name(rs.getString("name"))
                .code(CountryCode.valueOf(rs.getString("code")))
                .build();
    }

}
