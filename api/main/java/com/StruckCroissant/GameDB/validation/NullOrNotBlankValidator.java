package com.StruckCroissant.GameDB.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, Object> {
  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    boolean result = value == null;

    context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate());
    if (value instanceof String) {
      result = !((String) value).trim().isEmpty();
    }

    return result;
  }
}
