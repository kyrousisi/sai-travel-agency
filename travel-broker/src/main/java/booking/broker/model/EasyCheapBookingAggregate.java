package booking.broker.model;

import booking.agency.model.AgencyReply;
import com.google.gson.Gson;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Aggregate : it is used when message is sent to two agencies Easy_Tickets and Cheap_Tickets
 */
public class EasyCheapBookingAggregate implements BookingAggregate {
    // List of Messages which are related to one aggregate
    ArrayList<Message> replies = new ArrayList<>();
    Gson gson = new Gson();

    @Override
    public void addMessage(Message message) {
        replies.add(message);
    }

    @Override
    public boolean isComplete() {
        return replies.size() == 2;
    }

    // gives back the best offer from all the agencies(here best offer is minimum price of ticket)
    @Override
    public Message getResultMessage() {
        ArrayList<AgencyReply> agencyReplies = new ArrayList<>();
        // converts message list to  AgencyReply List
        for (Message message : replies) {
            try {
                agencyReplies.add(gson.fromJson(((TextMessage) message).getText(), AgencyReply.class));
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        // finds out the messageId of Reply which has lowest price
        String messageId = agencyReplies.stream().min(Comparator.comparingDouble(AgencyReply::getPrice)).map(AgencyReply::getId).orElse(null);
        // gets the one message from List according to messageId computed above
        return replies.stream().filter(element -> {
                    try {
                        return ((TextMessage) element).getText().contains(messageId);
                    } catch (JMSException e) {
                        return false;
                    }
                }).
                findFirst().
                orElse(null);
    }
}
