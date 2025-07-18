package ir.moke.microfox.api.elastic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ir.moke.microfox.api.elastic.ElasticFieldType.AUTO;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ElasticField {
    String name() default "";
    ElasticFieldType value() default AUTO;
}
