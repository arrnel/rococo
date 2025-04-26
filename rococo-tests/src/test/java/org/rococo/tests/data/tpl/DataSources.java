package org.rococo.tests.data.tpl;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.p6spy.engine.spy.P6DataSource;
import org.apache.commons.lang3.StringUtils;
import org.rococo.tests.config.Config;

import javax.annotation.Nonnull;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DataSources {
    private DataSources() {
    }

    private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
    private static final Config CFG = Config.getInstance();

    @Nonnull
    public static DataSource dataSource(@Nonnull String jdbcUrl) {
        return dataSources.computeIfAbsent(
                jdbcUrl,
                key -> {
                    AtomikosDataSourceBean dsBean = new AtomikosDataSourceBean();
                    final String uniqId = StringUtils.substringAfter(jdbcUrl, CFG.dbPort() + "/");
                    dsBean.setUniqueResourceName(uniqId);
                    dsBean.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
                    Properties props = new Properties();
                    props.put("URL", jdbcUrl);
                    props.put("user", CFG.dbUser());
                    props.put("password", CFG.dbPassword());
                    dsBean.setXaProperties(props);
                    dsBean.setPoolSize(10);
                    dsBean.setMaxPoolSize(50);
                    dsBean.setBorrowConnectionTimeout(5);
                    P6DataSource p6DataSource = new P6DataSource(dsBean);
                    try {
                        InitialContext context = new InitialContext();
                        context.bind("java:comp/env/jdbc/" + uniqId, p6DataSource);
                    } catch (NamingException e) {
                        throw new RuntimeException(e);
                    }
                    return p6DataSource;
                }
        );
    }
}