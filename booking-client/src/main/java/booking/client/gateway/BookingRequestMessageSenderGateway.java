package booking.client.gateway;

import booking.client.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class BookingRequestMessageSenderGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingRequestMessageSenderGateway.class);
    private Session session = null;
    private MessageProducer producer = null;

    public BookingRequestMessageSenderGateway(String queueName) {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Constants.ACTIVE_MQ_URL);
            Connection connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(queueName);
            producer = session.createProducer(queue);
        } catch (Exception e) {
            LOGGER.error("BookingRequestMessageSenderGateway could not be initialised", e);
        }
    }

    private Message createMessage(String messageBody) throws JMSException {
        return session.createTextMessage(messageBody);
    }

    public void sendMessage(String messageBody) {
        try {
            Message message = createMessage(messageBody);
            producer.send(message);
        } catch (JMSException e) {
            LOGGER.error("Message could not be sent: " + e.getMessage());
        }
    }
}
