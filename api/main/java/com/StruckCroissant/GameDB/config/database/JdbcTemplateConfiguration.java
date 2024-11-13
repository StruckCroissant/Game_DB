package com.StruckCroissant.GameDB.config.database;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
@Profile({"!test"})
public class JdbcTemplateConfiguration {
  @Bean
  @Autowired
  public NamedParameterJdbcTemplate NamedParameterJdbcTemplate(HikariDataSource dataSource) {
    return new NamedParameterJdbcTemplate(dataSource);
  }

  @Bean
  @Autowired
  public JdbcTemplate JdbcTemplate(HikariDataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
