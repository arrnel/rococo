package org.rococo.tests.data.rowMapper;

import org.rococo.tests.data.entity.AuthUserEntity;
import org.rococo.tests.data.entity.AuthorityEntity;
import org.rococo.tests.enums.Authority;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthorityRowMapper implements RowMapper<AuthorityEntity> {

    public static final AuthorityRowMapper INSTANCE = new AuthorityRowMapper();

    private AuthorityRowMapper() {
    }

    @Nonnull
    @Override
    public AuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return AuthorityEntity.builder()
                .id(rs.getObject("id", UUID.class))
                .user(AuthUserEntity.builder().id(rs.getObject("user_id", UUID.class)).build())
                .authority(Authority.valueOf(rs.getString("authority")))
                .build();
    }

}
