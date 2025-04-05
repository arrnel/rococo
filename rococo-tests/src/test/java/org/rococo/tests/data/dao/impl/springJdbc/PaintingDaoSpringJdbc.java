package org.rococo.tests.data.dao.impl.springJdbc;

import org.rococo.tests.config.Config;
import org.rococo.tests.data.dao.PaintingDao;
import org.rococo.tests.data.entity.PaintingEntity;
import org.rococo.tests.data.rowMapper.PaintingRowMapper;
import org.rococo.tests.data.tpl.DataSources;
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
public class PaintingDaoSpringJdbc implements PaintingDao {

    private static final String PAINTINGS_JDBC_URL = Config.getInstance().paintingsJdbcUrl();

    @Nonnull
    @Override
    public PaintingEntity create(PaintingEntity painting) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(PAINTINGS_JDBC_URL));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement("""
                                    INSERT INTO rococo.paintings
                                        (title, description, artist_id, museum_id)
                                    VALUES
                                        (?, ?, ?, ?)""",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setString(1, painting.getTitle());
                    ps.setString(2, painting.getDescription());
                    ps.setObject(3, painting.getArtistId());
                    ps.setObject(4, painting.getMuseumId());
                    return ps;
                },
                keyHolder
        );

        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        painting.setId(generatedKey);
        return painting;

    }

    @Nonnull
    @Override
    public Optional<PaintingEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(PAINTINGS_JDBC_URL));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("""
                                    
                                        SELECT *
                                    FROM
                                        rococo.paintings
                                    WHERE
                                        id = ?""",
                            PaintingRowMapper.
                                    INSTANCE,
                            id
                    ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    @Nonnull
    @Override
    public Optional<PaintingEntity> findByTitle(String title) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(PAINTINGS_JDBC_URL));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("""
                                    SELECT *
                                    FROM
                                        rococo.paintings
                                    WHERE
                                        title = ?""",
                            PaintingRowMapper.INSTANCE,
                            title
                    ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Nonnull
    @Override
    public List<PaintingEntity> findAllByPartialTitle(String partialTitle) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(DataSources.dataSource(PAINTINGS_JDBC_URL));
        return jdbcTemplate.query("""
                        SELECT *
                        FROM rococo.paintings
                        WHERE LOWER(title) LIKE LOWER(:title)""",
                Map.of("title", "%" + partialTitle + "%"),
                PaintingRowMapper.INSTANCE
        );
    }

    @Nonnull
    @Override
    public List<PaintingEntity> findAllByArtistId(UUID artistId) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(DataSources.dataSource(PAINTINGS_JDBC_URL));
        return jdbcTemplate.query("""
                        SELECT *
                        FROM rococo.paintings
                        WHERE artist_id = :artistId""",
                Map.of("artistId", artistId),
                PaintingRowMapper.INSTANCE
        );
    }

    @Nonnull
    @Override
    public List<PaintingEntity> findAllByTitles(List<String> titles) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(DataSources.dataSource(PAINTINGS_JDBC_URL));
        return jdbcTemplate.query("""
                        SELECT *
                        FROM rococo.paintings
                        WHERE id IN (:titles)""",
                Map.of("titles", titles),
                PaintingRowMapper.INSTANCE
        );
    }

    @Nonnull
    @Override
    public List<PaintingEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(PAINTINGS_JDBC_URL));
        return jdbcTemplate.query("""
                        SELECT *
                        FROM rococo.paintings""",
                PaintingRowMapper.INSTANCE
        );
    }

    @Nonnull
    @Override
    public PaintingEntity update(PaintingEntity painting) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(PAINTINGS_JDBC_URL));
        jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement("""
                            UPDATE
                                rococo.paintings
                            SET
                                title = ?,
                                description = ?,
                                artist_id = ?,
                                museum_id = ?
                            WHERE
                                id = ?"""
                    );
                    ps.setString(1, painting.getTitle());
                    ps.setString(2, painting.getDescription());
                    ps.setObject(3, painting.getArtistId());
                    ps.setObject(4, painting.getMuseumId());
                    ps.setObject(5, painting.getId());
                    return ps;
                }
        );
        return painting;
    }

    @Override
    public void remove(PaintingEntity painting) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(PAINTINGS_JDBC_URL));
        jdbcTemplate.update("""
                        DELETE
                        FROM
                            rococo.paintings
                        WHERE
                            id = ?""",
                painting.getId()
        );
    }

    @Override
    public void removeAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(PAINTINGS_JDBC_URL));
        jdbcTemplate.update(
                """
                        TRUNCATE TABLE
                            rococo.paintings
                        CASCADE"""
        );
    }

}
