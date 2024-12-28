package client;

import javafx.application.Application;
import javafx.stage.Stage;
import java.util.Scanner;
import gui.LibraryFrameController;

public class ClientUI extends Application {
    public static ClientController chat; // only one instance

    public static void main(String[] args) throws Exception {
        // Prompt the user for the IP address
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the server IP address: ");
        String ip = scanner.nextLine();
        scanner.close();

        // Pass the IP address as a system property for use in the Application
        System.setProperty("server.ip", ip);

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Retrieve the IP from the system property
        String ip = System.getProperty("server.ip");
        int port = 5555; // Default port

        // Initialize the client controller with the user-provided IP and default port
        chat = new ClientController(ip, port);

        // Initialize and start the library frame
        LibraryFrameController aFrame = new LibraryFrameController();
        aFrame.start(primaryStage);
    }
}
