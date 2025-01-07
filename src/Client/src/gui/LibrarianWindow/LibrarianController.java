package gui.LibrarianWindow;

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

public class LibrarianController implements Initializable {
    private LibrarianController l;

    @FXML
    private Label lblLibrarian_id;
    @FXML
    private Label lblLibrarian_name;
    @FXML
    private Label lblLibrarian_phoneNumber;
    @FXML
    private Label lblLibrarian_email;
    @FXML
    private TextField txtLibrarian_id;
    @FXML
    private TextField txtLibrarian_name;
    @FXML
    private TextField txtLibrarian_phoneNumber;
    @FXML
    private TextField txtLibrarian_email;
    @FXML
    private TextField txtSubscriber_detailedSubscriptionHistory;

    @FXML
    private Button btnClose = null;

    @FXML
    private Button btnUpdate = null;

    ObservableList<String> list;

    public void loadSubscriber(Librarian l1) {
        this.l = l1;
        this.txtLibrarian_id.setText(String.valueOf(s.getSubscriber_id())); // Convert int to String
        this.txtLibrarian_name.setText(s.getSubscriber_name()); // Assuming this is already a String
        this.txtLibrarian_phoneNumber.setText(s.getSubscriber_phone_number()); // Assuming this is already a String
        this.txtLibrarian_email.setText(s.getSubscriber_email()); // Assuming this is already a String
        this.txtSubscriber_detailedSubscriptionHistory.setText(String.valueOf(s.getDetailed_subscription_history())); // Convert int to String
    }

    public void getbtnClose(ActionEvent event) throws Exception {
        navigateToLibraryFrame(event);
    }

    public void btnUpdate(ActionEvent event) throws Exception {
        // Get the updated values from the text fields
        String id = txtSubscriber_id.getText();
        String phoneNumber = txtSubscriber_phoneNumber.getText();
        String email = txtSubscriber_email.getText();

        // Prepare the update message
        String obj = "Update:" + id + "," + phoneNumber + "," + email;

        // Send the update message to the server
        ClientUI.chat.accept(obj);

        // Navigate back to the LibraryFrame
        navigateToLibraryFrame(event);
    }

    private void navigateToLibraryFrame(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SubscraberFormWindow/SubscriberForm.fxml"));
        Parent root = loader.load();

        // Set up the scene and stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/SubscraberFormWindow/SubscriberForm.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Library Management Tool");
        stage.show();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Initialization logic, if needed
    }
}
