package org.rococo.tests.data.dao.impl.springJdbc;

import org.rococo.tests.config.Config;
import org.rococo.tests.data.dao.UserDao;
import org.rococo.tests.data.entity.UserEntity;
import org.rococo.tests.data.rowMapper.UserRowMapper;
import org.rococo.tests.data.tpl.DataSources;
import org.rococo.tests.ex.UserAlreadyExistsException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserDaoSpringJdbc implements UserDao {

    private static final String USERS_JDBC_URL = Config.getInstance().usersJdbcUrl();

    @Override
    public @Nonnull UserEntity create(UserEntity user) {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(USERS_JDBC_URL));
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement("""
                                        INSERT INTO rococo.users
                                            (username, first_name, last_name, created_date)
                                        VALUES
                                            (?, ?, ?, ?)""",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setString(1, user.getUsername());
                        ps.setString(2, user.getFirstName());
                        ps.setString(3, user.getLastName());
                        ps.setObject(4, user.getCreatedDate());
                        return ps;
                    },
                    keyHolder
            );

            final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
            user.setId(generatedKey);
            return user;
        } catch (DuplicateKeyException ex) {
            throw new UserAlreadyExistsException(user.getUsername());
        }

    }

    @Override
    public @Nonnull Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(USERS_JDBC_URL));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("""
                                    SELECT *
                                    FROM
                                        rococo.users
                                    WHERE
                                        id = ?""",
                            UserRowMapper.INSTANCE,
                            id
                    ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public @Nonnull Optional<UserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(USERS_JDBC_URL));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("""
                                    SELECT *
                                    FROM
                                        rococo.users
                                    WHERE
                                        username = ?""",
                            UserRowMapper.INSTANCE,
                            username
                    ));
        } catch (
                EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public @Nonnull List<UserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(USERS_JDBC_URL));
        return jdbcTemplate.query("""
                        SELECT *
                        FROM rococo.users""",
                UserRowMapper.INSTANCE
        );
    }

    @Override
    public @Nonnull UserEntity update(UserEntity user) {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(USERS_JDBC_URL));
            jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement("""
                                UPDATE
                                    rococo.users
                                SET
                                    username = ?,
                                    first_name = ?,
                                    last_name = ?
                                WHERE
                                    id = ?"""
                        );
                        ps.setString(1, user.getUsername());
                        ps.setString(2, user.getFirstName());
                        ps.setString(3, user.getLastName());
                        ps.setObject(4, user.getId());
                        return ps;
                    }
            );
            return user;
        } catch (DuplicateKeyException ex) {
            throw new UserAlreadyExistsException(user.getUsername());
        }
    }

    @Override
    public void remove(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(USERS_JDBC_URL));
        jdbcTemplate.update("""
                        DELETE
                        FROM
                            rococo.users
                        WHERE
                            id = ?""",
                user.getId()
        );
    }

    @Override
    public void removeAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(USERS_JDBC_URL));
        jdbcTemplate.update(
                """
                        TRUNCATE TABLE
                            rococo.users
                        CASCADE"""
        );
    }

}
