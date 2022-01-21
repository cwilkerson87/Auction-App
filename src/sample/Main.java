package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.*;

import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("Resources/Auction App.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setTitle("Auction App");
        primaryStage.setScene(scene);
        scene.getStylesheets().add("sample/StyleSheets/mainStylesheet.css");
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.getButtonTypes().setAll(yesButton, noButton);
                alert.setContentText("Are You Sure You Want To Exit?");

                Optional<ButtonType> type = alert.showAndWait();

                if (type.get() == yesButton) {
                    Platform.exit();
                    System.exit(0);
                }

                else if(type.get() == noButton){
                        alert.getDialogPane().getScene().getWindow().setOnCloseRequest(event-> event.consume());
                }
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }

}
