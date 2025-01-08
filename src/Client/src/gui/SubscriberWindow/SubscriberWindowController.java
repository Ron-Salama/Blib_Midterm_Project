package gui.SubscriberWindow;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import gui.SearchWindow.SearchFrameController;
import gui.baseController.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

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
    
    /** The button to navigate back to the main menu. */
    @FXML
    private Button btnBack = null;

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
        		"Library Management Tool");;
    }
   
   //***DONT DELETE IMPORTANT FOR LATER USE***
   /* public void getbtnMyBooks(ActionEvent event) throws Exception {
	    openMyBooksWindow(event);
	}
    */
    
   
   
	public void getbtnBorrow(ActionEvent event) throws Exception {
	    openBorrowWindow(event);
	}
    
    
    
    
    
    
    
  
   private void openBorrowWindow(ActionEvent event) {
	   openWindow(event,
       		"/gui/BorrowBookWindow/BorrowBookFrame.fxml",
       		"/gui/BorrowBookWindow/BorrowBookFrame.css",
       		"Library Management Tool");;
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
    			"Library Managment Tool");
    }

    /**
     * Initializes the SubscriberWindowController.
     * This method is called automatically when the FXML file is loaded.
     * 
     * @param arg0 the location of the FXML file
     * @param arg1 the resources for the FXML file
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Initialization logic, if needed
    }
}
