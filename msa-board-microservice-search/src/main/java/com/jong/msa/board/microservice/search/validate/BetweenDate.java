package com.jong.msa.board.microservice.search.validate;

import com.jong.msa.board.microservice.search.request.SearchRequest.DateRange;
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
@Constraint(validatedBy = BetweenDate.Validator.class)
public @interface BetweenDate {

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<BetweenDate, DateRange> {

        @Override
        public boolean isValid(DateRange value, ConstraintValidatorContext context) {
            return value == null
                || value.from() == null
                || value.to() == null
                || value.from().isBefore(value.to());
        }
    }

}
