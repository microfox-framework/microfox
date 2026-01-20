import ir.moke.microfox.exception.MicroFoxException;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

public class CustomMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage tx) {
                System.out.println(tx.getText());
            }
        } catch (JMSException e) {
            throw new MicroFoxException(e);
        }
    }
}
