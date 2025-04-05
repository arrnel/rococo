package org.rococo.tests.data.dao.impl.springJdbc;

import org.rococo.tests.config.Config;
import org.rococo.tests.data.dao.ArtistDao;
import org.rococo.tests.data.entity.ArtistEntity;
import org.rococo.tests.data.rowMapper.ArtistRowMapper;
import org.rococo.tests.data.tpl.DataSources;
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
public class ArtistDaoSpringJdbc implements ArtistDao {

    private static final String ARTISTS_JDBC_URL = Config.getInstance().artistsJdbcUrl();

    @Nonnull
    @Override
    public ArtistEntity create(ArtistEntity artist) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(ARTISTS_JDBC_URL));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement("""
                                    INSERT INTO rococo.artists
                                        (name, biography)
                                    VALUES
                                        (?, ?)""",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setString(1, artist.getName());
                    ps.setString(2, artist.getBiography());
                    return ps;
                },
                keyHolder
        );

        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        artist.setId(generatedKey);
        return artist;

    }

    @Nonnull
    @Override
    public Optional<ArtistEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(ARTISTS_JDBC_URL));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("""
                                    SELECT *
                                    FROM
                                        rococo.artists
                                    WHERE
                                        id = ?""",
                            ArtistRowMapper.INSTANCE,
                            id
                    ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Nonnull
    @Override
    public Optional<ArtistEntity> findByName(String name) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(ARTISTS_JDBC_URL));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("""
                                    SELECT *
                                    FROM
                                        rococo.artists
                                    WHERE
                                        name = ?""",
                            ArtistRowMapper.INSTANCE,
                            name
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    @Nonnull
    @Override
    public List<ArtistEntity> findAllByPartialName(String name) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(ARTISTS_JDBC_URL));
        return jdbcTemplate.query("""
                        SELECT *
                        FROM rococo.artists
                        WHERE name LIKE (:name)""",
                ArtistRowMapper.INSTANCE,
                "%" + name + "%"
        );
    }

    @Nonnull
    @Override
    public List<ArtistEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(ARTISTS_JDBC_URL));
        return jdbcTemplate.query("""
                        SELECT *
                        FROM rococo.artists""",
                ArtistRowMapper.INSTANCE
        );
    }

    @Nonnull
    @Override
    public ArtistEntity update(ArtistEntity artist) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(ARTISTS_JDBC_URL));
        jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement("""
                            UPDATE
                                rococo.artists
                            SET
                                name = ?,
                                biography = ?
                            WHERE
                                id = ?"""
                    );
                    ps.setString(1, artist.getName());
                    ps.setString(2, artist.getBiography());
                    ps.setObject(3, artist.getId());
                    return ps;
                }
        );
        return artist;
    }

    @Override
    public void remove(ArtistEntity artist) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(ARTISTS_JDBC_URL));
        jdbcTemplate.update("""
                        DELETE
                        FROM
                            rococo.artists
                        WHERE
                            id = ?""",
                artist.getId()
        );
    }

    @Override
    public void removeAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(ARTISTS_JDBC_URL));
        jdbcTemplate.update(
                """
                        TRUNCATE TABLE
                            rococo.artists
                        CASCADE"""
        );
    }

}
