package ir.moke.microfox.jms;

import ir.moke.microfox.api.jms.JmsConnectionInfo;
import ir.moke.microfox.exception.MicroFoxException;
import jakarta.jms.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class JmsFactory {
    private static final Logger logger = LoggerFactory.getLogger(JmsFactory.class);
    private static final Set<JmsConnectionInfo> INFO_LIST = new HashSet<>();

    static void register(String identity, ConnectionFactory connectionFactory, int concurrency) {
        JmsConnectionInfo connectionInfo = new JmsConnectionInfo();
        connectionInfo.setIdentity(identity);
        connectionInfo.setConnectionFactory(connectionFactory);
        connectionInfo.setConcurrency(concurrency);
        if (isExists(identity))
            throw new MicroFoxException("JMS connection factory with identity %s already registered".formatted(identity));

        INFO_LIST.add(connectionInfo);
        logger.info("Jms with identity {} registered", identity);
    }

    static void register(String identity, ConnectionFactory connectionFactory) {
        register(identity, connectionFactory, 1);
    }

    static boolean isExists(String identity) {
        return INFO_LIST.stream().anyMatch(item -> item.getIdentity().equals(identity));
    }

    static JmsConnectionInfo getConnectionInfo(String identity) {
        return INFO_LIST.stream()
                .filter(item -> item.getIdentity().equals(identity))
                .findFirst()
                .orElse(null);
    }

    static Set<JmsConnectionInfo> getInfoList() {
        return INFO_LIST;
    }
}
