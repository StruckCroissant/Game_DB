package com.StruckCroissant.GameDB.core.game;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import javax.validation.ConstraintValidatorContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class GameSearchRequestValidatorTest {
  private final GameSearchRequestValidator searchRequestValidator =
      new GameSearchRequestValidator();

  @Mock ConstraintValidatorContext context;

  @Test
  public void whenGameSearchRequestIsValidWithAllParameters_ReturnTrue() {
    GameSearchRequest request = new GameSearchRequest("Rust", 1);
    assertThat(this.searchRequestValidator.isValid(request, context)).isTrue();
  }

  @Test
  public void whenGameSearchRequestIsInvalidWithNoParameters_ReturnFalse() {
    GameSearchRequest request = new GameSearchRequest(null, null);
    assertThat(this.searchRequestValidator.isValid(request, context)).isFalse();
  }
}
