package com.jong.msa.board.support.web.validate;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Constraint(validatedBy = NullableNotBlank.Validator.class)
public @interface NullableNotBlank {

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<NullableNotBlank, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return value == null || !value.isBlank();
        }
    }

}
