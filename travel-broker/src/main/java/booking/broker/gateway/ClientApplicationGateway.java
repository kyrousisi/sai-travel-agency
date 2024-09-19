package booking.broker.gateway;

import booking.client.model.ClientBookingReply;
import booking.client.model.ClientBookingRequest;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public abstract class ClientApplicationGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplicationGateway.class);
    private BookingReplyMessageSenderGateway bookingReplyMessageSenderGateway;
    private final Gson gson = new Gson();

    public ClientApplicationGateway() {
        try {
            BookingRequestMessageReceiverGateway bookingRequestMessageReceiverGateway = new BookingRequestMessageReceiverGateway("bookingRequestQueue");
            bookingRequestMessageReceiverGateway.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        String msg = textMessage.getText();
                        ClientBookingRequest clientBookingRequest = gson.fromJson(msg, ClientBookingRequest.class);
                        onBookingClientRequestReceived(clientBookingRequest);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.error("ClientApplicationGateway could not initialised");
        }
        bookingReplyMessageSenderGateway = new BookingReplyMessageSenderGateway("bookingReplyQueue");
    }

    public abstract void onBookingClientRequestReceived(ClientBookingRequest clientBookingRequest);

    public void sendBookingReply(ClientBookingReply clientBookingReply, String correlationId) {
        bookingReplyMessageSenderGateway.sendMessage(gson.toJson(clientBookingReply), correlationId);
    }
}
