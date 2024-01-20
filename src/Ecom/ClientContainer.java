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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
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
    public void start(Stage arg0) throws Exception {
        startContainer();
        arg0.setTitle("Agent Interface");
        BorderPane borderpane = new BorderPane();
        VBox vbox = new VBox();
        HBox hbox1 = new HBox();
        hbox1.setPadding(new Insets(10, 0, 10, 30));
        hbox1.setSpacing(105);
        javafx.scene.control.Label labelpay = new javafx.scene.control.Label("Produit :");
        javafx.scene.control.TextField textpay = new javafx.scene.control.TextField();
        hbox1.getChildren().add(labelpay);
        hbox1.getChildren().add(textpay);
        HBox hbox2 = new HBox();
        hbox2.setPadding(new Insets(0, 0, 10, 30));
        hbox2.setSpacing(100);
        javafx.scene.control.Label labelville = new javafx.scene.control.Label("Quantite :");
        javafx.scene.control.TextField textville = new javafx.scene.control.TextField();
        hbox2.getChildren().add(labelville);
        hbox2.getChildren().add(textville);
        HBox hbox3 = new HBox();
        hbox3.setPadding(new Insets(0, 0, 10, 30));
        hbox3.setSpacing(100);
        javafx.scene.control.Label labelchambre = new javafx.scene.control.Label("Delai :");
        javafx.scene.control.TextField textdelai = new javafx.scene.control.TextField();

        hbox3.getChildren().add(labelchambre);
        hbox3.getChildren().add(textdelai);
        HBox hbox7 = new HBox();
        hbox7.setPadding(new Insets(0, 0, 0, 160));
        hbox7.setSpacing(40);
        Button btn = new Button("Envoyer");
        hbox7.getChildren().add(btn);
        GridPane gridPane = new GridPane();
        observableList = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<String>(observableList);
        gridPane.add(listView, 0, 0);
        gridPane.setPadding(new Insets(10, 0, 10, 80));
        vbox.getChildren().addAll(hbox1, hbox2, hbox3, hbox7, gridPane);
        borderpane.setCenter(vbox);
        Scene scene = new Scene(borderpane, 400, 600);
        arg0.setScene(scene);
        arg0.show();

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                String pay = textpay.getText();
                String ville = textville.getText();
                String chambre = textdelai.getText();
                GuiEvent guiEvent = new GuiEvent(this, 1);
                guiEvent.addParameter(pay);
                guiEvent.addParameter(ville);
                guiEvent.addParameter(chambre);
                System.out.println("999999999999999999999999999999999999999");

                System.out.println(pay);
                System.out.println(ville);
                System.out.println(chambre);

                System.out.println("999999999999999999999999999999999999999");

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
}
