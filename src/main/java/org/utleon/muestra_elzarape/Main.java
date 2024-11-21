package org.utleon.muestra_elzarape;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(Main.class.getResource("principal.fxml"));
        primaryStage.setTitle("Elzarape - Sistema de Control");
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }
}
