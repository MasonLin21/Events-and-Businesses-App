package cs1302.api;

import javafx.scene.text.Font;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.http.HttpClient;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.control.Button;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;

/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {
    Stage stage;
    Scene scene;
    VBox root;
    HBox topBox;
    HBox eventInfoBox;
    HBox nearRestBox;
    Text searchText;
    TextField searchField;
    Button getEventsButton;
    Text nearRestText;
    EventHandler<ActionEvent> gEventsButton;
    private static final String DEFAULT_URL = "https://api.seatgeek.com/2/events?venue.city=";
    private static final String YELP_URL = "https://api.yelp.com/v3/businesses/search?";
//    private static final String YELP_URL = "https://api.yelp.com/v3/businesses/search?latitude=37.786882&longitude=-122.399972" +
    //     "&categories=restaurants";

    Text eventInfo;



    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)           // uses HTTP protocol version 2 where possible
        .followRedirects(HttpClient.Redirect.NORMAL)  // always redirects, except from HTTPS to HTTP
        .build();                                     // builds and returns a HttpClient object

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()                          // enable nice output when printing
        .create();                                    // builds and returns a Gson object



    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox(10);
        topBox = new HBox(10);
        searchText = new Text("Search for events near a city");
        searchField = new TextField("Enter a city");
        getEventsButton = new Button("Get Events");
        eventInfo = new Text("Events will appear here");
        eventInfoBox = new HBox(10);
        nearRestBox = new HBox(10);
        nearRestText = new Text("Nearby businesses and restaurants");
    } // ApiApp


    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        this.stage = stage;
        root.setPrefSize(600, 300);
        searchField.setPrefWidth(300);
        topBox.getChildren().addAll(searchText, searchField, getEventsButton);
        setButton();
//        eventInfo.setFont(new Font(24));
        eventInfoBox.getChildren().addAll(eventInfo);
        nearRestBox.getChildren().addAll(nearRestText);

        root.getChildren().addAll(topBox, eventInfoBox, nearRestBox);
        scene = new Scene(root);

        // setup stage
        stage.setTitle("ApiApp!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

    } // start

    public void setButton() {
        gEventsButton = (ActionEvent e) -> {
            String city =  searchField.getText();
            String newCity = URLEncoder.encode(city, StandardCharsets.UTF_8);

            String uri = DEFAULT_URL + newCity + "&client_id=" + apiKey(0);
            seatGeek(uri);


        };
        getEventsButton.setOnAction(gEventsButton);
    }
    public void seatGeek(String uri) {
        try {
            URI link = URI.create(uri);
            HttpRequest request = HttpRequest.newBuilder().uri(link).build();
            HttpResponse<String> response =  HTTP_CLIENT.send(request,BodyHandlers.ofString());
            String responseBody = response.body();
            SeatGeekResponse sgResponse = GSON
                .<SeatGeekResponse>fromJson(responseBody, SeatGeekResponse.class);
            String eventStuff = "Top Event: " + sgResponse.events[0].venue.name +
                "\n" + "Url: " + sgResponse.events[0].venue.url;
            eventInfo.setText(eventStuff);


            String query = String.format("latitude=%s&longitude=%s",
                sgResponse.events[0].venue.location.lat
                ,sgResponse.events[0].venue.location.lon);
            String fullQuery = YELP_URL + query +  "&categories=restaurants";

            yelp(fullQuery);
        } catch (Exception e) {
            eventInfo.setText("Not valid city. Try Again");
            nearRestText.setText("");
        }

    }
    public void yelp(String uri) {
        try {
            URI link = URI.create(uri);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(link)
                .header("Authorization", "Bearer " + apiKey(1))
                .build();
            HttpResponse<String> response =  HTTP_CLIENT.send(request, BodyHandlers.ofString());
            String responseBody = response.body();
            YelpResponse yelpResponse = GSON
                .<YelpResponse>fromJson(responseBody, YelpResponse.class);
            String nearRestString = "Here is a nearby business/restaurant close to your event" +
                "\n Business/resturant: " + yelpResponse.businesses[0].name +
                "\n URL: " + yelpResponse.businesses[0].url;
            nearRestText.setText(nearRestString);
        } catch (Exception e) {
            nearRestText.setText("No nearby restaurants");
        }

    }
    public String apiKey(int a) {

        String configPath = "resources/config.properties";

        // the following try-statement is called a try-with-resources statement
        // see https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
        try (FileInputStream configFileStream = new FileInputStream(configPath)) {
            Properties config = new Properties();
            config.load(configFileStream);

            if (a == 0) {
                String apiKey = config.getProperty("ClientId");
                return apiKey;
            } else if (a == 1) {
                String apiKey = config.getProperty("ApiKeyYelp");
                return apiKey;
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
            ioe.printStackTrace();
        } // try
        return null;


    }


} // ApiApp
