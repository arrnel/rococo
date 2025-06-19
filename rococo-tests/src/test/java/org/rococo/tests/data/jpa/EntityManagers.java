package org.rococo.tests.data.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.commons.lang3.StringUtils;
import org.rococo.tests.config.Config;
import org.rococo.tests.data.tpl.DataSources;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityManagers {

    private static final Config CFG = Config.getInstance();

    private EntityManagers() {
    }

    private static final Map<String, EntityManagerFactory> emfs = new ConcurrentHashMap<>();

    @SuppressWarnings("resource")
    @Nonnull
    public static EntityManager em(@Nonnull String jdbcUrl) {
        return new ThreadSafeEntityManager(
                emfs.computeIfAbsent(
                        jdbcUrl,
                        key -> {
                            DataSources.dataSource(jdbcUrl);
                            final String persistenceUnitName = StringUtils.substringAfter(jdbcUrl, CFG.dbPort() + "/");
                            return Persistence.createEntityManagerFactory(persistenceUnitName);
                        }
                ).createEntityManager()
        );
    }

    public static void closeAllEmfs() {
        emfs.values().forEach(EntityManagerFactory::close);
    }

}
