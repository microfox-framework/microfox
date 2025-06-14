import com.ibm.msg.client.jakarta.jms.JmsConnectionFactory;
import com.ibm.msg.client.jakarta.jms.JmsFactoryFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import ir.moke.microfox.exception.MicrofoxException;
import ir.moke.microfox.jms.JmsFactory;
import jakarta.jms.Session;

import static ir.moke.microfox.MicroFox.consumeQueue;

public class MainClass {
    private static final String IDENTITY = "ibm-mq";
    private static final String APPLICATION_NAME = "microfox-ibm-mq";
    private static final String CHANNEL_NAME = "DEV.APP.SVRCONN";
    private static final String QUEUE_MANAGER = "QM1";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 1414;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "adminpass";
    private static final String CONNECTION_TTL = "3600";
    private static final String QUEUE_NAME = "test";

    public static void main(String[] args) {
        registerIBMConnectionFactory();
        consumeQueue(IDENTITY, QUEUE_NAME, Session.AUTO_ACKNOWLEDGE, new CustomMessageListener());
    }

    public static void registerIBMConnectionFactory() {
        try {
            JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.JAKARTA_WMQ_PROVIDER);
            JmsConnectionFactory connectionFactory = ff.createConnectionFactory();

            // Set connection properties
            connectionFactory.setStringProperty(WMQConstants.WMQ_HOST_NAME, HOST);
            connectionFactory.setIntProperty(WMQConstants.WMQ_PORT, PORT);
            connectionFactory.setStringProperty(WMQConstants.WMQ_CHANNEL, CHANNEL_NAME);
            connectionFactory.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, QUEUE_MANAGER);
            connectionFactory.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, APPLICATION_NAME);
            connectionFactory.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
            connectionFactory.setStringProperty(WMQConstants.USERID, USERNAME);
            connectionFactory.setStringProperty(WMQConstants.PASSWORD, PASSWORD);
            connectionFactory.setStringProperty(WMQConstants.TIME_TO_LIVE, CONNECTION_TTL);
            JmsFactory.registerConnectionFactory(IDENTITY, connectionFactory);
        } catch (Exception e) {
            throw new MicrofoxException(e);
        }
    }
}
