package booking.client.gateway;

import booking.client.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class BookingReplyMessageReceiverGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingReplyMessageReceiverGateway.class);
    private MessageConsumer consumer = null;
    private Connection connection = null;

    public BookingReplyMessageReceiverGateway(String queueName) {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Constants.ACTIVE_MQ_URL);
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            consumer = session.createConsumer(destination);
            connection.start();
            LOGGER.info("BookingReplyMessageReceiverGateway started");
        } catch (Exception e) {
            LOGGER.error("BookingReplyMessageReceiverGateway could not be initialised");
        }
    }

    public void setMessageListener(MessageListener messageListener) {
        try {
            consumer.setMessageListener(messageListener);
            connection.start();
        } catch (JMSException e) {
            LOGGER.error("Connection for BookingReplyMessageReceiverGateway could not be started");
        }
    }
}
