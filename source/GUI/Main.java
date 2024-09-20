/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package GUI;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author dan
 */

public class Main extends Application {
    
    public static final String VERSION = "1.0";

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) {
        Parent root;
        var loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            System.err.println("Error reading: " + e);
            e.printStackTrace();
            return;
        }
        var controller = (MainController)loader.getController();
        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Table Manager");
        stage.setOnCloseRequest((ev) -> {controller.onCloseAction(ev);});
        stage.show();
    }
    
}
