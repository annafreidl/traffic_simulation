package planverkehr;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        MGame gameModel = new MGame();
        VGame gameView = new VGame(gameModel, primaryStage);

    }


    public static void main(String[] args) {
        launch(args);
    }
}
