package gui.SubscriberRegisterWindow;

import java.net.URL;
import java.util.ResourceBundle;

import client.ClientUI;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.Subscriber;

public class SubscriberRegisterWindowController implements Initializable {
    private Subscriber s;
    
    @FXML
    private Label lblSubscriber_id;
    @FXML
    private Label lblSubscriber_name;
    @FXML
    private Label lblSubscriber_phoneNumber;
    @FXML
    private Label lblSubscriber_email;

    @FXML
    private TextField txtSubscriber_id;
    @FXML
    private TextField txtSubscriber_name;
    @FXML
    private TextField txtSubscriber_phoneNumber;
    @FXML
    private TextField txtSubscriber_email;

    @FXML
    private Button btnReturnToMainMenu = null;

    @FXML
    private Button btnRegister = null;

    ObservableList<String> list;

    public void loadSubscriber(Subscriber s1) {
        this.s = s1;
        this.txtSubscriber_id.setText(String.valueOf(s.getSubscriber_id())); // Convert int to String
        this.txtSubscriber_name.setText(s.getSubscriber_name()); // Assuming this is already a String
        this.txtSubscriber_phoneNumber.setText(s.getSubscriber_phone_number()); // Assuming this is already a String
        this.txtSubscriber_email.setText(s.getSubscriber_email()); // Assuming this is already a String
    }

    public void getbtnReturnToMainMenu(ActionEvent event) throws Exception {
        returnToMainMenu(event);
    }

    public void btnRegister(ActionEvent event) throws Exception {
        // Get the updated values from the text fields
        String id = txtSubscriber_id.getText();
        String phoneNumber = txtSubscriber_phoneNumber.getText();
        String email = txtSubscriber_email.getText();
        String name = txtSubscriber_name.getText();
        
        // Prepare the update message
        String obj = "Register" + id + "," + name + "," + phoneNumber + "," + email;

        // Send the update message to the server
        ClientUI.chat.accept(obj);

        // Navigate back to the LibraryFrame
        returnToMainMenu(event);
    }

    private void returnToMainMenu(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainMenu/MainMenuFrame.fxml"));
        Parent root = loader.load();

        // Set up the scene and stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/MainMenu/MainMenuFrame.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Library Management Tool");
        stage.show();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Initialization logic, if needed
    }
}
