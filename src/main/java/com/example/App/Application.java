package com.example.App;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        Image icon = new Image(String.valueOf(Application.class.getResource("java.png")));
        stage.getIcons().add(icon);

        FXMLLoader loader = new FXMLLoader(Application.class.getResource("index.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 800);
        Controller mainController = loader.getController();

        stage.setTitle("Sorting Visualization");
        stage.setScene(scene);
        stage.show();
    }
}