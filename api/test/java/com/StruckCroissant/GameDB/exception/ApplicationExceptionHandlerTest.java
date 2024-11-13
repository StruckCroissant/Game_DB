package com.StruckCroissant.GameDB.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.StruckCroissant.GameDB.core.game.GameController;
import com.StruckCroissant.GameDB.core.game.GameService;
import com.StruckCroissant.GameDB.core.user.UserDAOImpl;
import com.StruckCroissant.GameDB.core.web.advice.ApplicationExceptionHandler;
import config.TestDbConfig;
import java.lang.reflect.Field;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.FixedContentNegotiationStrategy;
import org.springframework.web.servlet.DispatcherServlet;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@ContextConfiguration(classes = {TestDbConfig.class, UserDAOImpl.class})
@SpringBootTest
public class ApplicationExceptionHandlerTest {
  private MockMvc mockMvc;

  @Mock GameService gameService;

  @Before
  public void setup() throws NoSuchFieldException, IllegalAccessException {
    this.setupMockMvcWithContentAndExampleController();
    this.setupMockMvcThrowExceptionIfHandlerNotFound();
  }

  private void setupMockMvcWithContentAndExampleController() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(new GameController(gameService))
            .setContentNegotiationManager(
                new ContentNegotiationManager(
                    Collections.singletonList(
                        new FixedContentNegotiationStrategy(MediaType.APPLICATION_JSON))))
            .setControllerAdvice(new ApplicationExceptionHandler())
            .build();
  }

  private void setupMockMvcThrowExceptionIfHandlerNotFound()
      throws NoSuchFieldException, IllegalAccessException {
    final Field field = MockMvc.class.getDeclaredField("servlet");
    field.setAccessible(true);
    final DispatcherServlet servlet = (DispatcherServlet) field.get(this.mockMvc);
    servlet.setThrowExceptionIfNoHandlerFound(true);
  }

  @Test
  public void whenHandlerNotFoundErrorIsCaught_thenReturnProblem() throws Exception {
    mockMvc
        .perform(get("/api/v1/test"))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("No route found for GET /api/v1/test"))
        .andExpect(header().string("Content-Type", "application/problem+json"));
  }
}
