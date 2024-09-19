package booking.broker.model;

/**
 * Response of Administration API
 */
public class ClientDetails {
    private double discount;
    private String type;

    public ClientDetails(double discount, String type) {
        this.discount = discount;
        this.type = type;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
