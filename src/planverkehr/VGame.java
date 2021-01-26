package planverkehr;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

public class VGame {
    MGame gameModel;
    Stage window;
    Group group;
    Canvas canvas, canvasFront;
    GraphicsContext gc, gcFront;
    Scene scene;
    JSONParser parser;
    MenuBar menuBar;
    Label debugCoord;
    Button vehicleButton, tickButton;

    public double mouseX, mouseY;
    static double tWidth = Config.tWidth;
    static double tHeight = tWidth / 2;
    static double tHeightHalf = tHeight / 2;
    static double tWidthHalf = tWidth / 2;
    static double worldWidth = Config.worldWidth;
    static double worldHeight = Config.worldHeight;
    Timeline tl = new Timeline();


    public VGame(MGame gameModel, Stage stage) {
        this.gameModel = gameModel;
        this.window = stage;
        group = new Group();
        parser = new JSONParser();
        canvas = new Canvas((worldWidth + 1) * tWidth, (worldHeight + 1) * tHeight);
        canvasFront = new Canvas((worldWidth + 1) * tWidth, (worldHeight + 1) * tHeight);
        gc = canvas.getGraphicsContext2D();
        gcFront = canvasFront.getGraphicsContext2D();
        canvasFront.toFront();
        group.getChildren().addAll(canvas, canvasFront);

        Button pauseButton = new Button("Pause");
        group.getChildren().add(pauseButton);
        pauseButton.setLayoutX(0);
        pauseButton.setLayoutY(60);
        pauseButton.setOnAction(event -> tl.pause());


        Button karteButton = new Button("Karte");
        group.getChildren().add(karteButton);
        karteButton.setLayoutX(0);
        karteButton.setLayoutY(90);
        karteButton.setOnAction(event -> System.out.println("Karte"));

        tickButton = new Button("Tick");
        group.getChildren().add(tickButton);
        tickButton.setLayoutX(0);
        tickButton.setLayoutY(30);


        vehicleButton = new Button("Road Vehicle");
        group.getChildren().add(vehicleButton);
        vehicleButton.setLayoutX(0);
        vehicleButton.setLayoutY(120);


        // Baumenü Beispiel
        Menu build = new Menu("Buildings");
        build.setId(String.valueOf(EBuildType.building));
        Menu airport = new Menu("Airport");
        airport.setId(String.valueOf(EBuildType.airport));
        Menu rail = new Menu("Rail");
        rail.setId(String.valueOf(EBuildType.rail));
        Menu roads = new Menu("Road");
        roads.setId(String.valueOf(EBuildType.road));
        Menu nature = new Menu("Nature");
        nature.setId(String.valueOf(EBuildType.nature));

        gameModel.getBuildingsList().forEach((key, b) -> {
            String name = b.getBuildingName();
            MenuItem m1 = new MenuItem();
            m1.setText(name);
            m1.setId(name);
            Map<String, MCoordinate> directionsMap;
            switch (b.getBuildMenu()) {
                case "road":
                    directionsMap = b.getPoints();
                    VRoadMenu menuRoadImage = new VRoadMenu(directionsMap);
                    m1.setGraphic(menuRoadImage);
                    roads.getItems().add(m1);
                    break;
                case "rail":
                    directionsMap = b.getPoints();
                    List<Pair<String, String>> l = b.getRails();
                    VRailMenu menuRailImage = new VRailMenu(directionsMap, l);
                    m1.setGraphic(menuRailImage);
                    rail.getItems().add(m1);
                    break;
                case "airport":
                    airport.getItems().add(m1);
                    break;
                case "nature":
                    nature.getItems().add(m1);
                    break;
                default:
                    if (b.getSpecial().equals("factory")) {
                        build.getItems().add(m1);
                    }
            }
        });


        // Größe des Items in Abhängigkeit der Seitenverhältnisse (universell für jedes Image benutzbar)
        Image house1 = new Image("Images/building.png");
        double scale = 20 / house1.getWidth();
        double height = house1.getHeight() * scale;
        double width = house1.getWidth() * scale;

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

            canvasFront.setScaleX(scaleX);
            canvasFront.setScaleY(scaleY);
            canvasFront.setTranslateX(canvas.getTranslateX() - fx * dx);
            canvasFront.setTranslateY(canvas.getTranslateY() - fy * dy);


            event.consume();
        });

        // Events für drag&drop
        // Position speichern an der die Maustaste gedrückt wurde
        scene.setOnMousePressed(event ->
        {
            event.consume();
            mouseX = event.getX();
            mouseY = event.getY();
        });

        // wenn Maustaste losgelassen wird, canvas verschieben
        // !! Koordinaten im Label stimmen dann nicht mehr!!
        scene.setOnMouseDragged(event -> {
            event.consume();
            double mouseNewX = event.getX();
            double mouseNewY = event.getY();
            double deltaX = mouseNewX - mouseX;
            double deltaY = mouseNewY - mouseY;
            canvas.setTranslateX(canvas.getTranslateX() + deltaX);
            canvas.setTranslateY(canvas.getTranslateY() + deltaY);

            canvasFront.setTranslateX(canvas.getTranslateX() + deltaX);
            canvasFront.setTranslateY(canvas.getTranslateY() + deltaY);

            mouseX = mouseNewX;
            mouseY = mouseNewY;
        });

        // Label zur Anzeige der Koordinaten auf denen man sich befindet (funktioniert noch nicht bei Zoom)
        debugCoord = new Label("empty");
        debugCoord.setTranslateX(menuBar.getWidth());
        group.getChildren().

            add(debugCoord);


    }

    public void drawField() {
        clearField();
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gameModel.getTileHashMap().forEach((index, tile) -> {
            VTile tempTileView = new VTile(tile);
            tempTileView.drawBackground(gc);
            if(tile.shouldDraw) {
                tempTileView.drawForeground(gcFront);
            }
            canvas.toBack();

        });

        gameModel.visibleVehiclesArrayList.forEach((vehicle) -> {
            new VVehicle(vehicle, gcFront);
        });
    }

    public void clearField() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gcFront.clearRect(0, 0, canvasFront.getHeight(), canvasFront.getWidth());
    }

    // Kartesische Koordinaten werden zu isometrischen Koordinaten umgerechnet
    public static double[] toIso(double x, double y) {

        double i = (x - y) * tWidthHalf;
        double j = (x + y) * tHeightHalf;

        i += Config.XOffset;
        j += Config.YOffset;

        return new double[]{i, j};
    }

    // isometrische Koordinaten werden zu kartesischen umgerechnet
    public double[] toGrid(double x, double y) {
//        x -= Config.Xoffset;
//        y -= Config.Yoffset;

        double i = ((x / tWidthHalf) + (y / tHeightHalf)) / 2;
        double j = -(((y / tHeightHalf) - (x / tWidthHalf)) / 2 /*-(int)worldWidth + 1*/);

        return new double[]{i, j};
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

    public void showCoordinates(double coordX, double coordY) {

        double[] gridCoordinates = toGrid(coordX, coordY);
        double gridX = gridCoordinates[0];
        double gridY = gridCoordinates[1];

        debugCoord.setText("x: " + (int)gridX + "   y: " + (int)gridY + "     xGrid: " + coordX + "  yGrid: " + coordY);
    }

    public void runTick() {

    }

    public Button getVehicleButton() {
        return vehicleButton;
    }

    public Button getTickButton() {
        return tickButton;
    }

    public Timeline getTl() {
        return tl;
    }
}
