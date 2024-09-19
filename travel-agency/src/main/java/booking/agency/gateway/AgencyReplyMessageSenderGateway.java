package booking.agency.gateway;

import booking.agency.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class AgencyReplyMessageSenderGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgencyReplyMessageSenderGateway.class);
    private Session session = null;
    private MessageProducer producer = null;

    public AgencyReplyMessageSenderGateway(String queueName) {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Constants.ACTIVE_MQ_URL);
            Connection connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(queueName);
            producer = session.createProducer(queue);
        } catch (Exception e) {
            LOGGER.error("AgencyReplyMessageSenderGateway could not be initialised", e);
        }
    }

    private Message createMessage(String messageBody) throws JMSException {
        return session.createTextMessage(messageBody);
    }

    public void sendMessage(String messageBody, String correlationId, String aggregationId, String aggregationName) {
        try {
            Message message = createMessage(messageBody);
            message.setJMSCorrelationID(correlationId);
            message.setStringProperty("aggregationId", aggregationId);
            message.setStringProperty("aggregationName", aggregationName);
            producer.send(message);
        } catch (JMSException e) {
            LOGGER.error("Message could not be sent: " + e.getMessage());
        }
    }
}
