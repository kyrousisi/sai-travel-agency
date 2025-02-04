package booking.agency.gui;

import booking.agency.model.AgencyReply;
import booking.agency.model.AgencyRequest;

import java.time.format.DateTimeFormatter;

class AgencyListViewLine {
    private AgencyRequest request;
    private AgencyReply reply;
    private String correlationId;
    private String aggregationId;
    private String aggregationName;

    public AgencyListViewLine(AgencyRequest request, String correlationId, String aggregationId, String aggregationName) {
        this.request = request;
        this.correlationId = correlationId;
        this.aggregationId = aggregationId;
        this.aggregationName = aggregationName;
        this.reply = null;
    }

    public AgencyRequest getRequest() {
        return request;
    }

    private void setRequest(AgencyRequest request) {
        this.request = request;
    }

    public AgencyReply getReply() {
        return reply;
    }

    public void setReply(AgencyReply reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        final String delimiter = "-";
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        String requestString = request.getFromAirport() + delimiter
                + request.getToAirport() + delimiter
                + request.getDate().format(formatters) + delimiter
                + request.getNrTravellers() + delimiter
                + ((request.isRegisteredClient() ? "registered" : "unregistered"));
        String replyString = "";
        if (reply == null) {
            replyString = "waiting...";
        } else {
            replyString = Double.toString(reply.getPrice());
        }
        return requestString + "  --->  " + replyString;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getAggregationId() {
        return aggregationId;
    }

    public void setAggregationId(String aggregationId) {
        this.aggregationId = aggregationId;
    }

    public String getAggregationName() {
        return aggregationName;
    }

    public void setAggregationName(String aggregationName) {
        this.aggregationName = aggregationName;
    }
}
