package gui.ReportsWindow;

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

public class ReportsWindowController extends BaseController implements Initializable  {
    private ReportsWindowController lc;
    
    
    @FXML
    private Button btnBack = null;
    
    @FXML
    private Button btnBorrowDates = null;
    
    @FXML
    private Button btnSubscriberStatus = null;
    
    @FXML
    private Button btnReturnToMainMenu = null;
    
    @FXML
    private Label greetingLabel = null;
    
    
    
    
        
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	
    }
    
    public void getBackBtn(ActionEvent event) throws Exception {
    	openWindow(event,
    			"/gui/LibrarianWindow/LibrarianFrame.fxml",
    			"/gui/LibrarianWindow/LibrarianFrame.css",
    			"Librarian View");
	}
    
    
    
    
    
    //this method i put aside as a reference to use for further implementation of the methods below.
    /*
    public void navigateToViewReports(ActionEvent event) throws Exception { // *REMOVE NOTES AFTER CREATING VIEW REPORTS WINDOW AND LINK BUTTON "VIEW REPORTS" TO THAT WINDOW*
		
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
		 
    }*/
    
    
   //***********************************************************************************//
    //TODO: ***For now we are putting this aside until we know how to make a graph.***
    
   /* public void navigateToViewSubscriberStatus(ActionEvent event) throws Exception { // *REMOVE NOTES AFTER CREATING VIEW REPORTS WINDOW AND LINK BUTTON "VIEW REPORTS" TO THAT WINDOW*
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
		
    }*/
     
    
    //***********************************************************************************//
    //TODO: ***For now we are putting this aside until we know how to make a graph.***
    
    /* public void navigateToViewBorrowDates(ActionEvent event) throws Exception { // *REMOVE NOTES AFTER CREATING VIEW REPORTS WINDOW AND LINK BUTTON "VIEW REPORTS" TO THAT WINDOW*
		
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
		
    }*/
    
    
    //also a reference for further implementation of the methods above.
    /*public void navigateToSubscriberRequests(ActionEvent event) throws Exception { 
        openWindow(event, 
                "/gui/SubscriberRequestsWindows/SubscriberRequestsWindowsFrame.fxml", 
                "/gui/SubscriberRequestsWindows/SubscriberRequestsWindowsFrame.css", 
                "Subscriber Requests Window");
    	
    }*/
    
    
   

    public void navigateToMainMenu(ActionEvent event) throws Exception {
    	SearchFrameController.FlagForSearch = "";
    	openWindow(event,
    			"/gui/MainMenu/MainMenuFrame.fxml",
    			"/gui/MainMenu/MainMenuFrame.css",
    			"Main Menu");
    }
    
    public void openSubscriberStatusReport(ActionEvent event) throws Exception{
    	openWindow(event,
    			"/gui/LibrarianSubscriberStatusReportWindow/LibrarianSubscriberStatusReportFrame.fxml",
    			"/gui/LibrarianSubscriberStatusReportWindow/LibrarianSubscriberStatusReportFrame.css",
    			"Subscriber Status Report");
    }
   
}
