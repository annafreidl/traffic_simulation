package planverkehr;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;

public class VGame {
    MGame gameModel;
    Stage window;
    Group group;
    Canvas canvas;
    Scene scene;

    public double mouseX, mouseY;
    int TILE_WIDTH = 40;
    int TILE_HEIGHT = 20;
    int TILE_HEIGHT_HALF = TILE_HEIGHT/2;
    int TILE_WIDTH_HALF = TILE_WIDTH/2;
    int Offset = Config.windowSize/2;


    public VGame (MGame gameModel, Stage stage){
        this.gameModel = gameModel;
        this.window = stage;
        group = new Group();
        canvas = new Canvas(Config.windowSize, Config.windowSize);

        group.getChildren().add(canvas);

        // Canvas wird erzeugt:
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BISQUE);

        // zeichnet isometrische Rauten auf die Karte
        for (int i = 1; i<Config.windowSize; i++) {
            for(int j =1; j<Config.windowSize; j++) {

                int[] isoCoordinates = toIso(i, j);
                int xnew = isoCoordinates[0];
                int ynew = isoCoordinates[1];

                gc.beginPath();
                gc.moveTo(xnew-TILE_WIDTH_HALF, ynew);
                gc.lineTo(xnew, ynew -TILE_HEIGHT_HALF);
                gc.lineTo(xnew +TILE_WIDTH_HALF, ynew);
                gc.lineTo(xnew, ynew+TILE_HEIGHT_HALF);
                gc.closePath();
                gc.stroke();
            }
        }

        scene = new Scene(group, Config.windowSize, Config.windowSize);

        window.setTitle("Planverkehr");
        window.setScene(scene);
        window.show();

        scene.setOnScroll(event -> {

            if (event.getDeltaY() == 0)
                return;

            // Bestehende Skalierung lesen
            double scaleX = group.getScaleX();
            double scaleY = group.getScaleY();

            // Bestehende Skalierung kopieren
            double oldScaleX = scaleX;
            double oldScaleY = scaleY;

            // Neue Skalierung berechnen
            // getDelta ist abhängig von den Scrolleinstellungen vom Betriebssystem
            scaleX *= Math.pow(1.01, event.getDeltaY());
            scaleY *= Math.pow(1.01, event.getDeltaY());

            double fx = (scaleX / oldScaleX) - 1;
            double fy = (scaleY / oldScaleY) - 1;

            // getBoundsInParent() gibt die Grenzen zurück, nachdem sie mit Translate angepasst wurden
            double dx = (event.getSceneX() - (group.getBoundsInParent().getWidth() / 2 + group.getBoundsInParent().getMinX()));
            double dy = (event.getSceneY() - (group.getBoundsInParent().getHeight() / 2 + group.getBoundsInParent().getMinY()));

            // Neue Skalierung setzen
            group.setScaleX(scaleX);
            group.setScaleY(scaleY);
            group.setTranslateX(group.getTranslateX() - fx * dx);
            group.setTranslateY(group.getTranslateY() - fy * dy);

            event.consume();
        });

        // Events für drag&drop
        // Position speichern an der die Maustaste gedrückt wurde
        scene.setOnMousePressed(event -> {
            event.consume();
            mouseX = event.getX();
            mouseY = event.getY();
//            VGame.this.toFront();
        });

        // wenn Maustaste losgelassen wird, group verschieben
        scene.setOnMouseDragged(event -> {
            event.consume();
            double mouseNewX = event.getX();
            double mouseNewY = event.getY();
            double deltaX = mouseNewX - mouseX;
            double deltaY = mouseNewY - mouseY;
            group.setTranslateX(group.getTranslateX() + deltaX);
            group.setTranslateY(group.getTranslateY() + deltaY);

            mouseX = mouseNewX;
            mouseY = mouseNewY;
        });

    }

    // Kartesische Koordinaten werden zu isometrischen Koordinaten umgerechnet
    public int[] toIso(int x, int y) {
        int i = (x - y) * TILE_WIDTH_HALF;
        int j = (x + y) * TILE_HEIGHT_HALF;

        i += Offset-TILE_WIDTH_HALF;

        return new int[] { i, j };
    }

}
