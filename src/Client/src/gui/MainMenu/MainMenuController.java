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

/**
 * Controller for the Main Menu of the Library Management Tool.
 * 
 * <p>This class manages user interactions in the main menu, allowing navigation to 
 * the search window, login window, or previous screens. It also handles application exit.</p>
 */
public class MainMenuController extends BaseController {

    private MainMenuController mmc;

    @FXML
    private Button btnExit = null;

    @FXML
    private Button btnSearch = null;

    @FXML
    private Button btnLogin = null;

    @FXML
    private Button btnBack = null;

    /**
     * Navigates to the Search Window for searching books.
     *
     * @param event the event triggered by clicking the search button.
     * @throws Exception if an error occurs during navigation.
     */
    public void search(ActionEvent event) throws Exception {
        openWindow(event, 
                   "/gui/SearchWindow/SearchFrame.fxml", 
                   "/gui/SearchWindow/SearchFrame.css", 
                   "Search a Book");
    }

    /**
     * Navigates to the Login Window for Librarian or Subscriber login.
     *
     * @param event the event triggered by clicking the login button.
     * @throws Exception if an error occurs during navigation.
     */
    public void login(ActionEvent event) throws Exception {
        openWindow(event, 
                   "/gui/LibraryFrameWindow/LibraryFrame.fxml", 
                   "/gui/LibraryFrameWindow/LibraryFrame.css", 
                   "Login");
    }

    /**
     * Navigates back to the IP Input Window.
     *
     * @param event the event triggered by clicking the back button.
     * @throws Exception if an error occurs during navigation.
     */
    public void back(ActionEvent event) throws Exception {
        openWindow(event, 
                   "/gui/IPInputWindow/IPInputFrame.fxml", 
                   "/gui/IPInputWindow/IPInputFrame.css", 
                   "Login");
    }

    /**
     * Starts the Main Menu window.
     *
     * @param primaryStage the primary stage of the application.
     * @throws Exception if an error occurs while starting the stage.
     */
    public void start(Stage primaryStage) throws Exception {
        start(primaryStage, 
              "/gui/MainMenuController/MainMenuFrame.fxml", 
              "/gui/MainMenuController/MainMenuFrame.css", 
              "Library Management Tool");
    }

    /**
     * Exits the application.
     *
     * @param event the event triggered by clicking the exit button.
     * @throws Exception this method does not currently throw exceptions but is 
     *         defined to maintain consistency with other methods.
     */
    public void getExitBtn(ActionEvent event) throws Exception {
        System.out.println("Exiting Library Tool");
        System.exit(1);
    }
}
