package ir.moke.microfox.http.validation;

import jakarta.validation.*;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.Set;

public class MicroFoxValidator {

    public static final ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory();

    public static <T> void validate(T t) {
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(t);
        for (ConstraintViolation<T> violation : violations) {
            throw new ValidationException(violation.getMessage());
        }
    }
}
