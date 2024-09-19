package booking.broker.model;

import javax.jms.Message;

/**
 * Aggregate Interface for Booking
 * Implementation of this interface is required for each kind of required aggregate
 */
public interface BookingAggregate {

    void addMessage(Message message);

    boolean isComplete();

    Message getResultMessage();
}
