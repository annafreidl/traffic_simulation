package planverkehr;

import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import planverkehr.graph.Graph;
import planverkehr.graph.MKnotenpunkt;
import planverkehr.transportation.EDirections;
import planverkehr.verkehrslinien.MLinie;
import planverkehr.verkehrslinien.VActiveLinie;
import planverkehr.verkehrslinien.VLinie;

import java.util.*;

public class VGame {
    MGame gameModel;
    Stage window;
    Group group;
    Canvas canvas, canvasFront, selectionCanvas;
    GraphicsContext gc, gcFront;
    Scene scene;
    JSONParser parser;
    MenuBar menuBar;
    Label debugCoord;
    Button tickButton, defaultRoad, removeButton, defaultRail, kartengeneratorButton, pauseButton, upButton, downButton, buildButton, saveBuildingButton, playButton, backButton;
    Label linieInfoLabel, savesBuilding;
    Button linienButton, linienButtonWeiter, linienButtonAbbrechen;
    BorderPane bp;
    public double mouseX, mouseY;
    Timeline tl = new Timeline();
    Color color;


    public VGame(MGame gameModel, Stage stage) {
        this.gameModel = gameModel;
        this.window = stage;

        group = new Group();


        parser = new JSONParser();
        String gameMode = parser.getMapFromJSON().getGameMode();

        int requiredCanvasWidth = (int) Math.ceil((Config.worldWidth + Config.worldHeight) * Config.tWidthHalft);
        int requiredCanvasHeight = (int) Math.ceil((Config.worldWidth + Config.worldHeight + 3) * Config.tHeightHalft);

        canvas = new Canvas(requiredCanvasWidth, requiredCanvasHeight);
        canvasFront = new Canvas(requiredCanvasWidth, requiredCanvasHeight);

        double translateX = requiredCanvasWidth / 2;
        double translateY = requiredCanvasHeight / 2;

        canvas.setTranslateX(canvas.getTranslateX() - translateX);
        canvas.setTranslateY(canvas.getTranslateY() - translateY);
        canvasFront.setTranslateX(canvasFront.getTranslateX() - translateX);
        canvasFront.setTranslateY(canvasFront.getTranslateY() - translateY);

        selectionCanvas = new Canvas((Config.worldWidth + 1) * Config.tWidth, (Config.worldHeight + 1) * Config.tHeight);
        gc = canvas.getGraphicsContext2D();
        gcFront = canvasFront.getGraphicsContext2D();
        canvasFront.toFront();
        bp = new BorderPane();
        bp.setLayoutX(20);
        bp.setLayoutY(20);

        group.getChildren().addAll(canvas, canvasFront, bp);


        switch (gameMode) {
            case "planverkehr":
                color = Color.rgb(153, 106, 8);
                break;
            case "own-scenario":
                color = Color.rgb(24, 106, 255);
                break;
            default:
                color = Color.rgb(255, 255, 255);
                break;
        }


        scene = new Scene(group, Config.windowSize + 150, Config.windowSize, color);
        scene.getStylesheets().add("/planverkehr/layout.css");

        window.setTitle("Planverkehr");
        window.setScene(scene);
        window.show();

        StackPane center = new StackPane();
        center.setPrefHeight(window.getHeight() - 150);
        center.setPrefWidth(window.getWidth() - 100);

        createButtons();
        createMenu();

        bp.setCenter(center);
        bp.setTop(createEmptyTopBar());
        bp.setBottom(createBottomBarBeforeStart());

        debugCoord = new Label("empty");
        group.getChildren().add(debugCoord);
        debugCoord.setLayoutX(0);
        debugCoord.setLayoutY(0);

        //drawField();
        drawGeneratedMap();

        scene.setOnScroll(event -> {
            event.consume();
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
            double newTranslateX = canvas.getTranslateX() - fx * dx;
            double newTranslateY = canvas.getTranslateY() - fy * dy;
            canvas.setScaleX(scaleX);
            canvas.setScaleY(scaleY);
            canvas.setTranslateX(newTranslateX);
            canvas.setTranslateY(newTranslateY);

            canvasFront.setScaleX(scaleX);
            canvasFront.setScaleY(scaleY);
            canvasFront.setTranslateX(newTranslateX);
            canvasFront.setTranslateY(newTranslateY);


            //event.consume();
        });

        // Events für drag&drop
        // Position speichern an der die Maustaste gedrückt wurde
        scene.setOnMousePressed(event ->
        {
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
            double newTranslateX = canvas.getTranslateX() + deltaX;
            double newTranslateY = canvas.getTranslateY() + deltaY;

            canvas.setTranslateX(newTranslateX);
            canvas.setTranslateY(newTranslateY);


            canvasFront.setTranslateX(newTranslateX);
            canvasFront.setTranslateY(newTranslateY);
//
//            canvas.setTranslateX(canvas.getTranslateX() + deltaX);
//            canvas.setTranslateY(canvas.getTranslateY() + deltaY);
//
//            canvasFront.setTranslateX(canvas.getTranslateX() + deltaX);
//            canvasFront.setTranslateY(canvas.getTranslateY() + deltaY);

            mouseX = mouseNewX;
            mouseY = mouseNewY;
        });

        //zeigt die Produktion der Gebäude an
        //währenddessen auf keinen fall die maus bewegen
        scene.setOnKeyReleased(event ->
        {
            if (event.getCode() == KeyCode.S) {
                event.consume();
                showProductions();
            }
        });


    }

