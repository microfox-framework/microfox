package ir.moke.microfox.mybatis;

import ir.moke.microfox.api.mybatis.MyBatisProvider;
import ir.moke.microfox.exception.MicrofoxException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class MyBatisProviderImpl implements MyBatisProvider {

    private static final Logger logger = LoggerFactory.getLogger(MyBatisProviderImpl.class);

    @Override
    public <T> void mybatis(String identity, Class<T> mapper, Consumer<T> consumer) {
        try (SqlSession sqlSession = MyBatisFactory.getSession(identity)) {
            T t = sqlSession.getMapper(mapper);
            consumer.accept(t);
        }
    }

    @Override
    public <T> void mybatisTx(String identity, Class<T> mapper, Consumer<T> consumer) {
        SqlSession sqlSession = MyBatisFactory.getSession(identity);
        try {
            T t = sqlSession.getMapper(mapper);
            consumer.accept(t);
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            throw new MicrofoxException(e);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public <T> void mybatisBatch(String identity, Class<T> mapper, Consumer<T> consumer) {
        SqlSession batchSession = MyBatisFactory.getBatchSession(identity);
        T t = batchSession.getMapper(mapper);
        try {
            consumer.accept(t);
            batchSession.commit();
        } catch (Exception e) {
            batchSession.rollback();
            throw new MicrofoxException(e);
        } finally {
            batchSession.close();
        }
    }
}
