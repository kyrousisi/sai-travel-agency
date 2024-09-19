package booking.broker;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class TravelBrokerMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final Logger logger = LoggerFactory.getLogger(getClass());
        BrokerService broker = BrokerFactory.createBroker(new URI("broker:(tcp://localhost:61616)"));
        broker.start();
        TravelBrokerController travelBrokerController = new TravelBrokerController();
    }
}
