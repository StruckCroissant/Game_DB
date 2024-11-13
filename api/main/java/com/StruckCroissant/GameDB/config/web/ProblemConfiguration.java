package com.StruckCroissant.GameDB.config.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.validation.ConstraintViolationProblemModule;

@Configuration
public class ProblemConfiguration implements InitializingBean {
  private final ObjectMapper objectMapper;

  public ProblemConfiguration(@Autowired ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void afterPropertiesSet() {
    objectMapper.registerModules(
        ProblemConfiguration.getProblemMapper(), new ConstraintViolationProblemModule());
  }

  private static ProblemModule getProblemMapper() {
    return new ProblemModule().withStackTraces(false);
  }
}
