package ir.moke.microfox.api.mybatis;

import java.util.function.Consumer;
import java.util.function.Function;

public interface MyBatisProvider {
    <T, R> R mybatis(String identity, Class<T> mapper, Function<T, R> function);

    <T> void mybatisTx(String identity, Class<T> mapper, Consumer<T> consumer);

    <T> void mybatisBatch(String identity, Class<T> mapper, Consumer<T> consumer);

}
