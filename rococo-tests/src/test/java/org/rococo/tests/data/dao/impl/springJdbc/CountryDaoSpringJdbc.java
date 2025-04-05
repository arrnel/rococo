package org.rococo.tests.data.dao.impl.springJdbc;

import org.rococo.tests.config.Config;
import org.rococo.tests.data.dao.CountryDao;
import org.rococo.tests.data.entity.CountryEntity;
import org.rococo.tests.data.rowMapper.CountryRowMapper;
import org.rococo.tests.data.tpl.DataSources;
import org.rococo.tests.enums.CountryCode;
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
public class CountryDaoSpringJdbc implements CountryDao {

    private static final String COUNTRIES_JDBC_URL = Config.getInstance().countriesJdbcUrl();

    @Nonnull
    @Override
    public CountryEntity create(CountryEntity country) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(COUNTRIES_JDBC_URL));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement("""
                                    INSERT INTO rococo.countries
                                        (name, code)
                                    VALUES
                                        (?, ?)""",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setString(1, country.getName());
                    ps.setString(2, country.getCode().toString());
                    return ps;
                },
                keyHolder
        );

        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        country.setId(generatedKey);
        return country;

    }

    @Nonnull
    @Override
    public Optional<CountryEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(COUNTRIES_JDBC_URL));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("""
                                    SELECT *
                                    FROM
                                        rococo.countries
                                    WHERE
                                        id = ?""",
                            CountryRowMapper.INSTANCE,
                            id
                    ));

        } catch (
                EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Nonnull
    @Override
    public Optional<CountryEntity> findByCode(CountryCode code) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(COUNTRIES_JDBC_URL));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("""
                                    SELECT *
                                    FROM
                                        rococo.countries
                                    WHERE
                                        code = ?""",
                            CountryRowMapper.INSTANCE,
                            code.name()
                    ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Nonnull
    @Override
    public List<CountryEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(COUNTRIES_JDBC_URL));
        return jdbcTemplate.query("""
                        SELECT *
                        FROM rococo.countries""",
                CountryRowMapper.INSTANCE
        );
    }

    @Nonnull
    @Override
    public CountryEntity update(CountryEntity country) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(COUNTRIES_JDBC_URL));
        jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement("""
                            UPDATE
                                rococo.countries
                            SET
                                name = ?
                            WHERE
                                id = ?"""
                    );
                    ps.setString(1, country.getName());
                    ps.setObject(2, country.getId());
                    return ps;
                }
        );
        return country;
    }

    @Override
    public void remove(CountryEntity country) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(COUNTRIES_JDBC_URL));
        jdbcTemplate.update("""
                        DELETE
                        FROM
                            rococo.countries
                        WHERE
                            id = ?""",
                country.getId()
        );
    }

    @Override
    public void removeAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(COUNTRIES_JDBC_URL));
        jdbcTemplate.update(
                """
                        TRUNCATE TABLE
                            rococo.countries
                        CASCADE"""
        );
    }

}
