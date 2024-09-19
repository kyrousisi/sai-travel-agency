package booking.broker.model;

/**
 * This is the Factory class which creates the correct Aggregate class according the given aggregation name
 */
public class BookingAggregateFactory {
    public BookingAggregate getAggregate(String aggregationName) {
        if (aggregationName == null) {
            return null;
        } else if (aggregationName.equals("easy")) {
            return new EasyBookingAggregate();
        } else if (aggregationName.equals("easyCheap")) {
            return new EasyCheapBookingAggregate();
        } else if (aggregationName.equals("easyBusiness")) {
            return new EasyBusinessBookingAggregate();
        } else if (aggregationName.equals("easyCheapBusiness")) {
            return new EasyCheapBusinessBookingAggregate();
        } else {
            return null;
        }
    }
}
