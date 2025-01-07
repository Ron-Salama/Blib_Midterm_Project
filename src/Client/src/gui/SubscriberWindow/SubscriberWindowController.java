package gui.SubscriberWindow;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import client.ClientUI;
import gui.SearchWindow.SearchFrameController;
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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logic.Subscriber;

public class SubscriberWindowController implements Initializable {


    
    @FXML
	private Button btnSearch = null;
    
    @FXML
    private Button btnMyBooks = null;

    @FXML
    private Button btnBorrow = null;
    
    @FXML
    private Button btnBack = null;



   
    
    public void search(ActionEvent event) throws Exception {
	    openSearchWindow(event);
	}

    public void getbtnBack(ActionEvent event) throws Exception {
        navigateToMainMenu(event);
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
    
    private void openSearchWindow(ActionEvent event){
		try {
            // Hide the current window
            ((Node) event.getSource()).getScene().getWindow().hide();

            // Load the SubscriberForm window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SearchWindow/SearchFrame.fxml"));
            Pane root = loader.load();

            SearchFrameController searchFrameController = loader.getController();
//            mainMenuController.loadSubscriber(ChatClient.s1);

            Stage primaryStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/SearchWindow/SearchFrame.css").toExternalForm());
            primaryStage.setTitle("Search a Book");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
    
    
    
    
    
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
        Parent root = FXMLLoader.load(getClass().getResource("/gui/SubscriberWindow/SubscriberWindow.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/SubscriberWindow/SubscriberWindow.css").toExternalForm());
        primaryStage.setTitle("Library Managment Tool");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void navigateToMainMenu(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainMenu/MainMenuFrame.fxml"));
        Parent root = loader.load();

        // Set up the scene and stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/MainMenu/MainMenuFrame.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Library Management Tool");
        stage.show();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Initialization logic, if needed
    }
}
