package gui.LibrarianWindow;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import client.ChatClient;
import gui.SearchWindow.SearchFrameController;
import gui.baseController.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import logic.Librarian;

public class LibrarianController extends BaseController implements Initializable  {
    
    
    public static Librarian currentLibrarian;
    
    @FXML
    private Button btnExit = null;

    @FXML
    private Button btnReturnToMainMenu = null;

    @FXML
    private Button btnSearchBook = null;

    @FXML
    private Button btnSearchSubscriber = null;

    @FXML
    private Button btnSubscriberRequests = null;

    @FXML
    private Button btnViewReports = null;

    @FXML
    private Button btnScanBarcode = null;
    
    @FXML
    private Label greetingLabel = null;
    
        
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	currentLibrarian = new Librarian(ChatClient.l1.getLibrarian_id(),ChatClient.l1.getLibrarian_name());
    	ChangeWelcomeLabelByTheTimeOfDay();
    }
    
    public void getExitBtn(ActionEvent event) throws Exception {
		System.out.println("exit Library Tool");	
		System.exit(1);
	}
    
    public void navigateToViewReports(ActionEvent event) throws Exception { // *REMOVE NOTES AFTER CREATING VIEW REPORTS WINDOW AND LINK BUTTON "VIEW REPORTS" TO THAT WINDOW*
		/*
		 * FXMLLoader loader = new
		 * FXMLLoader(getClass().getResource("/gui/SearchWindow/SearchFrame.fxml"));
		 * Parent root = loader.load();
		 * 
		 * // Set up the scene and stage Stage stage = (Stage) ((Node)
		 * event.getSource()).getScene().getWindow(); Scene scene = new Scene(root);
		 * scene.getStylesheets().add(getClass().getResource(
		 * "/gui/SearchWindow/SearchFrame.css").toExternalForm());
		 * stage.setScene(scene); stage.setTitle("Library Management Tool");
		 * stage.show();
		 */
    }
    
    
    public void navigateToSearchSubscriber(ActionEvent event) throws Exception { // *REMOVE NOTES AFTER CREATING SEARCH SUBSCRIBER WINDOW AND LINK BUTTON "VIEW REPORTS" TO THAT WINDOW*

    }
    
    public void navigateToSubscriberRequests(ActionEvent event) throws Exception { 
        openWindow(event, 
                "/gui/SubscriberRequestsWindows/SubscriberRequestsWindowsFrame.fxml", 
                "/gui/SubscriberRequestsWindows/SubscriberRequestsWindowsFrame.css", 
                "Subscriber Requests Window");
    	
    }
    
    
    public void navigateToSearchWindow(ActionEvent event) throws Exception {
        SearchFrameController.FlagForSearch = "Librarian";
        openWindow(event,
                "/gui/SearchWindow/SearchFrame.fxml",
                "/gui/SearchWindow/SearchFrame.css",
                "Search a Book");
    }

    public void navigateToMainMenu(ActionEvent event) throws Exception {
    	SearchFrameController.FlagForSearch = "";
    	openWindow(event,
    			"/gui/MainMenu/MainMenuFrame.fxml",
    			"/gui/MainMenu/MainMenuFrame.css",
    			"Main Menu");
    }
    
    private void ChangeWelcomeLabelByTheTimeOfDay() {
    	LocalDateTime time = LocalDateTime.now();
    	String[] timeSplit = time.toString().split("T");
    	String hour = timeSplit[1].substring(0, 2);
    	
    	String message;
    	
    	 int hourOfDay = Integer.parseInt(hour);
    	 
    	 if (hourOfDay < 12 && 7 < hourOfDay) {
    		 message = "Good Morning, " + currentLibrarian.getLibrarian_name() +  " ☀️";
    	 }
    	 else if (hourOfDay >= 12 && 18 > hourOfDay) {
    		 message = "Good Afternoon, " + currentLibrarian.getLibrarian_name() + " 🌅";
    	 }
    	 else {
    		 message = "Good Night, " + currentLibrarian.getLibrarian_name() + " 🌙";
    	 }
    	 
    	 showColoredLabelMessageOnGUI(greetingLabel, message, "-fx-text-fill: black;");
    }
}
