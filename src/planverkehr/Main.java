package planverkehr;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        GameConfig gameConfig = new GameConfig();

        MGame gameModel = new MGame(gameConfig);
        VGame gameView = new VGame(gameModel, primaryStage);
        new Controller(gameModel, gameView);

    }


    public static void main(String[] args) {
        launch(args);
    }

}
