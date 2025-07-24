package ir.moke.microfox.api.mybatis;

import java.util.function.Consumer;

public interface MyBatisProvider {
    <T> T mybatis(String identity, Class<T> mapper);

    <T> void mybatisTx(String identity, Class<T> mapper, Consumer<T> consumer);

    <T> void mybatisBatch(String identity, Class<T> mapper, Consumer<T> consumer);
}
