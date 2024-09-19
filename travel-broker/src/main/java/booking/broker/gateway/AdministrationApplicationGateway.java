package booking.broker.gateway;

import booking.broker.Constants;
import booking.broker.model.ClientDetails;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdministrationApplicationGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdministrationApplicationGateway.class);
    private final Gson gson = new Gson();

    public double getClientDiscount(int clientId) {
        double discount = 0;
        try {
            URL url = new URL(Constants.ADMINISTRATION_API_URL + clientId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                discount = getDiscount(output);
            }
            conn.disconnect();
        } catch (IOException e) {
            LOGGER.error("Administration Service could not be called", e);
        }
        return discount;
    }

    private double getDiscount(String response) {
        if (response != null) {
            ClientDetails clientDetails = gson.fromJson(response, ClientDetails.class);
            return clientDetails.getDiscount();
        }
        return 0;
    }
}
