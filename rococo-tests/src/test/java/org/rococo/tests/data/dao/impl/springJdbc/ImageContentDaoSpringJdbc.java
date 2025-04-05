package org.rococo.tests.data.dao.impl.springJdbc;

import org.rococo.tests.config.Config;
import org.rococo.tests.data.dao.ImageContentDao;
import org.rococo.tests.data.entity.ImageContentEntity;
import org.rococo.tests.data.rowMapper.ImageContentRowMapper;
import org.rococo.tests.data.tpl.DataSources;
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
public class ImageContentDaoSpringJdbc implements ImageContentDao {

    private static final String FILES_JDBC_URL = Config.getInstance().filesJdbcUrl();

    @Nonnull
    @Override
    public ImageContentEntity create(ImageContentEntity artist) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(FILES_JDBC_URL));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement("""
                                    INSERT INTO rococo.image_content
                                        (data, thumbnail_data)
                                    VALUES
                                        (?, ?)""",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setBytes(1, artist.getData());
                    ps.setBytes(2, artist.getThumbnailData());
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
    public Optional<ImageContentEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(FILES_JDBC_URL));
        return Optional.ofNullable(
                jdbcTemplate.queryForObject("""
                                SELECT *
                                FROM
                                    rococo.image_content
                                WHERE
                                    id = ?""",
                        ImageContentRowMapper.INSTANCE,
                        id
                ));
    }

    @Nonnull
    @Override
    public List<ImageContentEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(FILES_JDBC_URL));
        return jdbcTemplate.query("""
                        SELECT *
                        FROM rococo.image_content""",
                ImageContentRowMapper.INSTANCE
        );
    }

    @Nonnull
    @Override
    public ImageContentEntity update(ImageContentEntity artist) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(FILES_JDBC_URL));
        jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement("""
                            UPDATE
                                rococo.image_content
                            SET
                                data = ?,
                                thumbnail_data = ?
                            WHERE
                                id = ?"""
                    );
                    ps.setBytes(1, artist.getData());
                    ps.setBytes(2, artist.getThumbnailData());
                    ps.setObject(3, artist.getId());
                    return ps;
                }
        );
        return artist;
    }

    @Override
    public void remove(ImageContentEntity artist) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(FILES_JDBC_URL));
        jdbcTemplate.update("""
                        DELETE
                        FROM
                            rococo.image_content
                        WHERE
                            id = ?""",
                artist.getId()
        );
    }

    @Override
    public void removeAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(FILES_JDBC_URL));
        jdbcTemplate.update(
                """
                        TRUNCATE TABLE
                            rococo.image_content
                        CASCADE"""
        );
    }

}
