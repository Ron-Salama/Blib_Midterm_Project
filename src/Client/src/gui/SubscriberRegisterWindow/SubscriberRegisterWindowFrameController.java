package gui.SubscriberRegisterWindow;

import java.net.URL;
import java.util.ResourceBundle;

import client.ClientUI;
import gui.baseController.BaseController;
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

public class SubscriberRegisterWindowFrameController extends BaseController implements Initializable {
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
    private Button btnClose = null;

    @FXML
    private Button btnUpdate = null;

    @FXML
    private Button btnReturnToMainMenu = null;
    
    @FXML
    private Button btnRegister = null;


    public void btnRegister(ActionEvent event) throws Exception {
        // Get the updated values from the text fields
        String id = txtSubscriber_id.getText();
        String phoneNumber = txtSubscriber_phoneNumber.getText();
        String email = txtSubscriber_email.getText();
        String name = txtSubscriber_name.getText();
        
        // Prepare the update message
        String obj = id + "," + name + "," + phoneNumber + "," + email;

        // Send the update message to the server
        ClientUI.chat.accept("RegisterRequest:"+obj);

    }
    
    public void btnReturnToMainMenu(ActionEvent event) {
    	openWindow(event,
    			"/gui/MainMenu/MainMenuFrame.fxml",
    			"/gui/MainMenu/MainMenuFrame.css",
    			"MainMenu");
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Initialization logic, if needed
    }
    
    
    
}
