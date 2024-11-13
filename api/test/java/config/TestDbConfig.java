package config;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.TestPropertySource;

@TestConfiguration
@TestPropertySource("classpath:application.yml")
public class TestDbConfig {

  @Bean
  public DBConfiguration dbConfig() {
    DBConfigurationBuilder config = DBConfigurationBuilder.newBuilder();
    // Port not set to allow new instances to be generated for batch of tests
    config.setSecurityDisabled(true);
    return config.build();
  }

  @Bean("testDatasource")
  public DataSource dataSource(
      @Autowired DBConfiguration dbConfig,
      @Value("${app.datasource.name}") String databaseName,
      @Value("${app.datasource.username}") String datasourceUsername,
      @Value("${app.datasource.username}") String datasourcePassword,
      @Value("${app.datasource.driver-class-name}") String datasourceDriver)
      throws ManagedProcessException {
    DB db = DB.newEmbeddedDB(dbConfig);
    db.start();
    db.createDB(databaseName, "root", "");
    db.source("db/init/schema.sql", databaseName);

    DBConfiguration dbConfiguration = db.getConfiguration();

    return DataSourceBuilder.create()
        .driverClassName(datasourceDriver)
        .url(dbConfiguration.getURL(databaseName))
        .username(datasourceUsername)
        .password(datasourcePassword)
        .build();
  }

  @Bean
  public JdbcTemplate jdbcTemplate(@Qualifier("testDatasource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Bean
  public NamedParameterJdbcTemplate NamedParameterJdbcTemplate(@Qualifier("testDatasource") DataSource dataSource) {
    return new NamedParameterJdbcTemplate(dataSource);
  }
}
