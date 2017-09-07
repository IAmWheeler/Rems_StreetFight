package com.duewop.remsstreetfight;

import com.duewop.remsstreetfight.core.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Rems Rumble");

        initRootLayout();
    }
    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        GridPane rootLayout;
        Parameters parameters = getParameters();
        List<String> rawArguments = parameters.getRaw();

        try {
            FXMLLoader loader = new FXMLLoader();

                loader.setLocation(Main.class.getResource("views/mainrun.fxml"));
                rootLayout = loader.load();

				Scene scene = new Scene(rootLayout);
//				scene.setFill(Color.TRANSPARENT);
                primaryStage.setScene(scene);
				primaryStage.initStyle(StageStyle.UNDECORATED);
				primaryStage.show();

                // Give the controller access to the main app.
                Controller controller = loader.getController();
                controller.SetupRumble(rawArguments);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
