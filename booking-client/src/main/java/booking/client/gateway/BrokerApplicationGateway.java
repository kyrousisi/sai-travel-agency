package booking.client.gateway;

import booking.client.model.ClientBookingReply;
import booking.client.model.ClientBookingRequest;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public abstract class BrokerApplicationGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerApplicationGateway.class);
    private BookingRequestMessageSenderGateway bookingRequestMessageSenderGateway = null;
    private BookingReplyMessageReceiverGateway bookingReplyMessageReceiverGateway = null;
    private final Gson gson = new Gson();

    public BrokerApplicationGateway() {
        try {
            bookingRequestMessageSenderGateway = new BookingRequestMessageSenderGateway("bookingRequestQueue");
        } catch (Exception e) {
            LOGGER.error("BookingRequestMessageSenderGateway could not be initialised");
        }
        bookingReplyMessageReceiverGateway = new BookingReplyMessageReceiverGateway("bookingReplyQueue");
        bookingReplyMessageReceiverGateway.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    String msg = textMessage.getText();
                    ClientBookingReply clientBookingReply = gson.fromJson(msg, ClientBookingReply.class);
                    onBookingReplyReceived(clientBookingReply, message.getJMSCorrelationID());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getTicketPrice(ClientBookingRequest clientBookingRequest) {
        Gson gson = new Gson();
        bookingRequestMessageSenderGateway.sendMessage(gson.toJson(clientBookingRequest));
    }

    public abstract void onBookingReplyReceived(ClientBookingReply clientBookingReply, String correlationId);
}
