package gui.ServerPort;

import gui.baseController.BaseController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import server.ServerUI;

public class ServerPortFrameController extends BaseController{
	
	String temp="";
	
	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnDone = null;
	@FXML
	private Label lbllist;
	
	@FXML
	private Label dynamicLabel;
	
	@FXML
	private TextField portxt;
	
	ObservableList<String> list;
	
	private String getport() {
		return portxt.getText();			
	}
	
	public void Done(ActionEvent event) throws Exception {
		String p;
		
		p=getport();
		if(p.trim().isEmpty()) {
			showColoredLabelMessageOnGUI(dynamicLabel, "You must enter a port number.", "-fx-text-fill: red;");
			System.out.println("You must enter a port number");
		}
		else
		{
			((Node)event.getSource()).getScene().getWindow().hide(); //hiding primary window
			ServerUI.runServer(p);
			
			openWindow(event,
					"/gui/ServerLog/ServerLogFrame.fxml",
					"/gui/ServerLog/ServerLogFrame.css",
					"Server Log");
		}
	}

	public void start(Stage primaryStage) throws Exception {	
		start(primaryStage,
			  "/gui/ServerPort/ServerPort.fxml",
			  "/gui/ServerPort/ServerPort.css",
			  "Server - Enter Port");
		loadIcon(primaryStage);
	}
	
	public void getExitBtn(ActionEvent event) throws Exception {
		System.out.println("BLib Server Exit");
		System.exit(0);			
	}
	
	private void loadIcon(Stage primaryStage) {
        String iconRelativePath = "/assets/BLib_Server_Icon.png"; 

        // Load the icon image
        Image icon = new Image(getClass().getResourceAsStream(iconRelativePath));
        
        // Set the icon for the primary stage.
        primaryStage.getIcons().add(icon);
   }
}