package booking.broker.gateway;

import booking.broker.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class AgencyRequestMessageSenderGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgencyRequestMessageSenderGateway.class);
    private Session session = null;
    private MessageProducer producer = null;

    public AgencyRequestMessageSenderGateway(String queueName) {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Constants.ACTIVE_MQ_URL);
            Connection connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(queueName);
            producer = session.createProducer(queue);
        } catch (Exception e) {
            LOGGER.error("AgencyRequestMessageSenderGateway could not be initialised", e);
        }
    }

    private Message createMessage(String messageBody) throws JMSException {
        return session.createTextMessage(messageBody);
    }

    public void sendMessage(String messageBody, String correlationId, String aggregationName, String aggregationId) {
        try {
            Message message = createMessage(messageBody);
            message.setJMSCorrelationID(correlationId);
            message.setStringProperty("aggregationName", aggregationName);
            message.setStringProperty("aggregationId", aggregationId);
            producer.send(message);
        } catch (JMSException e) {
            LOGGER.error("Message could not be sent to Agency: " + e.getMessage());
        }
    }
}
