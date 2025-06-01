package ir.moke.microfox.validation;

import ir.moke.microfox.MicroFoxConfig;
import jakarta.validation.*;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;

import java.util.Locale;
import java.util.Set;

public class MicroFoxValidator {

    public static <T> void validate(T t, Locale locale) {
        MessageInterpolator messageInterpolator = getMessageInterpolator(locale);
        try (ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(messageInterpolator)
                .buildValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<T>> violations = validator.validate(t);
            for (ConstraintViolation<T> violation : violations) {
                throw new ValidationException(violation.getMessage());
            }
        }
    }

    private static MessageInterpolator getMessageInterpolator(Locale locale) {
        PlatformResourceBundleLocator resourceBundleLocator = new PlatformResourceBundleLocator(MicroFoxConfig.MICROFOX_RESOURCE_BUNDLE_NAME, Set.of(locale));
        ResourceBundleMessageInterpolator messageInterpolator = new ResourceBundleMessageInterpolator(resourceBundleLocator, true);
        return new FixedLocaleMessageInterpolator(messageInterpolator, locale);
    }
}
