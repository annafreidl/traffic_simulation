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
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class VGame {
    MGame gameModel;
    Stage window;
    Group group;
    Canvas canvas;
    GraphicsContext gc;
    Scene scene;
    JSONParser parser;
    MenuBar menuBar;
    Label debugCoord;

    public double mouseX, mouseY;
    static double tWidth = Config.tWidth;
    static double tHeight = tWidth /2;
    static double tHeightHalf = tHeight / 2;
    static double tWidthHalf = tWidth / 2;
    static double worldWidth = Config.worldWidth;
    static double worldHeight = Config.worldHeight;

    public VGame(MGame gameModel, Stage stage) {
        this.gameModel = gameModel;
        this.window = stage;
        group = new Group();
        parser = new JSONParser();
        canvas = new Canvas((worldWidth+1)*tWidth, (worldHeight+1)*tHeight);
        gc = canvas.getGraphicsContext2D();


        group.getChildren().add(canvas);

        // Baumenü Beispiel
        Menu build = new Menu ("Buildings");
        Menu airport = new Menu("Airport");
        Menu rail = new Menu("Rail");
        Menu roads = new Menu("Road");
        Menu nature = new Menu("Nature");

        for(Buildings b: gameModel.getBuildingsList()){
            String name = b.getBuildingName();
            MenuItem m1 = new MenuItem();
            switch (b.getBuildMenu()){
                case "road":
                    VRoad menuRoadImage = new VRoad();
                    m1.setGraphic(menuRoadImage);
                    m1.setText(name);
                    roads.getItems().add(m1);
                    break;
                case "rail":
                    m1.setText(name);
                    rail.getItems().add(m1);
                    break;
                case "airport":
                    m1.setText(name);
                    airport.getItems().add(m1);
                    break;
                case "nature":
                    m1.setText(name);
                    nature.getItems().add(m1);
                    break;
                default:
                    if(b.getSpecial().equals("factory")){
                        m1.setText(name);
                        build.getItems().add(m1);
                    }
            }
        }

        // Größe des Items in Abhängigkeit der Seitenverhältnisse (universell für jedes Image benutzbar)
        Image house1 = new Image("Images/building.png");
        double scale = 20/house1.getWidth();
        double height = house1.getHeight()*scale;
        double width = house1.getWidth()*scale;

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(build, airport, rail, roads, nature);
        group.getChildren().add(menuBar);

        drawField();

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
        debugCoord = new Label("empty");
        debugCoord.setTranslateX(menuBar.getWidth());
        group.getChildren().add(debugCoord);


    }

    public void drawField(){
       clearField();
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gameModel.getTileHashMap().forEach((index, tile) -> {
            new VTile(tile, gc);
        });
    }

    public void clearField() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }



    // Kartesische Koordinaten werden zu isometrischen Koordinaten umgerechnet
    public static double[] toIso(double x, double y) {

        double i = (x - y) * tWidthHalf;
        double j = (x + y) * tHeightHalf;

        i += Config.Xoffset;
        j += Config.Yoffset;

        return new double[]{i, j};
    }

    // isometrische Koordinaten werden zu kartesischen umgerechnet
    public double[] toGrid(double x, double y) {
//        x -= Config.Xoffset;
//        y -= Config.Yoffset;

        double i = ((x / tWidthHalf) + (y / tHeightHalf)) / 2;
        double j = -(((y / tHeightHalf) - (x / tWidthHalf)) / 2 /*-(int)worldWidth + 1*/);

        return new double[] { i, j };
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public GraphicsContext getGc() {
        return gc;
    }

    public Scene getScene() {
        return scene;
    }

    public void showCoordinates(double x, double y) {
        double coordX = x;
        double coordY = y;

        double[] isoCoordinates = toGrid( coordX, coordY);
        double isoX = isoCoordinates[0];
        double isoY = isoCoordinates[1];

        debugCoord.setText("x: "+ isoX +"   y: "+ isoY +"     xGrid: "+coordX+"  yGrid: "+coordY);
    }
}
