package com.StruckCroissant.GameDB.core.game;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class GameSearchRequestValidator
    implements ConstraintValidator<GameSearchRequestIsValid, GameSearchRequest> {
  @Override
  public boolean isValid(GameSearchRequest request, ConstraintValidatorContext context) {
    return request.name() != null || request.id() != null;
  }
}
