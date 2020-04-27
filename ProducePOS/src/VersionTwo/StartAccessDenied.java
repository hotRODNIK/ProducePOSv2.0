package VersionTwo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

class StartAccessDenied {
    private static final Stage primaryStage = new Stage();

    void start() throws Exception {
        // Load the FXML file, create a stage and show it
        Parent root = FXMLLoader.load(getClass().getResource("AccessDenied.fxml"));
        primaryStage.setTitle("Access Denied");
        primaryStage.setScene(new Scene(root, 345, 200));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // This method hides the stage when invoked
    static void killProcess(){
        primaryStage.close();
    }
}