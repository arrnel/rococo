package org.rococo.tests.data.dao.impl.springJdbc;

import io.qameta.allure.Step;
import org.rococo.tests.config.Config;
import org.rococo.tests.data.dao.ImageMetadataDao;
import org.rococo.tests.data.entity.ImageMetadataEntity;
import org.rococo.tests.data.rowMapper.ImageMetadataRowMapper;
import org.rococo.tests.data.tpl.DataSources;
import org.rococo.tests.enums.EntityType;
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
public class ImageMetadataDaoSpringJdbc implements ImageMetadataDao {

    private static final String FILES_JDBC_URL = Config.getInstance().filesJdbcUrl();

    @Nonnull
    @Override
    @Step("[DB] Send create new image metadata request")
    public ImageMetadataEntity create(ImageMetadataEntity artist) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(FILES_JDBC_URL));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement("""
                                    INSERT INTO rococo.image_metadata
                                        (entity_type, entity_id, format, content_hash, content_id, created_date)
                                    VALUES
                                        (?, ?, ?, ?, ?, ?)""",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setString(1, artist.getEntityType().name());
                    ps.setObject(2, artist.getEntityId());
                    ps.setString(3, artist.getFormat());
                    ps.setString(4, artist.getContentHash());
                    ps.setObject(5, artist.getContent().getId());
                    ps.setObject(6, artist.getCreatedDate());
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
    @Step("[DB] Send find entity type and by id image metadata request")
    public Optional<ImageMetadataEntity> findByEntityTypeAndEntityId(EntityType entityType, UUID entityId) {
        try {
            NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(DataSources.dataSource(FILES_JDBC_URL));
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("""
                                    SELECT
                                        m.id as metadata_id,
                                        m.entity_type as entity_type,
                                        m.entity_id as entity_id,
                                        m.format as format,
                                        m.content_hash as hash,
                                        c.id as content_id,
                                        c.data as data,
                                        c.thumbnail_data as thumbnail_data,
                                        m.created_date as created_date
                                    FROM
                                        rococo.image_metadata m
                                    LEFT JOIN
                                        rococo.image_content c
                                    ON
                                        m.content_id = c.id
                                    WHERE
                                        m.entity_type = :entityType
                                    AND
                                        m.entity_id = :entityId""",
                            Map.of("entityType", entityType.name(), "entityId", entityId),
                            ImageMetadataRowMapper.INSTANCE
                    ));
        } catch (
                EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Nonnull
    @Override
    @Step("[DB] Send find all images metadata by entity type request")
    public List<ImageMetadataEntity> findAllByEntityTypeAndEntitiesId(EntityType entityType, List<UUID> entityIds) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(DataSources.dataSource(FILES_JDBC_URL));
        return jdbcTemplate.query("""
                        SELECT
                            m.id as metadata_id,
                            m.entity_type as entity_type,
                            m.entity_id as entity_id,
                            m.format as format,
                            m.content_hash as hash,
                            c.id as content_id,
                            c.data as data,
                            c.thumbnail_data as thumbnail_data,
                            m.created_date as created_date
                        FROM
                            rococo.image_metadata m
                        LEFT JOIN
                            rococo.image_content c
                        ON
                            m.content_id = c.id
                        WHERE
                            m.entity_type = :entityType
                        AND
                            m.entity_id IN (:entityIds)""",
                Map.of("entityType", entityType.name(), "entityIds", entityIds),
                ImageMetadataRowMapper.INSTANCE);
    }

    @Nonnull
    @Override
    @Step("[DB] Send update image metadata request")
    public ImageMetadataEntity update(ImageMetadataEntity artist) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(FILES_JDBC_URL));
        jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement("""
                            UPDATE
                                rococo.image_metadata
                            SET
                                entity_type = ?,
                                entity_id = ?,
                                format = ?,
                                content_hash = ?,
                                content_id = ?
                            WHERE
                                id = ?"""
                    );
                    ps.setString(1, artist.getEntityType().name());
                    ps.setObject(2, artist.getEntityId());
                    ps.setString(3, artist.getFormat());
                    ps.setString(4, artist.getContentHash());
                    ps.setObject(5, artist.getContent().getId());
                    ps.setObject(6, artist.getId());
                    return ps;
                }
        );
        return artist;
    }

    @Override
    @Step("[DB] Send delete image metadata request")
    public void remove(ImageMetadataEntity artist) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(FILES_JDBC_URL));
        jdbcTemplate.update("""
                        DELETE
                        FROM
                            rococo.image_metadata
                        WHERE
                            id = ?""",
                artist.getId()
        );
    }

    @Override
    @Step("[DB] Send truncate image metadata table request")
    public void remove(EntityType entityType) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(FILES_JDBC_URL));
        jdbcTemplate.update("""
                        DELETE
                        FROM
                            rococo.image_metadata
                        WHERE
                            entity_type = ?""",
                entityType.name()
        );
    }

    @Override
    @Step("[DB] Send truncate images metadata table request")
    public void removeAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(FILES_JDBC_URL));
        jdbcTemplate.update(
                """
                        TRUNCATE TABLE
                            rococo.image_metadata
                        CASCADE"""
        );
    }

}
