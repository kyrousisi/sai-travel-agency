package booking.broker.gateway;

import booking.agency.model.AgencyRequest;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgencyApplicationRecipientListGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgencyApplicationRecipientListGateway.class);
    private AgencyRequestMessageSenderGateway agencyRequestMessageSenderGateway = null;
    private final Gson gson = new Gson();

    public AgencyApplicationRecipientListGateway(String queueName) {
        try {
            agencyRequestMessageSenderGateway = new AgencyRequestMessageSenderGateway(queueName);
        } catch (Exception e) {
            LOGGER.error("AgencyApplicationRecipientListGateway could not initialized");
        }
    }

    public void getTicketPrice(AgencyRequest agencyRequest, String correlationId, String aggregationName, String aggregationId) {
        agencyRequestMessageSenderGateway.sendMessage(gson.toJson(agencyRequest), correlationId, aggregationName, aggregationId);
    }

}
