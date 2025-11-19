package com.skoglund;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("My first JavaFX Application");


        BorderPane rootLayout = new BorderPane();
        Scene scene = new Scene(rootLayout);

    }
}
