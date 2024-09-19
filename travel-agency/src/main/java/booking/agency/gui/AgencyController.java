package booking.agency.gui;

import booking.agency.gateway.BrokerApplicationGateway;
import booking.agency.model.AgencyReply;
import booking.agency.model.AgencyRequest;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class AgencyController implements Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgencyController.class);

    private String agencyName;

    @FXML
    public TextField tfPrice;

    @FXML
    public ListView<AgencyListViewLine> lvAgencyRequestReply;

    private BrokerApplicationGateway brokerApplicationGateway = null;


    public AgencyController(String queueName, String agencyName) {
        this.agencyName = agencyName;
        brokerApplicationGateway = new BrokerApplicationGateway(queueName) {
            @Override
            public void onAngencyRequestReceived(AgencyRequest agencyRequest, String correlationId, String aggregationId, String aggregationName) {
                Gson gson = new Gson();
                LOGGER.info("Received Agency request: " + gson.toJson(agencyRequest));
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lvAgencyRequestReply.getItems().add(new AgencyListViewLine(agencyRequest, correlationId, aggregationId, aggregationName));
                    }
                });
            }
        };
    }

    @FXML
    public void btnSendAgencyReplyClicked() {
        AgencyListViewLine listViewLine = lvAgencyRequestReply.getSelectionModel().getSelectedItem();
        if (listViewLine != null) {
            if (listViewLine.getReply() == null) {
                double price = Double.parseDouble(tfPrice.getText());
                String id = UUID.randomUUID().toString();
                AgencyReply reply = new AgencyReply(id, agencyName, price);

                listViewLine.setReply(reply);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lvAgencyRequestReply.refresh();
                    }
                });
                LOGGER.info("Sending Agency reply: " + reply);
                brokerApplicationGateway.sendAgencyReply(reply, listViewLine.getCorrelationId(), listViewLine.getAggregationId(),listViewLine.getAggregationName());
            } else {
                showErrorMessageDialog("You have already sent reply for this request.");
            }
        } else {
            showErrorMessageDialog("No request is selected.\nPlease select a request for which you want to send the reply.");
        }

    }

    private void showErrorMessageDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Travel Agency");
        alert.setHeaderText("Error occurred while sending price offer.");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
