package gui.SubscriberWindow;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import client.ChatClient;
import gui.MyBooksWindow.MyBooksController;
import gui.SearchWindow.SearchFrameController;
import gui.baseController.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import logic.ClientTimeDiffController;
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

	private ClientTimeDiffController clock = new ClientTimeDiffController();
	
	/** The button to open the Search window. */
    @FXML
	private Button btnSearch = null;
    
    /** The button to open the My Books window. */
    @FXML
    private Button btnMyBooks = null;
    
    /** The button to open the My Reservations window. */
    @FXML
    private Button btnMyReservations = null;

    /** The button to open the Borrow window. */
    @FXML
    private Button btnBorrow = null;
    
    @FXML
    private Label greetingLabel = null;
    
    @FXML
    private Label myStatusLabel = null;
    
    @FXML
    private Label frozenUntilAndDaysLeftDynamicText = null;
    
    /** The button to navigate back to the main menu. */
    @FXML
    private Button btnBack = null;
    @FXML
    private Button btnUpdate = null;
    
    private String status = null; // Frozen at / Not Frozen
    
    private String frozenUntil = null; // The date until which the subscriber is frozen. format: dd-MM-yyyy EX: 21-1-1999
    
    /**
     * Initializes the SubscriberWindowController.
     * This method is called automatically when the FXML file is loaded.
     * 
     * @param arg0 the location of the FXML file
     * @param arg1 the resources for the FXML file
     */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	    currentSubscriber = new Subscriber(
	        ChatClient.s1.getSubscriber_id(),
	        ChatClient.s1.getDetailed_subscription_history(),
	        ChatClient.s1.getSubscriber_name(),
	        ChatClient.s1.getSubscriber_phone_number(),
	        ChatClient.s1.getSubscriber_email(),
	        ChatClient.s1.getStatus()
	    );
	    
	    String frozenData[] = clock.parseFrozenSubscriberStatus(currentSubscriber.getStatus()); 
	    
	    if (isSubsriberFrozen(frozenData[1])) {
	    	status = frozenData[1]; // Make the status easier to manipulate and use.
	    	
	    	// Take the date the account was frozen at, add a month and return it as a string.
	    	frozenUntil = clock.convertStringToLocalDate(frozenData[2]).plusMonths(1).format(clock.getDateFormatter()).toString();
	    	
	    	btnBorrow.setDisable(true);
            changefrozenUntilAndDaysLeftDynamicTextToSubsriberStatus();
	    }else { // Subscriber not frozen than set the status to Not Frozen.
	    	status = frozenData[1];
	    }
	   
	    ChangeWelcomeLabelByTheTimeOfDay();
	    changeMyStatusLabelAccordingToSubscriberStatus();
	}
	
    public static Subscriber currentSubscriber;
        
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
    
    public void openMyBooksWindow(ActionEvent event) throws Exception {
    	MyBooksController.viewing = false;
    	MyBooksController.librarianViewing = -1;
		openWindow(event,
	       		"/gui/MyBooksWindow/MyBooksFrame.fxml",
	       		"/gui/MyBooksWindow/MyBooksFrame.css",
	       		"My Books");;
	}
    
    
    public void openMyReservationsWindow(ActionEvent event) throws Exception {
		openWindow(event,
	       		"/gui/MyReservationsWindow/MyReservationsFrame.fxml",
	       		"/gui/MyReservationsWindow/MyReservationsFrame.css",
	       		"My Reservations");;
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
        		"Update Information");;
	}
    
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
    		 message = "Good Morning, " + currentSubscriber.getSubscriber_name() +  " ☀";
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
                "-fx-effect: dropshadow(gaussian, rgba(30, 144, 255, 0.7), 5, 0.3, 0, 0); " +
                "-fx-font-weight: bold;";


    	String notFrozenStyle = "-fx-text-fill: black; " +
                   "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.2, 0, 0);";
    	
    	if (isSubsriberFrozen(status)) {
    		showColoredLabelMessageOnGUI(myStatusLabel, "My Status is: Frozen", frozenStyle);    		
    	}
    	else {
    		showColoredLabelMessageOnGUI(myStatusLabel, "My Status is: Not Frozen", notFrozenStyle);
    	}
    }
    
    private void changefrozenUntilAndDaysLeftDynamicTextToSubsriberStatus() {
    	String frozenStyle = "-fx-text-fill: linear-gradient(to right, #1e90ff, #4682b4); " +
                "-fx-effect: dropshadow(gaussian, rgba(30, 144, 255, 0.7), 5, 0.3, 0, 0); " +
                "-fx-font-weight: bold;";
    	
    	int daysLeftUntilUnfrozen = clock.timeDateDifferenceBetweenTwoDates(clock.timeNow(), frozenUntil);
    	String frozenMessage = "Frozen until: " + frozenUntil + "\nDays left until unfrozen: " +  daysLeftUntilUnfrozen;
    	showColoredLabelMessageOnGUI(frozenUntilAndDaysLeftDynamicText, frozenMessage, frozenStyle);
    }
}
