package gui.MainMenu;

import java.io.IOException;

import gui.LibraryFrameWindow.LibraryFrameController;
import gui.SearchWindow.SearchFrameController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import gui.baseController.*;

public class MainMenuController extends BaseController{

		private MainMenuController mmc;	
		
		@FXML
		private Button btnExit = null;
		
		@FXML
		private Button btnSearch = null;
		@FXML
		private Button btnLogin = null;
		@FXML
		private Button btnBack = null;
		
		public void search(ActionEvent event) throws Exception {
		    openWindow(event,
		    		"/gui/SearchWindow/SearchFrame.fxml",
		    		"/gui/SearchWindow/SearchFrame.css",
		    		"Search a Book");
		}
		
		public void login(ActionEvent event) throws Exception {
	        openWindow(event,
	        		"/gui/LibraryFrameWindow/LibraryFrame.fxml",
	        		"/gui/LibraryFrameWindow/LibraryFrame.css",
	        		"Login");
		}
		public void back(ActionEvent event) throws Exception {
	        openWindow(event,
	        		"/gui/IPInputWindow/IPInputFrame.fxml",
	        		"/gui/IPInputWindow/IPInputFrame.css",
	        		"Login");
		}
		
		
		public void start(Stage primaryStage) throws Exception {	
			start(primaryStage,
					"/gui/MainMenuController/MainMenuFrame.fxml",
					"/gui/MainMenuController/MainMenuFrame.css",
					"Library Managment Tool");	 	   
		}
		
		
		//TODO: placeholder for subscriber registration.
		public void getExitBtn(ActionEvent event) throws Exception {
			System.out.println("exit Library Tool");	
			System.exit(1);
		}
		
}
