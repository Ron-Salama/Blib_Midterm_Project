package gui.LibrarianWindow;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import gui.MyBooksWindow.MyBooksController;
import gui.SearchWindow.SearchFrameController;
import gui.baseController.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import logic.Librarian;

/**
 * Controller class for the Librarian window. This class handles navigation between different windows,
 * displays the current librarian's greeting based on the time of day, and updates the return dates text area.
 */
public class LibrarianController extends BaseController implements Initializable  {
    
	// The current librarian instance
    public static Librarian currentLibrarian;
    
    // Button to exit the application
    @FXML
    private Button btnExit = null;

    // Button to return to the main menu 
    @FXML
    private Button btnReturnToMainMenu = null;

    // Button to search for books 
    @FXML
    private Button btnSearchBook = null;

    // Button to search for subscribers 
    @FXML
    private Button btnSearchSubscriber = null;

    // Button to view subscriber requests 
    @FXML
    private Button btnSubscriberRequests = null;

    // Button to view reports
    @FXML
    private Button btnViewReports = null;

    // Label to display a greeting message to the librarian 
    @FXML
    private Label greetingLabel = null;
    
    // Text area to display updates for new return dates from subscribers 
    @FXML
    private TextArea returnDateUpdatesTextArea;
    
    /**
     * Initializes the librarian controller by setting the current librarian
     * and updating the greeting message based on the time of day.
     * Also, attempts to update the return date updates text area.
     *
     * @param arg0 the location used to resolve relative paths for the root object, or null if the location is not known.
     * @param arg1 the resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	currentLibrarian = new Librarian(ChatClient.l1.getLibrarian_id(),ChatClient.l1.getLibrarian_name());
    	ChangeWelcomeLabelByTheTimeOfDay();
    	
    	try {
			updateNewReturnDateTextArea();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * Navigates to the reports window when the 'View Reports' button is clicked.
     *
     * @param event the ActionEvent triggered by the button click.
     * @throws Exception if there is an error opening the new window.
     */
    public void navigateToViewReports(ActionEvent event) throws Exception { 
    	openWindow(event, 
                "/gui/ReportsWindow/ReportsWindow.fxml", 
                "/gui/ReportsWindow/ReportsWindow.css", 
                "Reports Window");
    }
    
    /**
     * Navigates to the search subscriber window when the 'Search Subscriber' button is clicked.
     *
     * @param event the ActionEvent triggered by the button click.
     * @throws Exception if there is an error opening the new window.
     */
    public void navigateToSearchSubscriber(ActionEvent event) throws Exception { // *REMOVE NOTES AFTER CREATING SEARCH SUBSCRIBER WINDOW AND LINK BUTTON "VIEW REPORTS" TO THAT WINDOW*
    	MyBooksController.viewing = true;
    	MyBooksController.librarianViewing = currentLibrarian.getLibrarian_id();
    	MyBooksController.LibrarianName = currentLibrarian.getLibrarian_name();
    	openWindow(event, 
                "/gui/MyBooksWindow/MyBooksFrame.fxml", 
                "/gui/MyBooksWindow/MyBooksFrame.css", 
                "Reports Window");
    }
    
    /**
     * Navigates to the subscriber requests window when the 'Subscriber Requests' button is clicked.
     *
     * @param event the ActionEvent triggered by the button click.
     * @throws Exception if there is an error opening the new window.
     */
    public void navigateToSubscriberRequests(ActionEvent event) throws Exception { 
        openWindow(event, 
                "/gui/SubscriberRequestsWindows/SubscriberRequestsWindowsFrame.fxml", 
                "/gui/SubscriberRequestsWindows/SubscriberRequestsWindowsFrame.css", 
                "Subscriber Requests Window");	
    }
    
    /**
     * Navigates to the search window when the 'Search Book' button is clicked.
     *
     * @param event the ActionEvent triggered by the button click.
     * @throws Exception if there is an error opening the new window.
     */
    public void navigateToSearchWindow(ActionEvent event) throws Exception {
        SearchFrameController.FlagForSearch = "Librarian";
        openWindow(event,
                "/gui/SearchWindow/SearchFrame.fxml",
                "/gui/SearchWindow/SearchFrame.css",
                "Search a Book");
    }

    /**
     * Navigates to the main menu when the 'Return to Main Menu' button is clicked.
     *
     * @param event the ActionEvent triggered by the button click.
     * @throws Exception if there is an error opening the new window.
     */
    public void navigateToMainMenu(ActionEvent event) throws Exception {
    	SearchFrameController.FlagForSearch = "";
    	openWindow(event,
    			"/gui/MainMenu/MainMenuFrame.fxml",
    			"/gui/MainMenu/MainMenuFrame.css",
    			"Main Menu");
    }
    
    /**
     * Changes the welcome label's text based on the current time of day.
     * It greets the librarian with an appropriate message such as "Good Morning",
     * "Good Afternoon", or "Good Night".
     */
    private void ChangeWelcomeLabelByTheTimeOfDay() {
    	LocalDateTime time = LocalDateTime.now();
    	String[] timeSplit = time.toString().split("T");
    	String hour = timeSplit[1].substring(0, 2);
    	
    	String message;
    	
    	 int hourOfDay = Integer.parseInt(hour);
    	 
    	 if (hourOfDay < 12 && 7 < hourOfDay) {
    		 message = "Good Morning, " + currentLibrarian.getLibrarian_name() +  " ☀";
    	 }
    	 else if (hourOfDay >= 12 && 18 > hourOfDay) {
    		 message = "Good Afternoon, " + currentLibrarian.getLibrarian_name() + " 🌅";
    	 }
    	 else {
    		 message = "Good Night, " + currentLibrarian.getLibrarian_name() + " 🌙";
    	 }
    	 
    	 showColoredLabelMessageOnGUI(greetingLabel, message, "-fx-text-fill: black;");
    }
    
    /**
     * Updates the return date updates text area with the new extension information pulled from the server.
     * Each entry contains the subscriber ID, book title, extension approval, and new return date.
     *
     * @throws InterruptedException if the operation is interrupted while waiting for server response.
     */
    private void updateNewReturnDateTextArea() throws InterruptedException {
    	returnDateUpdatesTextArea.setEditable(false); // Make the text area read only.
    	
    	ClientUI.chat.accept("PullNewExtenstion:");
    	
    	waitForServerResponse();
    	
    	if (ChatClient.extendedReturnDatesFromSubscriber.isEmpty()) { // Return in case there's no information to work on.
    		return;
    	}
    	
    	String[] extendedReturnDatesFromSubscribers = ChatClient.extendedReturnDatesFromSubscriber.split(";");
    	
    	for (String extensionInformation : extendedReturnDatesFromSubscribers) {
    		String data[] = extensionInformation.split(",");
    		returnDateUpdatesTextArea.appendText(data[0] + ": Subscriber ID: " + data[1] + ", " + data[2] + " extension approved. New return date: " + data[4] + ", for the book \"" + data[3] + "\".\n");
    	}
    }
}
