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
    public <T> T mybatis(String identity, Class<T> mapper) {
        SqlSession sqlSession = MyBatisFactory.getSession(identity);
        return sqlSession.getMapper(mapper);
    }

    @Override
    public <T> void mybatisTx(String identity, Class<T> mapper, Consumer<T> consumer) {
        SqlSession sqlSession = MyBatisFactory.getSession(identity);
        T t = sqlSession.getMapper(mapper);
        try {
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
        SqlSession sqlSession = MyBatisFactory.getSession(identity);
        T t = sqlSession.getMapper(mapper);
        try {
            consumer.accept(t);
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            throw new MicrofoxException(e);
        } finally {
            sqlSession.close();
        }
    }
}
