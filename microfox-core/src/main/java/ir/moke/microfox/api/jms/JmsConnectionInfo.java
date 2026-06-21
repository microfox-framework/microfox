package ir.moke.microfox.api.jms;

import jakarta.jms.ConnectionFactory;

import java.util.Objects;

public class JmsConnectionInfo {
    private String identity;
    private ConnectionFactory connectionFactory;
    private int concurrency;

    public JmsConnectionInfo(ConnectionFactory connectionFactory, int concurrency) {
        this.connectionFactory = connectionFactory;
        this.concurrency = concurrency;
    }

    public JmsConnectionInfo(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        JmsConnectionInfo that = (JmsConnectionInfo) o;
        return Objects.equals(identity, that.identity);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identity);
    }
}
