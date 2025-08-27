package com.jong.msa.board.support.web.response;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.Builder;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@Builder
public record ErrorResponse(
    String path,
    String code,
    String message,
    List<ValidError> validErrors,
    LocalDateTime timestamp
) {

    @Builder
    public record ValidError(
        String field,
        String message
    ) {

        public static ValidError of(ObjectError error) {
            return ValidError.builder()
                .field(error instanceof FieldError fieldError
                    ? fieldError.getField()
                    : null)
                .message(error.getDefaultMessage())
                .build();
        }

        public static ValidError of(ConstraintViolation violation) {
            return ValidError.builder()
                .field(StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
                    .filter(node -> node.getKind() == ElementKind.PARAMETER
                        || node.getKind() == ElementKind.PROPERTY)
                    .map(Path.Node::getName)
                    .collect(Collectors.joining(".")))
                .message(violation.getMessage())
                .build();
        }
    }

}
