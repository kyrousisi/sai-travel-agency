package booking.broker;

import booking.agency.model.AgencyReply;
import booking.agency.model.AgencyRequest;
import booking.broker.gateway.AdministrationApplicationGateway;
import booking.broker.gateway.AgencyApplicationGateway;
import booking.broker.gateway.AgencyApplicationRecipientListGateway;
import booking.broker.gateway.ClientApplicationGateway;
import booking.client.model.ClientBookingReply;
import booking.client.model.ClientBookingRequest;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TravelBrokerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TravelBrokerController.class);
    private final ClientApplicationGateway clientApplicationGateway;
    private final Map<String, ClientBookingRequest> clientBookingRequestMap = new HashMap<>();
    private final AdministrationApplicationGateway administrationApplicationGateway = new AdministrationApplicationGateway();
    private String aggragationName = "easy";
    Gson gson = new Gson();

    public TravelBrokerController() {
        AgencyApplicationGateway agencyApplicationGateway = new AgencyApplicationGateway() {
            @Override
            public void onAgencyReplyReceived(AgencyReply agencyReply, String correlationId) {
                LOGGER.info("Received Agency reply: " + gson.toJson(agencyReply) + ", CorrelationId: " + correlationId);
                ClientBookingRequest clientBookingRequest = clientBookingRequestMap.get(correlationId);
                double discount = 0;
                if (clientBookingRequest.getClientID() != 0) {
                    discount = administrationApplicationGateway.getClientDiscount(clientBookingRequest.getClientID());
                }
                ClientBookingReply clientBookingReply = createClientBookingReply(agencyReply, discount, clientBookingRequest.getNumberOfTravellers());
                LOGGER.info("Sending Booking reply: " + gson.toJson(clientBookingReply) + " correlationId: " + correlationId);
                clientApplicationGateway.sendBookingReply(clientBookingReply, correlationId);
            }
        };
        clientApplicationGateway = new ClientApplicationGateway() {
            @Override
            public void onBookingClientRequestReceived(ClientBookingRequest clientBookingRequest) {
                LOGGER.info("Received Booking request in TravelBrokerController: " + gson.toJson(clientBookingRequest));
                clientBookingRequestMap.put(clientBookingRequest.getId(), clientBookingRequest);
                List<AgencyApplicationRecipientListGateway> recipientList = getRecipientList(clientBookingRequest);
                String aggregationId = UUID.randomUUID().toString();
                for (AgencyApplicationRecipientListGateway gateway : recipientList) {
                    AgencyRequest agencyRequest = createAgencyRequest(clientBookingRequest);
                    LOGGER.info("Sending Agency request: " + agencyRequest + ", CorrelationId: " + clientBookingRequest.getId() + ", AggregationId: " + aggregationId);
                    gateway.getTicketPrice(agencyRequest, clientBookingRequest.getId(), aggragationName, aggregationId);
                }
            }
        };
    }

    private AgencyRequest createAgencyRequest(ClientBookingRequest clientBookingRequest) {
        AgencyRequest agencyRequest = new AgencyRequest();
        agencyRequest.setId(UUID.randomUUID().toString());
        agencyRequest.setDate(clientBookingRequest.getDate());
        agencyRequest.setNrTravellers(clientBookingRequest.getNumberOfTravellers());
        agencyRequest.setFromAirport(clientBookingRequest.getOriginAirport());
        agencyRequest.setToAirport(clientBookingRequest.getDestinationAirport());
        agencyRequest.setRegisteredClient(clientBookingRequest.getClientID() != 0);
        return agencyRequest;
    }

    private ClientBookingReply createClientBookingReply(AgencyReply agencyReply, double discount, int nrOfTravellers) {
        ClientBookingReply clientBookingReply = new ClientBookingReply();
        clientBookingReply.setId(agencyReply.getId());
        clientBookingReply.setAgencyName(agencyReply.getName());
        double discountPercent = (100 - discount) / 100;
        clientBookingReply.setTotalPrice(agencyReply.getPrice() * discountPercent * nrOfTravellers);
        return clientBookingReply;
    }

    private List<AgencyApplicationRecipientListGateway> getRecipientList(ClientBookingRequest clientBookingRequest) {
        List<AgencyApplicationRecipientListGateway> recipientList = new ArrayList<>();
        recipientList.add(new AgencyApplicationRecipientListGateway(Constants.EASY_TICKETS_AGENCY_REQUEST_QUEUE));
        aggragationName = "easy";
        if (clientBookingRequest.getNumberOfTravellers() > 2) {
            recipientList.add(new AgencyApplicationRecipientListGateway(Constants.CHEAP_TICKETS_AGENCY_REQUEST_QUEUE));
            aggragationName = aggragationName + "Cheap";
        }
        if (clientBookingRequest.getClientID() != 0) {
            recipientList.add(new AgencyApplicationRecipientListGateway(Constants.BUSINESS_TICKETS_AGENCY_REQUEST_QUEUE));
            aggragationName = aggragationName + "Business";
        }
        return recipientList;
    }
}
