package cs1302.api;

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

/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {
    Stage stage;
    Scene scene;
    VBox root;
    HBox topBox;
    Text searchText;
    TextField searchField;
    Button getEventsButton;
    EventHandler<ActionEvent> gEventsButton;
    private static final String DEFAULT_URL = "https://api.seatgeek.com/2/events?venue.city=";
    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox();
        topBox = new HBox(10);
        searchText = new Text("Search for events near a city");
        searchField = new TextField("Enter a city");
        getEventsButton = new Button("Get Events");
    } // ApiApp


    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        this.stage = stage;
        searchField.setPrefWidth(300);
        topBox.getChildren().addAll(searchText, searchField, getEventsButton);
        setButton();
        root.getChildren().addAll(topBox);
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
            System.out.println(newCity);
            String uri = DEFAULT_URL + newCity + "&client_id=MzM1MTU1MzJ8MTY4MzQyNzk1NS4zNDQ1MTA4";
            System.out.println(uri);

        };
        getEventsButton.setOnAction(gEventsButton);
    }
    public void seatGeek() {

    }
    public String apiKey(int a) {



    }


} // ApiApp
