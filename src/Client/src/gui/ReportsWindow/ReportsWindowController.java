package gui.ReportsWindow;

import java.net.URL;
import java.util.ResourceBundle;

import gui.SearchWindow.SearchFrameController;
import gui.baseController.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller class for handling actions in the Reports Window of the library management system.
 * It provides navigation options to different reports and the Main Menu.
 */
public class ReportsWindowController extends BaseController implements Initializable  {
	
	/** This button allows the user to navigate back to the Librarian View. */
    @FXML
    private Button btnBack = null;
    
    /** This button opens the Borrowed Books Status Report window. */
    @FXML
    private Button btnBorrowDates = null;
    
    /** This button opens the Subscriber Status Report window */
    @FXML
    private Button btnSubscriberStatus = null;
    
    /** This button allows the user to navigate back to the Main Menu. */
    @FXML
    private Button btnReturnToMainMenu = null;
    
    /** This label displays a greeting message to the user. */
    @FXML
    private Label greetingLabel = null;
 
    /**
     * Initializes the controller, though no specific initialization is performed here.
     *
     * @param arg0 the URL location of the FXML file used to initialize the controller.
     * @param arg1 the resources used to localize the root object, unused here.
     */    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {	
    }
    
    /**
     * Navigates back to the Librarian View when the Back button is clicked.
     *
     * @param event the event triggered by clicking the Back button.
     * @throws Exception if there is an error while opening the window.
     */
    public void getBackBtn(ActionEvent event) throws Exception {
    	openWindow(event,
    			"/gui/LibrarianWindow/LibrarianFrame.fxml",
    			"/gui/LibrarianWindow/LibrarianFrame.css",
    			"Librarian View");
	}
    
    /**
     * Navigates to the Main Menu when the Return to Main Menu button is clicked.
     *
     * @param event the event triggered by clicking the Return to Main Menu button.
     * @throws Exception if there is an error while opening the window.
     */
    public void navigateToMainMenu(ActionEvent event) throws Exception {
    	SearchFrameController.FlagForSearch = "";
    	openWindow(event,
    			"/gui/MainMenu/MainMenuFrame.fxml",
    			"/gui/MainMenu/MainMenuFrame.css",
    			"Main Menu");
    }
    
    /**
     * Opens the Borrowed Books Status Report window when the corresponding button is clicked.
     *
     * @param event the event triggered by clicking the Borrowed Books Status Report button.
     * @throws Exception if there is an error while opening the window.
     */
    public void openBorrowedBooksReport(ActionEvent event) throws Exception{
    	openWindow(event,
    			"/gui/LibrarianBorrowedBooksReportWindow/LibrarianBorrowedBooksReportFrame.fxml",
    			"/gui/LibrarianBorrowedBooksReportWindow/LibrarianBorrowedBooksReportFrame.css",
    			"Borrowed Books Status Report");
    }
    
    /**
     * Opens the Subscriber Status Report window when the corresponding button is clicked.
     *
     * @param event the event triggered by clicking the Subscriber Status Report button.
     * @throws Exception if there is an error while opening the window.
     */
    public void openSubscriberStatusReport(ActionEvent event) throws Exception{
    	openWindow(event,
    			"/gui/LibrarianSubscriberStatusReportWindow/LibrarianSubscriberStatusReportFrame.fxml",
    			"/gui/LibrarianSubscriberStatusReportWindow/LibrarianSubscriberStatusReportFrame.css",
    			"Subscriber Status Report");
    }
}
