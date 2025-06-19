package org.rococo.tests.jupiter.extension;


import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.rococo.tests.config.Config;
import org.rococo.tests.data.jpa.EntityManagers;
import org.rococo.tests.data.tpl.Connections;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.service.db.ArtistServiceDb;
import org.rococo.tests.service.db.MuseumServiceDb;
import org.rococo.tests.service.db.PaintingServiceDb;
import org.rococo.tests.service.db.UserServiceDb;

import java.util.stream.Stream;

/**
 * EXTENSION HAS GLOBAL REGISTRATION TYPE
 */
public class DatabasesExtension implements SuiteExtension, AfterEachCallback {

    private static final boolean CLEAR_DB = System.getProperty("tests.db_cleanup", "false").equals("true");

    private static final Config CFG = Config.getInstance();
    private static final String[] jdbcUrls = new String[]{
            CFG.artistsJdbcUrl(),
            CFG.countriesJdbcUrl(),
            CFG.filesJdbcUrl(),
            CFG.museumsJdbcUrl(),
            CFG.paintingsJdbcUrl(),
            CFG.usersJdbcUrl()};

    @Override
    public void beforeSuite(ExtensionContext context) {
        clearDb();
        createTestUser();
    }

    @Override
    public void afterSuite() {
        if (CLEAR_DB) {
            clearDb();
            createTestUser();
        }
        Connections.closeAllConnections();
        EntityManagers.closeAllEmfs();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        Stream.of(jdbcUrls)
                .forEach(jdbcUrl -> {
                    Connections.holder(jdbcUrl).close();
                    EntityManagers.em(jdbcUrl).close();
                });
    }

    void clearDb() {

        new ArtistServiceDb().clearAll();
        new MuseumServiceDb().clearAll();
        new PaintingServiceDb().clearAll();
        new UserServiceDb().clearAll();

    }

    void createTestUser() {
        new UserServiceDb().create(
                new UserDTO()
                        .setUsername(CFG.testUserName())
                        .password(CFG.testUserPassword()));
    }

}
