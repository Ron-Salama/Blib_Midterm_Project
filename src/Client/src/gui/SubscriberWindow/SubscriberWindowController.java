package gui.SubscriberWindow;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import client.ChatClient;
import gui.SearchWindow.SearchFrameController;
import gui.baseController.BaseController;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.Subscriber;


/**
 * The SubscriberWindowController class manages the Subscriber Window in the GUI.
 * This window allows users to perform actions such as searching for books, viewing borrowed books,
 * and returning to the main menu. 
 * 
 * <p>This class extends {@link BaseController} and implements {@link Initializable} for GUI initialization.</p>
 * 
 * <p>Some methods and functionality have been commented out but retained for future use, such as accessing
 * the "My Books" and "Borrow" windows.</p>
 */
public class SubscriberWindowController extends BaseController implements Initializable {

	/** The button to open the Search window. */
    @FXML
	private Button btnSearch = null;
    
    /** The button to open the My Books window. */
    @FXML
    private Button btnMyBooks = null;

    /** The button to open the Borrow window. */
    @FXML
    private Button btnBorrow = null;
    
    @FXML
    private Label greetingLabel = null;
    
    @FXML
    private Label myStatusLabel = null;
    
    /** The button to navigate back to the main menu. */
    @FXML
    private Button btnBack = null;
    @FXML
    private Button btnUpdate = null;
    
    
    /**
     * Initializes the SubscriberWindowController.
     * This method is called automatically when the FXML file is loaded.
     * 
     * @param arg0 the location of the FXML file
     * @param arg1 the resources for the FXML file
     */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ChangeWelcomeLabelByTheTimeOfDay();
		changeMyStatusLabelAccordingToSubscriberStatus();
	}
	
    public static Subscriber currentSubscriber = new Subscriber(ChatClient.s1.getSubscriber_id(),ChatClient.s1.getDetailed_subscription_history(),ChatClient.s1.getSubscriber_name(),ChatClient.s1.getSubscriber_phone_number(),ChatClient.s1.getSubscriber_email(), ChatClient.s1.getStatus());
        
    /**
     * Opens the Search window, allowing the user to search for books in the library.
     * 
     * @param event the {@link ActionEvent} triggered by clicking the Search button
     * @throws Exception if an error occurs while loading the Search window
     */
    public void search(ActionEvent event) throws Exception {
    	SearchFrameController.FlagForSearch = "Subscriber";
	    openWindow(event,
	    		"/gui/SearchWindow/SearchFrame.fxml",
	    		"/gui/SearchWindow/SearchFrame.css",
	    		"Search a Book");
	}

    /**
     * Navigates back to the main menu window.
     * 
     * @param event the {@link ActionEvent} triggered by clicking the Back button
     * @throws Exception if an error occurs while loading the main menu window
     */
    public void getbtnBack(ActionEvent event) throws Exception {
    	SearchFrameController.FlagForSearch = "";
        openWindow(event,
        		"/gui/MainMenu/MainMenuFrame.fxml",
        		"/gui/MainMenu/MainMenuFrame.css",
        		"Main Menu");;
    }
   
   //***DONT DELETE IMPORTANT FOR LATER USE***
   /* public void getbtnMyBooks(ActionEvent event) throws Exception {
	    openMyBooksWindow(event);
	}
    */
    
    public void openMyBooksWindow(ActionEvent event) throws Exception {
		openWindow(event,
	       		"/gui/MyBooksWindow/MyBooksFrame.fxml",
	       		"/gui/MyBooksWindow/MyBooksFrame.css",
	       		"My Books");;
	}
    
	public void openBorrowWindow(ActionEvent event) throws Exception {
		openWindow(event,
	       		"/gui/BorrowBookWindow/BorrowBookFrame.fxml",
	       		"/gui/BorrowBookWindow/BorrowBookFrame.css",
	       		"Borrow a Book");;
	}
    
	public void update(ActionEvent event) throws Exception {
        openWindow(event,
        		"/gui/UpdateInfoWindow/UpdateInfoFrame.fxml",
        		"/gui/UpdateInfoWindow/UpdateInfoFrame.css",
        		"Update Inforamtion");;
	}
    
    //******DONT DELETE, THIS IS IMPORTANT FOR LATER FOR THE USE OF GOING TO THE MyBooks WINDOW AFTER PUSHING THE MyBooks BUTTON********
    /*private void openMyBooksWindow(ActionEvent event) {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MyBooksWindowController/MyBooksWindow.fxml"));
        Parent root = loader.load();

        // Set up the scene and stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/MyBooksWindowController/MyBooksWindow.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("TEST--WILL--CHANGE--LATER");
        stage.show();
	}
    */
    
    /**
     * Starts the Subscriber Window application.
     * 
     * @param primaryStage the primary stage for the application
     * @throws Exception if an error occurs while loading the Subscriber window
     */
    public void start(Stage primaryStage) throws Exception {
    	start(primaryStage,
    			"/gui/SubscriberWindow/SubscriberWindow.fxml",
    			"/gui/SubscriberWindow/SubscriberWindow.css", 
    			"Subscriber View");
    }

    private void ChangeWelcomeLabelByTheTimeOfDay() {
    	LocalDateTime time = LocalDateTime.now();
    	String[] timeSplit = time.toString().split("T");
    	String hour = timeSplit[1].substring(0, 2);
    	
    	String message;
    	
    	 int hourOfDay = Integer.parseInt(hour);
    	 
    	 if (hourOfDay < 12 && 7 < hourOfDay) {
    		 message = "Good Morning, " + currentSubscriber.getSubscriber_name() +  " ☀️";
    	 }
    	 else if (hourOfDay >= 12 && 18 > hourOfDay) {
    		 message = "Good Afternoon, " + currentSubscriber.getSubscriber_name() + " 🌅";
    	 }
    	 else {
    		 message = "Good Night, " + currentSubscriber.getSubscriber_name() + " 🌙";
    	 }
    	 
    	 showColoredLabelMessageOnGUI(greetingLabel, message, "-fx-text-fill: black;");
    }
    
    private void changeMyStatusLabelAccordingToSubscriberStatus() {
    	String frozenStyle = "-fx-text-fill: linear-gradient(to right, #1e90ff, #4682b4); " +
                "-fx-effect: dropshadow(gaussian, rgba(30,144,255,0.5), 10, 0.5, 0, 0);";

    	String notFrozenStyle = "-fx-text-fill: black; " +
                   "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.2, 0, 0);";


        String statusMessage = isSubsriberFrozen(currentSubscriber)
            ? "My Status is: Frozen ❄️"
            : "My Status is: Not Frozen";

        String style = isSubsriberFrozen(currentSubscriber) ? frozenStyle : notFrozenStyle;
    }

}
