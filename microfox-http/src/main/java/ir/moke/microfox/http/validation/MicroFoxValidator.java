package ir.moke.microfox.http.validation;

import jakarta.validation.*;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.Set;

public class MicroFoxValidator {

    public static <T> void validate(T t) {
        try (ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<T>> violations = validator.validate(t);
            for (ConstraintViolation<T> violation : violations) {
                throw new ValidationException(violation.getMessage());
            }
        }
    }
}
