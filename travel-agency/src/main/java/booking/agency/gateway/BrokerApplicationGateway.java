package booking.agency.gateway;

import booking.agency.model.AgencyReply;
import booking.agency.model.AgencyRequest;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public abstract class BrokerApplicationGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerApplicationGateway.class);
    private AgencyRequestMessageReceiverGateway agencyRequestMessageReceiverGateway = null;
    private AgencyReplyMessageSenderGateway agencyReplyMessageSenderGateway = null;
    private final Gson gson = new Gson();

    public BrokerApplicationGateway(String queueName) {
        try {
            agencyRequestMessageReceiverGateway = new AgencyRequestMessageReceiverGateway(queueName);
            agencyRequestMessageReceiverGateway.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        String msg = textMessage.getText();
                        AgencyRequest agencyRequest = gson.fromJson(msg, AgencyRequest.class);
                        onAngencyRequestReceived(agencyRequest, message.getJMSCorrelationID(), message.getStringProperty("aggregationId"), message.getStringProperty("aggregationName"));
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            LOGGER.error("BrokerApplicationGateway could not initialised, AgencyRequestMessageReceiverGateway could not be created");
        }
        try {
            agencyReplyMessageSenderGateway = new AgencyReplyMessageSenderGateway("agencyReplyQueue");
        } catch (Exception e) {
            LOGGER.error("BrokerApplicationGateway could not initialised, AgencyReplyMessageSenderGateway could not be created");
        }
    }

    public abstract void onAngencyRequestReceived(AgencyRequest agencyRequest, String correlationId, String aggregationId, String aggregationName);

    public void sendAgencyReply(AgencyReply agencyReply, String correlationId, String aggregationId, String aggregationName) {
        agencyReplyMessageSenderGateway.sendMessage(gson.toJson(agencyReply), correlationId, aggregationId, aggregationName);
    }
}
