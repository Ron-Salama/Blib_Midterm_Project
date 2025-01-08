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

public class SubscriberWindowController extends BaseController implements Initializable {


    
    @FXML
	private Button btnSearch = null;
    
    @FXML
    private Button btnMyBooks = null;

    @FXML
    private Button btnBorrow = null;
    
    @FXML
    private Button btnBack = null;



   
    
    public void search(ActionEvent event) throws Exception {
	    openWindow(event,
	    		"/gui/SearchWindow/SearchFrame.fxml",
	    		"/gui/SearchWindow/SearchFrame.css",
	    		"Search a Book");
	}

    public void getbtnBack(ActionEvent event) throws Exception {
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
    
   
    //***DONT DELETE IMPORTANT FOR LATER USE***
	/*public void getbtnBorrow(ActionEvent event) throws Exception {
	    openBorrowWindow(event);
	}
    */
    
    
    
    
    
    
    //*******DONT DELETE, THIS IS IMPORTANT FOR LATER FOR THE USE OF GOING TO THE BORROW WINDOW AFTER PUSHING THE BORROW BUTTON******
   /* private void openBorrowWindow(ActionEvent event) {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/BorrowWindowController/BorrowWindow.fxml"));
        Parent root = loader.load();

        // Set up the scene and stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/BorrowWindowController/BorrowWindow.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("TEST--WILL--CHANGE--LATER");
        stage.show();
	}*/
    
     
    
    
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
    
    
    public void start(Stage primaryStage) throws Exception {
    	start(primaryStage,
    			"/gui/SubscriberWindow/SubscriberWindow.fxml",
    			"/gui/SubscriberWindow/SubscriberWindow.css", 
    			"Library Managment Tool");
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Initialization logic, if needed
    }
}
