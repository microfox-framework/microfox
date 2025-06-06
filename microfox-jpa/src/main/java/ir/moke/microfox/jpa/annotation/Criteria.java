package ir.moke.microfox.jpa.annotation;

import ir.moke.microfox.jpa.CriteriaProvider;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Criteria {
    Class<? extends CriteriaProvider<?>> provider();

    boolean ignoreNullValues() default false;
}
