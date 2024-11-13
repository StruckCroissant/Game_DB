package com.StruckCroissant.GameDB.core.game;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = GameSearchRequestValidator.class)
public @interface GameSearchRequestIsValid {
  String message() default "Search request must contain at least one search criteria";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
