package VersionTwo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

class StartBalancing {
    private static final Stage primaryStage = new Stage();

    void start() throws Exception {
        // Load the FXML file, create a stage and show it
        Parent root = FXMLLoader.load(getClass().getResource("Balancing.fxml"));
        primaryStage.setTitle("ProducePOS v2.0 Balancing");
        primaryStage.setScene(new Scene(root, 325, 415));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // This method hides the stage when invoked
    static void killProcess(){
        primaryStage.close();
    }
}