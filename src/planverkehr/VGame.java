package planverkehr;

import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class VGame {
    MGame gameModel;
    Stage window;
    Group group;
    Canvas canvas;
    Scene scene;

    public double mouseX, mouseY;
    static double tWidth = 40;
    static double tHeight = tWidth /2;
    static double tHeightHalf = tHeight / 2;
    static double tWidthHalf = tWidth / 2;
    static double offset = Config.windowSize / 2 - 65;
    static double worldWidth = 10;
    static double worldHeigth = 10;

    public VGame(MGame gameModel, Stage stage) {
        this.gameModel = gameModel;
        this.window = stage;
        group = new Group();
        canvas = new Canvas(Config.windowSize, Config.windowSize);

        group.getChildren().add(canvas);

        // Baumenü Beispiel
        Menu menu = new Menu("Menu 1");
        MenuItem menuItem1 = new MenuItem();

        // Größe des Items in Abhängigkeit der Seitenverhältnisse (universell für jedes Image benutzbar)
        Image house1 = new Image("building.png");
        double scale = 20/house1.getWidth();
        double height = house1.getHeight()*scale;
        double width = house1.getWidth()*scale;

        ImageView building = new ImageView(house1);
        building.setFitWidth(width);
        building.setFitHeight(height);
        menuItem1.setGraphic(building);

        menu.getItems().add(menuItem1);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);
        group.getChildren().add(menuBar);

        // Canvas wird erzeugt:
        GraphicsContext gc = canvas.getGraphicsContext2D();

// set background color !! todo: doesn't work while zooming !!
//        gc.setFill(Color.BISQUE);
//        gc.fillRect(
//            0,
//            0,
//            canvas.getWidth(),
//            canvas.getHeight());

        // zeichnet isometrische Rauten auf die Karte
        for (int i = 0; i < worldWidth; i++) {
            for (int j = 0; j < worldHeigth; j++) {

                double[] isoCoordinates = toIso(i, j);
                double xnew = isoCoordinates[0];
                double ynew = isoCoordinates[1];

                gc.beginPath();
                gc.moveTo(xnew, ynew);
                gc.lineTo(xnew+tWidthHalf, ynew - tHeightHalf);
                gc.lineTo(xnew + tWidth, ynew);
                gc.lineTo(xnew+tWidthHalf, ynew + tHeightHalf);
                gc.setFill(Color.LIMEGREEN);
                gc.closePath();
                gc.fill();
                gc.stroke();
                gc.setFill(Color.BLACK);
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setTextBaseline(VPos.CENTER);
                gc.fillText(
                    i+","+(j-(int)worldWidth+1),
                    xnew,
                    ynew-2
                );
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
            double scaleX = canvas.getScaleX();
            double scaleY = canvas.getScaleY();

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
            canvas.setScaleX(scaleX);
            canvas.setScaleY(scaleY);
            canvas.setTranslateX(canvas.getTranslateX() - fx * dx);
            canvas.setTranslateY(canvas.getTranslateY() - fy * dy);

            event.consume();
        });

        // Events für drag&drop
        // Position speichern an der die Maustaste gedrückt wurde
        scene.setOnMousePressed(event -> {
            event.consume();
            mouseX = event.getX();
            mouseY = event.getY();
        });

        // wenn Maustaste losgelassen wird, canvas verschieben
        // !! Koordinaten im Label stimmen dann nicht mehr!!
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                event.consume();
                double mouseNewX = event.getX();
                double mouseNewY = event.getY();
                double deltaX = mouseNewX - mouseX;
                double deltaY = mouseNewY - mouseY;
                canvas.setTranslateX(canvas.getTranslateX() + deltaX);
                canvas.setTranslateY(canvas.getTranslateY() + deltaY);

                mouseX = mouseNewX;
                mouseY = mouseNewY;
            }
        });

        // Label zur Anzeige der Koordinaten auf denen man sich befindet (funktioniert noch nicht bei Zoom)
        Label debugCoord = new Label("empty");
        debugCoord.setTranslateX(menuBar.getWidth());
        group.getChildren().add(debugCoord);
        scene.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {

            double coordX = e.getX();
            double coordY = e.getY();

            double[] isoCoordinates = toGrid( coordX, coordY);
            double isoX = isoCoordinates[0];
            double isoY = isoCoordinates[1];

            debugCoord.setText("x: "+ isoX +"   y: "+ isoY);

        });

    }

    // Kartesische Koordinaten werden zu isometrischen Koordinaten umgerechnet
    public static double[] toIso(double x, double y) {

        double i = (x - y) * tWidthHalf;
        double j = (x + y) * tHeightHalf;

        i += offset;
        j += offset;

        return new double[]{i, j};
    }

    // isometrische Koordinaten werden zu kartesischen umgerechnet
    public double[] toGrid(double x, double y) {
        x -= offset;
        y -= offset;

        double i = ((x / tWidthHalf) + (y / tHeightHalf)) / 2;
        double j = -(((y / tHeightHalf) - (x / tWidthHalf)) / 2 -(int)worldWidth + 1);

        return new double[] { i, j };
    }

}
