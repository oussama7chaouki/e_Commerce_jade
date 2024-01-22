package Ecom;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ClientContainer extends Application {

    private Client agentinterface;
    ObservableList<String> observableList = null;

    public static void main(String[] args) {
        launch(ClientContainer.class);
    }

    public void startContainer() {
        try {
            Runtime runtime = Runtime.instance();
            ProfileImpl profileImpl = new ProfileImpl();
            profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
            AgentContainer agentContainer = runtime.createAgentContainer(profileImpl);
            AgentController reservationAgentController = agentContainer.createNewAgent("client", Client.class.getName(), new Object[]{this});
            reservationAgentController.start();            
            System.out.println("Agents Container Started!");
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void start(Stage primaryStage) {
        startContainer();
        primaryStage.setTitle("Client Interface");

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #333333;");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20, 20, 20, 20));

        HBox hbox1 = createField("Produit :", "textpay");
        HBox hbox2 = createField("Quantite :", "textville");
        HBox hbox3 = createField("Delai :", "textdelai");

        HBox hbox7 = new HBox(10);
        hbox7.setPadding(new Insets(10, 0, 0, 20));
        Button btn = new Button("Envoyer");
        btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        hbox7.getChildren().add(btn);

        GridPane gridPane = new GridPane();
        observableList = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<String>(observableList);
        gridPane.add(listView, 0, 0);
        gridPane.setPadding(new Insets(10, 0, 10, 0));

        vbox.getChildren().addAll(hbox1, hbox2, hbox3, hbox7, gridPane);
        vbox.setStyle("-fx-background-color: #444444; -fx-border-color: #555555; -fx-border-width: 2px;");
        borderPane.setCenter(vbox);

        Scene scene = new Scene(borderPane, 400, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        btn.setOnAction(event -> {
            // Handle button click
            String pay = getTextFieldText(hbox1);
            String ville = getTextFieldText(hbox2);
            String delai = getTextFieldText(hbox3);

            // Check if any field is empty
            if (pay.isEmpty() || ville.isEmpty() || delai.isEmpty()) {
                // Show a warning alert
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Please fill in all fields before proceeding!");
                alert.showAndWait();
            } else {
                // Process the input or send it to the agent
                System.out.println("Product: " + pay);
                System.out.println("Quantity: " + ville);
                System.out.println("Delay: " + delai);

                GuiEvent guiEvent = new GuiEvent(this, 1);
                guiEvent.addParameter(pay);
                guiEvent.addParameter(ville);
                guiEvent.addParameter(delai);

                agentinterface.onGuiEvent(guiEvent);
            }
        });
    }
 

    public Client getAgentinterface() {
        return agentinterface;
    }

    public void setAgentinterface(Client agentinterface) {
        this.agentinterface = agentinterface;
    }
    
    public void show(String message) {
        this.observableList.add(0, message);
        
    }
    private HBox createField(String labelText, String textFieldId) {
        HBox hbox = new HBox(10);
        hbox.setPadding(new Insets(0, 0, 10, 0));

        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #ffffff;");

        TextField textField = new TextField();
        textField.setId(textFieldId);

        hbox.getChildren().addAll(label, textField);
        return hbox;
    }

    private String getTextFieldText(HBox hbox) {
        TextField textField = (TextField) hbox.lookup("#" + hbox.getChildren().get(1).getId());
        return textField.getText();
    }
}
