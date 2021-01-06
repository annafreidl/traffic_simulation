package planverkehr;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class VGame {
    MGame gameModel;
    Stage window;
    Group group;
    Canvas canvas;
    Scene scene;

    public VGame (MGame gameModel, Stage stage){
        this.gameModel = gameModel;
        this.window = stage;
        group = new Group();
        canvas = new Canvas(Config.windowSize, Config.windowSize);

        group.getChildren().add(canvas);
// Grafik auf dem Canvas zeichnen:
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BISQUE);
        gc.fillOval(5, 5, 100, 90);
        gc.strokeLine(50, 50, 70, 60);
        gc.strokeText("Text", 10, 20);

        scene = new Scene(group, Config.windowSize, Config.windowSize);

        window.setTitle("Planverkehr");
        window.setScene(scene);
        window.show();
    }
}
