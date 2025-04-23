package org.rococo.tests.data.rowMapper;

import org.rococo.tests.data.entity.UserEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserRowMapper implements RowMapper<UserEntity> {

    public static final UserRowMapper INSTANCE = new UserRowMapper();

    private UserRowMapper() {
    }

    @Nonnull
    @Override
    public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserEntity.builder()
                .id(rs.getObject("id", UUID.class))
                .username(rs.getString("username"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .createdDate(rs.getObject("created_date", LocalDateTime.class))
                .build();
    }

}
