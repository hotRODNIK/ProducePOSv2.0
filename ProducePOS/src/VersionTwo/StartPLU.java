package VersionTwo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

class StartPLU {
    private static final Stage primaryStage = new Stage();

    void start() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("PLU.fxml"));
        primaryStage.setTitle("ProducePOS v2.0 PLU Maintenance");
        primaryStage.setScene(new Scene(root, 370, 400));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // This method hides the stage when invoked
    static void killProcess(){
        primaryStage.close();
    }
}