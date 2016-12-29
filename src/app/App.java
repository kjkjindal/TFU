package app;

import app.controller.AppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/resources/main.fxml")
            );
            fxmlLoader.setController(new AppController(primaryStage));

            primaryStage.setTitle("FluidXMan - Fluidic Experimentation Manager");
            primaryStage.setScene(new Scene(fxmlLoader.load(), 1024, 600));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Triggered when the application is shutting down
     */
    @Override
    public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
