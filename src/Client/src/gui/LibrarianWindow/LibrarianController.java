package gui.LibrarianWindow;

import java.net.URL;
import java.util.ResourceBundle;

import gui.SearchWindow.SearchFrameController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import gui.baseController.BaseController;

public class LibrarianController extends BaseController implements Initializable  {
    private LibrarianController lc;

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
    
    public void navigateToSubscriberRequests(ActionEvent event) throws Exception { 
        openWindow(event, 
                "/gui/SubscriberRequestsWindows/SubscriberRequestsWindowsFrame.fxml", 
                "/gui/SubscriberRequestsWindows/SubscriberRequestsWindowsFrame.css", 
                "Library Management Tool");
    	
    }
    
    
    public void navigateToSearchWindow(ActionEvent event) throws Exception {
    	SearchFrameController.FlagForSearch = "Librarian";
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SearchWindow/SearchFrame.fxml"));
        Parent root = loader.load();

        // Set up the scene and stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/SearchWindow/SearchFrame.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Library Management Tool");
        stage.show();
    }

    public void navigateToMainMenu(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainMenu/MainMenuFrame.fxml"));
        Parent root = loader.load();

        // Set up the scene and stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/MainMenu/MainMenuFrame.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Library Management Tool");
        stage.show();
        SearchFrameController.FlagForSearch = "";
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Initialization logic, if needed
    }

	public Button getBtnExit() {
		return btnExit;
	}

	public void setBtnExit(Button btnExit) {
		this.btnExit = btnExit;
	}

	public Button getBtnSearchBook() {
		return btnSearchBook;
	}

	public void setBtnSearchBook(Button btnSearchBook) {
		this.btnSearchBook = btnSearchBook;
	}

	public Button getBtnReturnToMainMenu() {
		return btnReturnToMainMenu;
	}

	public void setBtnReturnToMainMenu(Button btnReturnToMainMenu) {
		this.btnReturnToMainMenu = btnReturnToMainMenu;
	}

	public Button getBtnSearchSubscriber() {
		return btnSearchSubscriber;
	}

	public void setBtnSearchSubscriber(Button btnSearchSubscriber) {
		this.btnSearchSubscriber = btnSearchSubscriber;
	}

	public Button getBtnSubscriberRequests() {
		return btnSubscriberRequests;
	}

	public void setBtnSubscriberRequests(Button btnSubscriberRequests) {
		this.btnSubscriberRequests = btnSubscriberRequests;
	}

	public Button getBtnViewReports() {
		return btnViewReports;
	}

	public void setBtnViewReports(Button btnViewReports) {
		this.btnViewReports = btnViewReports;
	}
}