    private void createButtons() {
        removeButton = new Button();
        Image removeIcon = new Image("Images/delete.png");
        addImage(removeButton, removeIcon);

        defaultRail = new Button();
        defaultRoad = new Button();

        upButton = new Button();
        Image upIcon = new Image("Images/up.png");
        addImage(upButton, upIcon);


        downButton = new Button();
        Image downIcon = new Image("Images/down.png");
        addImage(downButton, downIcon);

        tickButton = new Button();
        linienButton = new Button();
        buildButton = new Button();
        backButton = new Button();
        Image backIcon = new Image("Images/back.png");
        ImageView imgViewBackIcon = new ImageView(backIcon);
        imgViewBackIcon.setFitWidth(35);
        imgViewBackIcon.setFitHeight(35);
        backButton.setGraphic(imgViewBackIcon);
        backButton.getStyleClass().add("button-icon");

        saveBuildingButton = new Button();
        Image saveIcon = new Image("Images/save.png");
        addImage(saveBuildingButton, saveIcon);

        linienButtonWeiter = new Button();
        linienButtonAbbrechen = new Button();

        menuBar = new MenuBar();

    }

    private void addImage(Button button, Image icon) {
        ImageView imgViewRemoveIcon = new ImageView(icon);
        imgViewRemoveIcon.setFitWidth(35);
        imgViewRemoveIcon.setFitHeight(35);
        button.setGraphic(imgViewRemoveIcon);
        button.getStyleClass().add("button-icon");
    }

    private Node createLeft() {
        VBox leftPane = new VBox(5);


        Image linienIcon = new Image("Images/linie.png");
        ImageView imgViewLinienIcon = new ImageView(linienIcon);
        imgViewLinienIcon.setFitWidth(35);
        imgViewLinienIcon.setFitHeight(35);
        linienButton.setGraphic(imgViewLinienIcon);
        linienButton.getStyleClass().add("button-icon");


        Image buildIcon = new Image("Images/build.png");
        ImageView imgViewBuildIcon = new ImageView(buildIcon);
        imgViewBuildIcon.setFitWidth(30);
        imgViewBuildIcon.setFitHeight(30);
        buildButton.setGraphic(imgViewBuildIcon);
        buildButton.getStyleClass().add("button-icon");

        leftPane.getChildren().addAll(buildButton, linienButton);
        leftPane.setAlignment(Pos.CENTER);

        return leftPane;
    }

    private Node createLeftBuildMode() {
        VBox leftPane = new VBox(5);

        savesBuilding = new Label();
        savesBuilding.setText(gameModel.getSavedBuilding());
        // savesBuilding.textProperty().bind(gameModel.getSavedBuilding());


        leftPane.getChildren().addAll(backButton, upButton, downButton, defaultRoad, defaultRail, removeButton, saveBuildingButton, savesBuilding);
        leftPane.setAlignment(Pos.CENTER);
        return leftPane;
    }

    private Node createLeftLinienMode() {
        VBox leftPane = new VBox(5);

        for (MLinie l : gameModel.linienList) {
            Button b = new Button(l.getName() + "(" + l.getVehicle().getName() + ")");
            String webFormat = String.format("#%02x%02x%02x",
                (int) (255 * l.getColor().getRed()),
                (int) (255 * l.getColor().getGreen()),
                (int) (255 * l.getColor().getBlue()));
            b.getStyleClass().add("linienButton");
            b.setStyle(" -fx-border-color:" + webFormat);
            leftPane.getChildren().add(b);

        }

        return leftPane;
    }

    private Node createBottomLinienMode() {
        VBox bottomVPane = new VBox(5);
        HBox bottomPane = new HBox(5);

        linieInfoLabel = new Label("wähle zuerst die zur Linie gehörigen Haltestellen aus, klicke dann auf weiter um ein Fahrzeug zu bestimmen");
        linienButtonAbbrechen.setText("Abbrechen");
        linienButtonWeiter.setText("Weiter");

        bottomPane.getChildren().addAll(linienButtonAbbrechen, linienButtonWeiter);
        bottomVPane.getChildren().addAll(linieInfoLabel, bottomPane);

        return bottomPane;
    }


