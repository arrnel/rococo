package org.rococo.tests.data.dao.impl.springJdbc;

import org.rococo.tests.config.Config;
import org.rococo.tests.data.dao.MuseumDao;
import org.rococo.tests.data.entity.MuseumEntity;
import org.rococo.tests.data.rowMapper.MuseumRowMapper;
import org.rococo.tests.data.tpl.DataSources;
import org.rococo.tests.ex.MuseumAlreadyExistsException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class MuseumDaoSpringJdbc implements MuseumDao {

    private static final String MUSEUMS_JDBC_URL = Config.getInstance().museumsJdbcUrl();

    @Nonnull
    @Override
    public MuseumEntity create(MuseumEntity museum) {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(MUSEUMS_JDBC_URL));
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement("""
                                        INSERT INTO rococo.museums
                                            (title, description, country_id, city, created_date)
                                        VALUES
                                            (?, ?, ?, ?, ?)""",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setString(1, museum.getTitle());
                        ps.setString(2, museum.getDescription());
                        ps.setObject(3, museum.getCountryId());
                        ps.setString(4, museum.getCity());
                        ps.setObject(5, museum.getCreatedDate());
                        return ps;
                    },
                    keyHolder
            );

            final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
            museum.setId(generatedKey);
            return museum;
        } catch (DuplicateKeyException ex) {
            throw new MuseumAlreadyExistsException(museum.getTitle());
        }
    }

    @Nonnull
    @Override
    public Optional<MuseumEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(MUSEUMS_JDBC_URL));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("""
                                    SELECT *
                                    FROM
                                        rococo.museums
                                    WHERE
                                        id = ?""",
                            MuseumRowMapper.INSTANCE,
                            id
                    ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Nonnull
    @Override
    public Optional<MuseumEntity> findByTitle(String title) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(MUSEUMS_JDBC_URL));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("""
                                    SELECT *
                                    FROM
                                        rococo.museums
                                    WHERE
                                        title = ?""",
                            MuseumRowMapper.
                                    INSTANCE,
                            title
                    ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Nonnull
    @Override
    public List<MuseumEntity> findAllByPartialTitle(String partialTitle) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(MUSEUMS_JDBC_URL));
        return jdbcTemplate.query("""
                        SELECT *
                        FROM
                            rococo.museums
                        WHERE
                            LOWER(title) LIKE (?)""",
                MuseumRowMapper.INSTANCE,
                String.join(",", "%" + partialTitle.toLowerCase() + "%")
        );
    }

    @Nonnull
    @Override
    public List<MuseumEntity> findAllByIds(List<UUID> ids) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(DataSources.dataSource(MUSEUMS_JDBC_URL));
        return jdbcTemplate.query("""
                        SELECT *
                        FROM
                            rococo.museums
                        WHERE
                            id IN (:ids)""",
                Map.of("ids", ids),
                MuseumRowMapper.INSTANCE
        );
    }

    @Nonnull
    @Override
    public List<MuseumEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(MUSEUMS_JDBC_URL));
        return jdbcTemplate.query("""
                        SELECT *
                        FROM rococo.museums""",
                MuseumRowMapper.INSTANCE
        );
    }

    @Nonnull
    @Override
    public MuseumEntity update(MuseumEntity museum) {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(MUSEUMS_JDBC_URL));
            jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement("""
                                UPDATE
                                    rococo.museums
                                SET
                                    title = ?,
                                    description = ?,
                                    country_id = ?,
                                    city = ?
                                WHERE
                                    id = ?"""
                        );
                        ps.setString(1, museum.getTitle());
                        ps.setString(2, museum.getDescription());
                        ps.setObject(3, museum.getCountryId());
                        ps.setString(4, museum.getCity());
                        ps.setObject(5, museum.getId());
                        return ps;
                    }
            );
            return museum;
        } catch (DuplicateKeyException ex) {
            throw new MuseumAlreadyExistsException(museum.getTitle());
        }
    }

    @Override
    public void remove(MuseumEntity museum) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(MUSEUMS_JDBC_URL));
        jdbcTemplate.update("""
                        DELETE
                        FROM
                            rococo.museums
                        WHERE
                            id = ?""",
                museum.getId()
        );
    }

    @Override
    public void removeAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(MUSEUMS_JDBC_URL));
        jdbcTemplate.update(
                """
                        TRUNCATE TABLE
                            rococo.museums
                        CASCADE"""
        );
    }

}
