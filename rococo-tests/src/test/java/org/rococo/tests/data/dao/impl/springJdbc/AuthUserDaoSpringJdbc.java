package org.rococo.tests.data.dao.impl.springJdbc;

import io.qameta.allure.Step;
import org.rococo.tests.config.Config;
import org.rococo.tests.data.dao.AuthUserDao;
import org.rococo.tests.data.entity.AuthUserEntity;
import org.rococo.tests.data.rowMapper.AuthUserRowMapper;
import org.rococo.tests.data.tpl.DataSources;
import org.rococo.tests.ex.UserAlreadyExistsException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthUserDaoSpringJdbc implements AuthUserDao {

    private static final String AUTH_JDBC_URL = Config.getInstance().authJdbcUrl();

    @Nonnull
    @Override
    @Step("[DB] Send create new auth user request")
    public AuthUserEntity create(AuthUserEntity user) {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(AUTH_JDBC_URL));
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                """
                                        INSERT INTO rococo.users (
                                                          username,
                                                          password,
                                                          enabled,
                                                          account_non_expired,
                                                          account_non_locked,
                                                          credentials_non_expired)
                                        VALUES
                                            (?, ?, ?, ?, ?, ?)""",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setString(1, user.getUsername());
                        ps.setString(2, PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(user.getPassword()));
                        ps.setBoolean(3, user.getEnabled());
                        ps.setBoolean(4, user.getAccountNonExpired());
                        ps.setBoolean(5, user.getAccountNonLocked());
                        ps.setBoolean(6, user.getCredentialsNonExpired());
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

    @Nonnull
    @Override
    @Step("[DB] Send find auth user by id request")
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(AUTH_JDBC_URL));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("""
                                    SELECT *
                                    FROM rococo.users
                                    WHERE id = ?""",
                            AuthUserRowMapper.INSTANCE,
                            id)
            );
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Nonnull
    @Override
    @Step("[DB] Send find auth user by username request")
    public Optional<AuthUserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(AUTH_JDBC_URL));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("""
                                    SELECT *
                                    FROM rococo.users
                                    WHERE username = ?""",
                            AuthUserRowMapper.INSTANCE,
                            username
                    )
            );
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Nonnull
    @Override
    @Step("[DB] Send find all auth users request")
    public List<AuthUserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(AUTH_JDBC_URL));
        return jdbcTemplate.query("""
                        SELECT *
                        FROM rococo.users""",
                AuthUserRowMapper.INSTANCE
        );
    }

    @Nonnull
    @Override
    @Step("[DB] Send update auth user request")
    public AuthUserEntity update(AuthUserEntity user) {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(AUTH_JDBC_URL));
            jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement(
                                """
                                        UPDATE
                                            rococo.users
                                        SET
                                            username = ?,
                                            password = ?,
                                            enabled = ?,
                                            account_non_expired = ?,
                                            account_non_locked = ?,
                                            credentials_non_expired = ?
                                        WHERE
                                            id = ?"""
                        );
                        ps.setString(1, user.getUsername());
                        ps.setString(2, PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(user.getPassword()));
                        ps.setBoolean(3, user.getEnabled());
                        ps.setBoolean(4, user.getAccountNonExpired());
                        ps.setBoolean(5, user.getAccountNonLocked());
                        ps.setBoolean(6, user.getCredentialsNonExpired());
                        ps.setObject(6, user.getId());
                        return ps;
                    }
            );
            return user;
        } catch (DuplicateKeyException ex) {
            throw new UserAlreadyExistsException(user.getUsername());
        }
    }

    @Override
    @Step("[DB] Send delete auth user request")
    public void remove(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(AUTH_JDBC_URL));
        jdbcTemplate.update(
                """
                        DELETE
                        FROM rococo.users
                        WHERE id = ?""",
                user.getId()
        );
    }

    @Override
    public void removeAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(AUTH_JDBC_URL));
        jdbcTemplate.update(
                """
                        TRUNCATE TABLE
                            rococo.users
                        CASCADE"""
        );
    }

}
