package booking.broker.gateway;

import booking.agency.model.AgencyReply;
import booking.broker.Constants;
import booking.broker.model.BookingAggregator;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

public abstract class AgencyApplicationGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgencyApplicationGateway.class);
    private final Gson gson = new Gson();

    public AgencyApplicationGateway() {
        AgencyReplyMessageReceiverGateway agencyReplyMessageReceiverGateway = new AgencyReplyMessageReceiverGateway(Constants.AGENCY_REPLY_QUEUE);
        agencyReplyMessageReceiverGateway.setMessageListener(new BookingAggregator() {
            @Override
            public void onAgencyReplyReceived(Message message) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    AgencyApplicationGateway.this.onAgencyReplyReceived(gson.fromJson(textMessage.getText(), AgencyReply.class), message.getJMSCorrelationID());
                } catch (JMSException e) {
                    LOGGER.error("Error in receiving Message");
                }
            }
        });
    }

    public abstract void onAgencyReplyReceived(AgencyReply agencyReply, String correlationId);
}
