package ir.moke.microfox.http.validation;

import jakarta.validation.MessageInterpolator;

import java.util.Locale;

public class FixedLocaleMessageInterpolator implements MessageInterpolator {
    private final MessageInterpolator delegate;
    private final Locale locale;

    public FixedLocaleMessageInterpolator(MessageInterpolator delegate, Locale locale) {
        this.delegate = delegate;
        this.locale = locale;
    }

    @Override
    public String interpolate(String messageTemplate, Context context) {
        return delegate.interpolate(messageTemplate, context, locale);
    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale ignored) {
        return delegate.interpolate(messageTemplate, context, locale);
    }
}