    private Node createBottomBarBeforeStart() {
        HBox bottomPane = new HBox(5);

        kartengeneratorButton = new Button();
        Image mapIcon = new Image("Images/map.png");
        ImageView imgViewMapIcon = new ImageView(mapIcon);
        imgViewMapIcon.setFitWidth(35);
        imgViewMapIcon.setFitHeight(35);
        kartengeneratorButton.setGraphic(imgViewMapIcon);
        kartengeneratorButton.getStyleClass().add("button-icon");
        kartengeneratorButton.setOnAction(e -> {
            e.consume();
            drawGeneratedMap();
        });

        // tickButton = new Button("Weiter");


        playButton = new Button();
        Image playIcon = new Image("Images/play.png");
        ImageView imgViewPlayIcon = new ImageView(playIcon);
        imgViewPlayIcon.setFitWidth(35);
        imgViewPlayIcon.setFitHeight(35);
        playButton.setGraphic(imgViewPlayIcon);
        playButton.getStyleClass().add("button-icon");
        playButton.getStyleClass().add("button-gameControl");


        bottomPane.getChildren().addAll(playButton, kartengeneratorButton);
        bottomPane.setAlignment(Pos.CENTER);

        return bottomPane;

    }

    private Node createBottomBarForGame() {
        HBox bottomPane = new HBox(5);
        GridPane grid = new GridPane();

        Image weiterIcon = new Image("Images/weiter.png");
        ImageView imgViewWeiterIcon = new ImageView(weiterIcon);
        imgViewWeiterIcon.setFitWidth(30);
        imgViewWeiterIcon.setFitHeight(30);
        tickButton.setGraphic(imgViewWeiterIcon);
        tickButton.getStyleClass().add("button-icon");

        pauseButton = new Button();
        Image stopIcon = new Image("Images/stop.png");
        ImageView imgViewStopIcon = new ImageView(stopIcon);
        imgViewStopIcon.setFitWidth(30);
        imgViewStopIcon.setFitHeight(30);
        pauseButton.setGraphic(imgViewStopIcon);
        pauseButton.getStyleClass().add("button-icon");

        pauseButton.setOnAction(event -> gameModel.timeline.pause());

        Image playIcon = new Image("Images/pause.png");
        ImageView imgViewPlayIcon = new ImageView(playIcon);
        imgViewPlayIcon.setFitWidth(35);
        imgViewPlayIcon.setFitHeight(35);
        playButton.setGraphic(imgViewPlayIcon);
        playButton.getStyleClass().add("button-icon");
        playButton.getStyleClass().add("button-gameControl");

        Slider slider = new Slider();
        grid.add(slider, 0, 1, 2, 1);
        slider.setMin(0.1);
        slider.setMax(2);
        slider.setValue(1);
        slider.setMajorTickUnit(0.2);
        slider.setMinorTickCount(1);
        slider.setBlockIncrement(0.1);
        slider.setSnapToTicks(true);
        slider.setShowTickLabels(true);

        Label tickText = new Label("Velocity: ");
        grid.add(tickText, 0, 0);

        Label output = new Label();
        grid.add(output, 1, 0);

        output.textProperty().bind(slider.valueProperty().asString());

        //todo: muss im model geändert werden
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                Duration tickFrequency = Duration.seconds((double) t1);
            }
        });

        bottomPane.getChildren().addAll(grid, playButton, tickButton, pauseButton);
        bottomPane.setAlignment(Pos.CENTER);

        return bottomPane;

    }

    private Node createEmptyTopBar() {

        HBox emptyTop = new HBox();
        emptyTop.setPrefHeight(40);
        emptyTop.setPrefWidth(175);
        return emptyTop;
    }

    private void createMenu() {
        Menu airportMenu = new Menu();
        Image airportImage = new Image("Images/flughafen.png");
        ImageView airportImgView = new ImageView(airportImage);
        airportImgView.setFitHeight(30);
        airportImgView.setFitWidth(30);
        airportMenu.setGraphic(airportImgView);
        airportMenu.setId(String.valueOf(EBuildType.airport));

        Menu railMenu = new Menu();
        Image railImage = new Image("Images/schienen.png");
        ImageView railImgView = new ImageView(railImage);
        railImgView.setFitHeight(30);
        railImgView.setFitWidth(30);
        railMenu.setGraphic(railImgView);
        railMenu.setId(String.valueOf(EBuildType.rail));

        Menu roadsMenu = new Menu();
        Image roadsImage = new Image("Images/straße.png");
        ImageView roadImgView = new ImageView(roadsImage);
        roadImgView.setFitHeight(30);
        roadImgView.setFitWidth(30);
        roadsMenu.setGraphic(roadImgView);
        roadsMenu.setId(String.valueOf(EBuildType.road));
        Menu nature = new Menu();
        Image natureImage = new Image("Images/nature.png");
        ImageView imgView = new ImageView(natureImage);
        imgView.setFitHeight(30);
        imgView.setFitWidth(30);
        nature.setGraphic(imgView);
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
                    roadsMenu.getItems().add(m1);
                    break;
                case "rail":
                    directionsMap = b.getPoints();
                    List<Pair<String, String>> l = b.getRails();
                    VRailMenu menuRailImage = new VRailMenu(directionsMap, l);
                    m1.setGraphic(menuRailImage);
                    railMenu.getItems().add(m1);
                    break;
                case "airport":
                    airportMenu.getItems().add(m1);
                    break;
                case "nature":
                    nature.getItems().add(m1);
                    break;
                default:
                    break;
            }
        });

        menuBar.getMenus().addAll(airportMenu, railMenu, roadsMenu, nature);

    }

    private Node createTopBar() {

        HBox topPane = new HBox();
        topPane.getChildren().addAll(menuBar);
        topPane.setAlignment(Pos.CENTER);

        return topPane;
    }


    private void drawGeneratedMap() {
        gameModel.clearLists();

        //ebne das Spielfeld
        ebneMap();

        //setze die Factories auf das Spielfeld

        LinkedList<Buildings> openList = new LinkedList();

        gameModel.getBuildingsList().forEach((key, b) -> {
            if (b.getSpecial().equals("factory")) {
                openList.add(b);
            }
        });

        while (!openList.isEmpty()) {

            Buildings b = openList.pollFirst();

            int xId = generateRandomInt(Config.worldWidth - b.getWidth()) + 1;
            int yId = generateRandomInt(Config.worldHeight - b.getDepth()) + 1;
            String randomId = xId + "--" + yId;
            if (yId == 0) {
                randomId = xId + "-" + yId;
            }

            MTile t = gameModel.getTileById(randomId);

            ArrayList<MTile> mitbesetzte = gameModel.getTilesToBeGroupedFactorie(b, t);

            boolean allemitbesetztenfrei = true;
            if (mitbesetzte != null) {

                for (MTile mitbesetzt : mitbesetzte) {
                    if (!mitbesetzt.isFree()) {
                        allemitbesetztenfrei = false;

                    }

                    for (MTile nachbar : gameModel.getNeighbours(mitbesetzt)) {
                        if (!nachbar.isFree()) {
                            if (nachbar.getBuildingOnTile().getBuildType() == EBuildType.factory) {
                                allemitbesetztenfrei = false;
                            }
                        }
                    }
                }
                if (!t.isFree()) {
                    allemitbesetztenfrei = false;

                }
            }

            boolean nachbarnhabenhäuser = true;
            for (MTile nachbar : gameModel.getNeighbours(t)) {
                if (!nachbar.isFree()) {
                    nachbarnhabenhäuser = false;
                    if (nachbar.getBuildingOnTile().getBuildType() == EBuildType.factory || nachbar.getBuildingOnTile().getBuildType() == EBuildType.cathedral) {
                        nachbarnhabenhäuser = false;
                    }
                }
            }

            if (t.isFree() && nachbarnhabenhäuser && allemitbesetztenfrei) {
                t.setState(EBuildType.factory);
                t.setBuildingOnTile(b);
                t.addConnectedBuilding(b);
                MCoordinate buildingCoord = new MCoordinate(xId + 0.5, yId, 0);
                MKnotenpunkt buildingNode = new MKnotenpunkt(buildingCoord.toString(), b.getBuildingName(), buildingCoord, b.getBuildType(), b.getBuildingName(), randomId, EDirections.EMPTY, true);
                t.addKnotenpunkt(buildingNode);
                MFactory f = new MFactory(b);
                f.setKnotenpunkt(buildingNode);
                f.setHaltestelle(gameModel.createFactoryStation(t, f));

                gameModel.addFactoryToConstructedFactories(f);


                if (mitbesetzte != null) {
                    for (MTile mitbesetzt : mitbesetzte) {
                        if (f.getBuildType().equals(EBuildType.cathedral)) {
                            mitbesetzt.setState(EBuildType.cathedral);
                        } else {
                            mitbesetzt.setState(EBuildType.factory);
                        }
                        mitbesetzt.setBuildingOnTile(b);
                        mitbesetzt.addConnectedBuilding(b);
                    }
                }

                if (f.getBuildType().equals(EBuildType.cathedral)) {
                    t.setState(EBuildType.cathedral);
                }
            } else {
                System.out.println("Building wird nochmal gezeichnet: " + b);
                openList.add(b);
            }

        }

        // erhöhe/erniedrige die Tiles mit einer bestimmten Wahrscheinlichkeit
        gameModel.getTileArray().forEach((tile) -> {
            int wirdhöhenrandom = generateRandomInt(40);
            int erhöhtodervertieft = generateRandomInt(2);
            int wiehoch = generateRandomInt(3);
            //wird erhöht mit Wahrscheinlichkeit von 30%
            if (wirdhöhenrandom < 2) {

                if (erhöhtodervertieft == 0) {

                    boolean nachbarlevelniedrigeralseins = true;
                    for (MTile m : gameModel.getNeighbours(tile)) {
                        if (m.getIncline()) {
                            nachbarlevelniedrigeralseins = false;
                        }
                    }
                    if (nachbarlevelniedrigeralseins) {
                        for (int i = 0; i <= wiehoch; i++) {
                            setHigh(tile, true);
                        }
                    }

                } else {
                    if (wirdhöhenrandom < 1) {
                        boolean nachbarlevelniedrigeralseins = true;
                        for (MTile m : gameModel.getNeighbours(tile)) {
                            if (tile.getLevel() > 0) {
                                nachbarlevelniedrigeralseins = false;
                            }
                        }
                        if (nachbarlevelniedrigeralseins) {
                            setHigh(tile, false);
                        }
                    }
                }
            }
            tile.createHöhenArray();
        });

        gameModel.getBuildingsList().forEach((key, b) -> {

            if (b.getSpecial().equals("nature")) {

                int naturenumber = Math.round(Config.worldWidth * Config.worldHeight / 50);
                for (int i = 0; i < naturenumber; i++) {

                    Graph relevantGraph = gameModel.gameGraph;

                    int xId = generateRandomInt(Config.worldWidth - b.getWidth());
                    int yId = generateRandomInt(Config.worldHeight - b.getDepth());
                    String randomId = xId + "--" + yId;
                    if (yId == 0) {
                        randomId = xId + "-" + yId;
                    }

                    MTile t = gameModel.getTileById(randomId);

                    if (t.isFree()) {
                        t.setState(EBuildType.nature);
                        t.setBuildingOnTile(b);
                        t.addConnectedBuilding(b);
                    }
                }

            }
        });
        drawField();

    }

    public static int generateRandomInt(int i) {
        Random random = new Random();
        return random.nextInt(i);
    }

    public void updateSavedBuilding() {
        savesBuilding.setText(gameModel.getSavedBuilding());
    }

    private void ebneMap() {

        gameModel.getTileArray().forEach((tile) -> {

            for (MCoordinate m : tile.getPunkteNeu()) {
                m.setZ(0);
                tile.createHöhenArray();
            }
            tile.reset();
        });
    }

                /* Idee: von jedem Tile auf dem Spielbrett werden die Eckpunkte ausgelesen
            und die Schnittmenge mit den Eckpunkten des angeklickten Tiles erstellt.
            Ist die Schnittmenge leer passiert nichts, weil das Tile dann kein Nachbar vom angeklickten Tile ist,
            ist die Schnittmenge jedoch nicht leer, so werden die Punkte in der Schnittmenge für
            das jeweilige Tile um einen Höhenschritt erhöht bzw. abgesenkt.
             */

    //verändert die Höhe der Tiles und setzt den y-Wert nach oben
    public void setHigh(MTile t, boolean richtung) {

        // wenn richtung = true: Anheben des Bodens, also Faktor positiv
        // wenn richtung = false: Absenken des Bodens, also Faktor negativ
        int factor = 1;
        if (!richtung) {
            factor = -1;
        }

        boolean nachbaristwasser = true;

        if (t.getLevel() == 0 && factor < 0) {
            for (MTile nachbart : gameModel.getNeighbours(t)) {
                for (int höhe : nachbart.höhen) {
                    if (höhe > 0) {
                        nachbaristwasser = false;
                        break;
                    }
                }
            }
        } else if (t.getLevel() == 0 && factor > 0) {
            for (MTile nachbart : gameModel.getNeighbours(t)) {
                for (int höhe : nachbart.höhen) {
                    if (höhe < 0) {
                        nachbaristwasser = false;
                        break;
                    }
                }
            }
        } else if (t.getLevel() == 1 && factor > 0) {
            for (MTile nachbart : gameModel.getNeighbours(t)) {
                for (MTile nachbar2 : gameModel.getNeighbours(nachbart)) {
                    for (int höhe : nachbar2.höhen) {
                        if (höhe < 0) {
                            nachbaristwasser = false;
                            break;
                        }
                    }
                }
            }
        }


        //bei factor 1 auch level <0
        if (!t.getIncline() && ((factor > 0 && t.getLevel() < 2) || factor < 0 && t.getLevel() >= 0) && nachbaristwasser) {

            HashMap<MTile, ArrayList<MCoordinate>> erhöheAmEnde = new HashMap<>();
            ArrayList<MTile> bereitserhöht = new ArrayList<>();
            LinkedList<MTile> openList = new LinkedList<>();
            openList.add(t);

            while (!openList.isEmpty()) {

                openList.sort(Comparator.comparingInt(current
                    -> current.höhendif().stream().mapToInt(Integer::intValue).sum()));
                Collections.reverse(openList);
                MTile currentMittelPunkt = openList.pollFirst();
                bereitserhöht.add(currentMittelPunkt);

                assert currentMittelPunkt != null;
                erhöheAmEnde.put(currentMittelPunkt, currentMittelPunkt.getPunkte());

                ArrayList<MTile> nachbarnsortiert = gameModel.getNeighbours(currentMittelPunkt);

                nachbarnsortiert.sort(Comparator.comparingInt(nachbar -> nachbar.intersection(currentMittelPunkt).size()));
                Collections.reverse(nachbarnsortiert);

                for (MTile currentNachbar : nachbarnsortiert) {

                    if (!bereitserhöht.contains(currentNachbar)) {

                        switch (currentMittelPunkt.höhendif().stream().mapToInt(Integer::intValue).sum()) {
                            case 2 -> {
                                if (currentNachbar.intersection(currentMittelPunkt).size() == 2
                                ) {
                                    addTilesToErhöhenAmEndeArray(factor, erhöheAmEnde, bereitserhöht, openList, currentMittelPunkt, currentNachbar);
                                }
                            }
                            case 0, 1, 3 -> addTilesToErhöhenAmEndeArray(factor, erhöheAmEnde, bereitserhöht, openList, currentMittelPunkt, currentNachbar);
                        }
                    }
                }
            }

            boolean darferhöhtwerden = true;
            for (MTile key : erhöheAmEnde.keySet()) {
                if (!(key.getState() == EBuildType.free || key.getState() == EBuildType.water)) {
                    darferhöhtwerden = false;
                    break;
                }
            }

            // ACHTUNG: hier wird nicht das Tile selbst verändert!
            if (darferhöhtwerden) {
                for (MTile key : erhöheAmEnde.keySet()) {

                    key.erhöhePunkte(erhöheAmEnde.get(key), factor);
                    key.createHöhenArray();
                    key.höhendif();
                    if (key.getLevel() >= 0 && key.getState() == EBuildType.water) {
                        key.setState(EBuildType.free);
                    }
                    key.setHoch(richtung);
                }
            }

        }

        // wenn Feld nicht eben/erhöhbar, gebe aus:
        else System.out.println("Feld ist zu schief");
    }

    private void addTilesToErhöhenAmEndeArray(int factor, HashMap<MTile, ArrayList<MCoordinate>> erhöheAmEnde, ArrayList<MTile> bereitserhöht, LinkedList<MTile> openList, MTile currentMittelPunkt, MTile currentNachbar) {
        bereitserhöht.add(currentNachbar);
        ArrayList<MCoordinate> same = currentNachbar.intersection(currentMittelPunkt);
        ArrayList<MCoordinate> other = entferne(currentNachbar.getPunkte(), currentNachbar.intersection(currentMittelPunkt));
        for (MCoordinate o : other) {
            ArrayList<MCoordinate> zuerhöhende = new ArrayList<>();
            if ((factor > 0 && currentNachbar.getMeZ(o) < currentNachbar.getMeZ(same.get(0))) ||
                (factor < 0 && currentNachbar.getMeZ(o) > currentNachbar.getMeZ(same.get(0)))) {
                openList.add(currentNachbar);
                zuerhöhende.add(o);
            }
            erhöheAmEnde.put(currentNachbar, same);
            if (!zuerhöhende.isEmpty()) {
                ArrayList<MCoordinate> merge = erhöheAmEnde.get(currentNachbar);
                for (MCoordinate erhöhe : zuerhöhende) {
                    if (!merge.contains(erhöhe)) {
                        merge.add(erhöhe);
                    }
                }
                erhöheAmEnde.put(currentNachbar, merge);
            }
        }
    }

    //Differenz
    public ArrayList<MCoordinate> entferne(ArrayList<MCoordinate> list1, ArrayList<MCoordinate> list2) {
        ArrayList<MCoordinate> übrige = new ArrayList<>();

        for (MCoordinate t : list1) {
            for (MCoordinate p : list2) {
                if (!(t.istGleich(p))) {
                    übrige.add(t);
                }
            }
        }
        return übrige;
    }

    //Schnittmenge zweier ArrayLists
    public ArrayList<MCoordinate> intersection(ArrayList<MCoordinate> list1, ArrayList<MCoordinate> list2) {
        ArrayList<MCoordinate> list = new ArrayList<>();

        for (MCoordinate t : list1) {
            for (MCoordinate p : list2) {
                if (t.istGleich(p)) {
                    list.add(t);
                }
            }
        }
        return list;
    }


    //zeichne das Spielfeld neu
    public void drawField() {
        clearField();
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        HashMap<String, MTile> edgeTiles = new HashMap<>();

//        for (int i = 0; i < 4; i++) {
//            double x = i % 2 == 0 ? 0 : window.getHeight();
//            double y = i % 2 == 0 ? 0 : window.getWidth();
//
//            Point2D canvasPoint = canvas.sceneToLocal(x, y);
//            x = canvasPoint.getX();
//            y = canvasPoint.getY();
//
//            MCoordinate tileCoord = gameModel.getVisibleCoordsByCanvasCoords(x,y);
//
//            if(tileCoord.getX() > Config.worldWidth){
//                tileCoord.setX(Config.worldWidth);
//            } else if (tileCoord.getX() <0){
//                tileCoord.setX(0);
//            }
//
//            if(tileCoord.getY() > Config.worldHeight){
//                tileCoord.setY(Config.worldHeight);
//            } else if (tileCoord.getY() <0){
//                tileCoord.setY(0);
//            }
//
//
//            MTile temp = gameModel.getTileById(tileCoord.toIntStringCoordinates());
//
//            if(temp == null){
//                x = i % 2 == 0 ? 0 : Config.worldWidth;
//                y = i % 2 == 0 ? 0 : Config.worldHeight;
//                if(y == 0){
//                    temp = gameModel.getTileById((int)x +"-" + (int)y);
//                } else {
//                    temp = gameModel.getTileById((int) x + "--" + (int) y);
//                }
//            }
//
//            edgeTiles.put(temp.getId(), temp);
//        }

        ArrayList<MTile> visibleTileArray = new ArrayList<>();


        gameModel.getTileArray().forEach((tile) -> {
            VTile tempTileView = new VTile(tile);
            tempTileView.drawBackground(gc);
            if (tile.isFirstTile) {
                tempTileView.drawForeground(gcFront);
            }


            canvas.toBack();

        });

        if (gameModel.isCreateLine()) {
            gameModel.activeLinie.getListOfHaltestellenKnotenpunkten().forEach(wp -> new VActiveLinie(wp, gcFront, true, gameModel.activeLinie.getColor()));

            int i = 0;

            for (MLinie l : gameModel.linienList) {
                new VLinie(gcFront, group, l, i);
                i++;
            }

        }

        gameModel.visibleVehiclesArrayList.forEach((vehicle) -> new VVehicle(vehicle, gcFront));
    }

    public void drawChangedTiles(TreeSet<MTile> changedTiles) {
        changedTiles.forEach(tile -> {
            clearTiles(tile);
        });


        TreeSet<MTile> neighbourTiles = new TreeSet<>();
        //clearField();
        changedTiles.forEach(mTile -> {
            neighbourTiles.addAll(gameModel.getNeighboursOfSecondOrder(mTile));
        });

        changedTiles.addAll(neighbourTiles);


        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        changedTiles.forEach((tile) -> {
            VTile tempTileView = new VTile(tile);
            tempTileView.drawBackground(gc);
            if (tile.isFirstTile) {
                tempTileView.drawForeground(gcFront);
            }
            if (!tile.isFree()) {
                canvas.toBack();
            }

        });

        if (gameModel.isCreateLine()) {
            gameModel.activeLinie.getListOfHaltestellenKnotenpunkten().forEach(wp -> new VActiveLinie(wp, gcFront, true, gameModel.activeLinie.getColor()));

            int i = 0;

            for (MLinie l : gameModel.linienList) {
                new VLinie(gcFront, group, l, i);
                i++;
            }

        }
        gameModel.visibleVehiclesArrayList.forEach((vehicle) -> new VVehicle(vehicle, gcFront));
    }

    public void clearField() {

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gcFront.clearRect(0, 0, canvasFront.getWidth(), canvasFront.getHeight());
    }

    public void clearTiles(MTile tile) {

        double schraege;
        MCoordinate westRelativ = tile.punkteNeu.get(3);
        MCoordinate westVisible = tile.getVisibleCoordinates();
        MCoordinate westAbsolut = new MCoordinate(westVisible.getX() + westRelativ.getX(), westVisible.getY() + westRelativ.getY(), westRelativ.getZ());
        boolean gehtHoch = tile.punkteNeu.get(3).getZ() != tile.getLevel();
        double westAbsolutX = westAbsolut.getX();
        double westAbsolutY = westAbsolut.getY();
        double westAbsolutZ = westAbsolut.getZ();

        if (tile.isSchraeg()) {
            schraege = gehtHoch ? westAbsolutZ - 0.5 : westAbsolutZ + 0.5;
        } else {
            schraege = westAbsolutZ;
        }

        MCoordinate centerCoord = new MCoordinate(westAbsolutX, westAbsolutY, schraege).toCanvasCoordWithoutOffset();

        gcFront.clearRect(centerCoord.getX(), centerCoord.getY() - centerCoord.getZ() - Config.tHeightHalft - Config.tHeightHalft / 2, Config.tWidth, Config.tHeight + Config.tHeightHalft);
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }


    public Scene getScene() {
        return scene;
    }

    public void showCoordinates(double coordX, double coordY) {
        MCoordinate canvasCoord = new MCoordinate(coordX, coordY, 0);
        MCoordinate visibleCoord = canvasCoord.toVisibleCoord();
        debugCoord.setText("x: " + visibleCoord.getX() + "   y: " + visibleCoord.getY() + "     xGrid: " + coordX + "  yGrid: " + coordY);
    }

    public Button getLinienButton() {
        return linienButton;
    }

    public Button getSaveBuildingButton() {
        return saveBuildingButton;
    }

    public Button getTickButton() {
        return tickButton;
    }

    public void toggleLinienInfoLabel(boolean shouldShow) {
        if (shouldShow) {

            bp.setLeft(createLeftLinienMode());
            bp.setBottom(createBottomLinienMode());

        } else {
            bp.setLeft(createLeft());
            bp.setBottom(createBottomBarForGame());
        }
    }

    public Button getDefaultRoadButton() {
        return defaultRoad;
    }

    public Button getRemoveButton() {
        return removeButton;
    }

    public Button getDefaultRailButton() {
        return defaultRail;
    }

    public Button getLinienButtonAbbrechen() {
        return linienButtonAbbrechen;
    }

    public Button getLinienButtonWeiter() {
        return linienButtonWeiter;
    }

    public void showProductions() {
        if (gameModel.getSelectedTile() != null &&
            (gameModel.getSelectedTile().getState().equals(EBuildType.factory) ||
                gameModel.getSelectedTile().getState().equals(EBuildType.cathedral) ||
                gameModel.getSelectedTile().getState().equals(EBuildType.cathedral_foundation))) {
            MTile mTile = gameModel.getSelectedTile();
            Buildings buildings = new Buildings(mTile.getConnectedBuilding());
            VBox box = new VBox();
            group.getChildren().add(box);
            box.setLayoutX(300);
            box.setLayoutY(0);
            box.setAlignment(Pos.BASELINE_CENTER);
            box.setStyle("-fx-background-color : lightgreen;");

            if (mTile.getConnectedBuilding() != null &&
                (mTile.getState().equals(EBuildType.factory) ||
                    mTile.getState().equals(EBuildType.cathedral) ||
                    mTile.getState().equals(EBuildType.cathedral_foundation))) {
                Label factory = new Label("Factory: " + buildings.getBuildingName());
                box.getChildren().add(factory);
                for (int i = 0; i < buildings.getProductions().size(); i++) {

                    if (!buildings.getProductions().get(i).produce.isEmpty()) {
                        Label produce = new Label("Produce: " + buildings.getProductions().get(i).produce.toString());
                        box.getChildren().add(produce);
                    }
                    if (!buildings.getProductions().get(i).consume.isEmpty()) {
                        Label consume = new Label("Consume: " + buildings.getProductions().get(i).consume.toString());
                        box.getChildren().add(consume);
                    }
                    if (!buildings.getProductions().get(i).produceStorage.isEmpty()) {
                        Label warehouse = new Label("Warehouse: " + buildings.getProductions().get(i).produceStorage.toString());
                        box.getChildren().add(warehouse);
                    }
                    if (!buildings.getProductions().get(i).consume.isEmpty() && !buildings.getProductions().get(i).produce.isEmpty()) {
                        Label duration = new Label("Duration: " + buildings.getProductions().get(i).duration);
                        box.getChildren().add(duration);
                    }

                    if (!buildings.getProductions().get(i).storage.isEmpty()) {
                        Label storage = new Label("Storage: " + buildings.getProductions().get(i).storage.toString());
                        box.getChildren().add(storage);
                    }
                }
            }
            scene.setOnKeyPressed(event ->
            {
                if (event.getCode() == KeyCode.S) {
                    event.consume();
                    box.getChildren().clear();
                }
            });
        }
    }

    public Button getPlayButton() {
        return playButton;
    }

    public Button getBuildButton() {
        return buildButton;
    }

    public void togglePlayPause() {
        Image playIcon;
        if (gameModel.gamePaused) {
            playIcon = new Image("Images/play.png");
        } else {
            playIcon = new Image("Images/pause.png");
        }
        ImageView imgViewPlayIcon = new ImageView(playIcon);
        imgViewPlayIcon.setFitWidth(35);
        imgViewPlayIcon.setFitHeight(35);
        playButton.setGraphic(imgViewPlayIcon);
    }

    public void startGame() {
        bp.setBottom(createBottomBarForGame());
        bp.setLeft(createLeft());
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getUpButton() {
        return upButton;
    }

    public Button getDownButton() {
        return downButton;
    }

    public void toggleBuildingMode() {
        if (gameModel.isBuilding) {
            bp.setTop(createTopBar());
            bp.setLeft(createLeftBuildMode());
        } else {
            bp.setTop(null);
            bp.setLeft(createLeft());
        }
    }

    public void showNormalView() {
        bp.setTop(createEmptyTopBar());
        bp.setLeft(createLeft());
        bp.setBottom(createBottomBarForGame());
    }

    public void toggleAutoSave() {
        Image saveIcon;
        if (gameModel.autoSaveMode) {
            saveIcon = new Image("Images/save.png");
        } else {
            saveIcon = new Image("Images/notSaving.png");
        }
        ImageView imgViewPlayIcon = new ImageView(saveIcon);
        imgViewPlayIcon.setFitWidth(35);
        imgViewPlayIcon.setFitHeight(35);
        saveBuildingButton.setGraphic(imgViewPlayIcon);
        updateSavedBuilding();
    }
}