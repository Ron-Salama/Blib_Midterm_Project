package gui.UpdateInfoWindow;

import java.net.URL;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import gui.SubscriberWindow.SubscriberWindowController;
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

public class UpdateInfoController extends BaseController implements Initializable {
    @FXML
    private Label lblSubscriber_id = null;
    @FXML
    private Label lblSubscriber_name = null;
    @FXML
    private Label lblSubscriber_phoneNumber = null;
    @FXML
    private Label lblSubscriber_email = null;
    
    @FXML
    private Label UpdateStatus = null;

    @FXML
    private TextField txtSubscriber_id = null;
    @FXML
    private TextField txtSubscriber_name = null;
    @FXML
    private TextField txtSubscriber_phoneNumber = null;
    @FXML
    private TextField txtSubscriber_email = null;

    @FXML
    private Button btnClose = null;

    @FXML
    private Button btnUpdate = null;

    @FXML
    private Button btnReturnToSubscriberForm = null;
    
    @FXML
    private Button btnRegister = null;
    
    Subscriber currentSub = SubscriberWindowController.currentSubscriber;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	txtSubscriber_id.setDisable(true);
    	txtSubscriber_name.setDisable(true);
    	
		 txtSubscriber_id.setText("" + currentSub.getSubscriber_id());
		 txtSubscriber_name.setText("" + currentSub.getSubscriber_name());
		 txtSubscriber_email.setText("" + currentSub.getSubscriber_email());
		 txtSubscriber_phoneNumber.setText("" + currentSub.getSubscriber_phone_number());
    	
    }

    
    
    
    public void btnUpdate(ActionEvent event) throws Exception {
        String phoneNumber = txtSubscriber_phoneNumber.getText();
        String email = txtSubscriber_email.getText();
        String id = txtSubscriber_id.getText();

        String obj = "" + id + "," + phoneNumber + "," + email;

        // Validation check.
        if (email.isEmpty() && phoneNumber.isEmpty()) {
        	showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(UpdateStatus, "Phone number and Email must be entered.", "-fx-text-fill: red;", 2);
        	return;
        } else if (email.isEmpty()) {
        	showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(UpdateStatus, "Email must be entered.", "-fx-text-fill: red;", 2);
        	return;
        } else if (phoneNumber.isEmpty()) {
        	showColoredLabelMessageOnGUIAndMakeItDisappearAfterDelay(UpdateStatus, "Phone number must be entered.", "-fx-text-fill: red;", 2);
        	return;
        }
        
        ClientUI.chat.accept("Update:" + obj);
        
        waitForServerResponse();
        
        if(ChatClient.alertIndicator) {
            // Feedback to the user
            showColoredLabelMessageOnGUI(UpdateStatus, "You have successfully updated your personal information.", "-fx-text-fill: green;");
        }else {
        	showColoredLabelMessageOnGUI(UpdateStatus, "Updating your personal information has failed.", "-fx-text-fill: red;");
        }
    }
    
    public void btnReturnToSubscriberForm(ActionEvent event) {
    	openWindow(event,
    			"/gui/SubscriberWindow/SubscriberWindow.fxml",
    			"/gui/SubscriberWindow/SubscriberWindow.css",
    			"Subscriber View");
    }
}
