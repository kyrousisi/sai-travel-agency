package booking.broker.model;

import javax.jms.Message;
import java.util.ArrayList;

/**
 * Aggregate : it is used when message is sent to only agency Easy_Ticket
 */
public class EasyBookingAggregate implements BookingAggregate {
    // List of Messages which are related to one aggregate
    ArrayList<Message> replies = new ArrayList<>();

    @Override
    public void addMessage(Message message) {
        replies.add(message);
    }

    @Override
    public boolean isComplete() {
        return replies.size() == 1;
    }

    @Override
    public Message getResultMessage() {
        return replies.get(0);
    }
}
