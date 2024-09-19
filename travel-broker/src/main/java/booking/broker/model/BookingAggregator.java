package booking.broker.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Booking Aggregator which implements the MessageListener for recieving the messages form all the Agencies
 */
public abstract class BookingAggregator implements MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingAggregator.class);
    static final String PROP_AGGRGATIONID = "aggregationId";
    static final String PROP_AGGRGATION_NAME = "aggregationName";
    Map<String, BookingAggregate> activeAggregates = new HashMap<>();

    @Override
    public void onMessage(Message message) {
        try {
            String aggregationId = message.getStringProperty(PROP_AGGRGATIONID);
            LOGGER.info("Booking Aggregator Recieved Agency Reply: " + message + ", CorrelationId: " + message.getJMSCorrelationID() + ", AggregationId: " + aggregationId);
            // finds if Any Message for this AggrgataionId is already exist.
            BookingAggregate bookingAggregate = activeAggregates.get(aggregationId);
            // if it does not exist then create new Entry for aggregationId in the Map pf active aggregates
            if (bookingAggregate == null) {
                bookingAggregate = new BookingAggregateFactory().getAggregate(message.getStringProperty(PROP_AGGRGATION_NAME));
                activeAggregates.put(aggregationId, bookingAggregate);
            }
            // if aggregate is not complete then add the message in Map
            if (!bookingAggregate.isComplete()) {
                // after adding the message to Map, check again for completeness
                bookingAggregate.addMessage(message);
                // if it is now complete, then gets the best result out of all the agency replies and notifies the controller
                if (bookingAggregate.isComplete()) {
                    Message resultMessage = bookingAggregate.getResultMessage();
                    LOGGER.info("Best Offer: " + resultMessage + ", CorrelationId: " + message.getJMSCorrelationID() + ", AggregationId: " + aggregationId);
                    onAgencyReplyReceived(resultMessage);
                }
            }
        } catch (JMSException e) {
            LOGGER.error("Error receiving Message");
        }
    }

    public abstract void onAgencyReplyReceived(Message message);
}
