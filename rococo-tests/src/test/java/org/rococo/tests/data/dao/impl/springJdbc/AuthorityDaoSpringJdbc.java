package org.rococo.tests.data.dao.impl.springJdbc;

import org.rococo.tests.config.Config;
import org.rococo.tests.data.dao.AuthorityDao;
import org.rococo.tests.data.entity.AuthorityEntity;
import org.rococo.tests.data.rowMapper.AuthorityRowMapper;
import org.rococo.tests.data.tpl.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthorityDaoSpringJdbc implements AuthorityDao {

    private static final String AUTH_JDBC_URL = Config.getInstance().authJdbcUrl();

    @Override
    public void create(AuthorityEntity... authority) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(AUTH_JDBC_URL));
        jdbcTemplate.batchUpdate(
                """
                        INSERT INTO rococo.authorities
                            (user_id, authority)
                        VALUES
                            (?, ?)""",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authority[i].getUser().getId());
                        ps.setString(2, authority[i].getAuthority().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return authority.length;
                    }
                }
        );
    }

    @Override
    public @Nonnull Optional<AuthorityEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(AUTH_JDBC_URL));
        try {
            // QueryForObject not returns null if not found object. Method throws EmptyResultDataAccessException
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            """
                                    SELECT *
                                    FROM rococo.authorities
                                    WHERE id = ?""",
                            AuthorityRowMapper.INSTANCE,
                            id)
            );
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public @Nonnull List<AuthorityEntity> findByUserId(UUID userId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(AUTH_JDBC_URL));
        return jdbcTemplate.query(
                """
                        SELECT *
                        FROM rococo.authorities
                        WHERE user_id = ?""",
                AuthorityRowMapper.INSTANCE,
                userId
        );
    }

    @Override
    public @Nonnull List<AuthorityEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(AUTH_JDBC_URL));
        return jdbcTemplate.query(
                """
                        SELECT *
                        FROM rococo.authorities""",
                AuthorityRowMapper.INSTANCE
        );
    }

    @Override
    public void update(AuthorityEntity... authority) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(AUTH_JDBC_URL));
        jdbcTemplate.batchUpdate(
                """
                        UPDATE
                            rococo.authorities
                        SET
                            user_id = ?,
                            authority = ?
                        WHERE
                            id = ?""",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authority[i].getUser().getId());
                        ps.setString(2, authority[i].getAuthority().name());
                        ps.setObject(3, authority[i].getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return authority.length;
                    }
                }
        );
    }

    @Override
    public void remove(AuthorityEntity... authority) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(AUTH_JDBC_URL));
        jdbcTemplate.batchUpdate(
                """
                        DELETE
                        FROM rococo.authorities
                        WHERE id = ?""",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authority[i].getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return authority.length;
                    }
                }
        );
    }

    @Override
    public void removeAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(AUTH_JDBC_URL));
        jdbcTemplate.update(
                """
                        TRUNCATE TABLE
                            rococo.authorities
                        CASCADE"""
        );
    }

}
