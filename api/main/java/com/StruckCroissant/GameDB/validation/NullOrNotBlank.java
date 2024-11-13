package com.StruckCroissant.GameDB.validation;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NullOrNotBlankValidator.class)
public @interface NullOrNotBlank {
  String message() default "Value must either be null, or a non-blank String";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
