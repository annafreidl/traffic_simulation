package planverkehr;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.util.Duration;
import javafx.util.Pair;
import planverkehr.graph.Graph;
import planverkehr.transportation.MTransportConnection;

import java.text.DecimalFormat;
import java.util.*;
import static planverkehr.Controller.*;

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
    Button vehicleButton, tickButton, defaultRoad, removeButton, defaultRail, clearButton, drawButton, kartengeneratorButton;

    MouseMode mouseMode;

    public void setMouseMode(MouseMode mouseMode) {
        this.mouseMode = mouseMode;
    }

    public MouseMode getMouseMode() {
        return mouseMode;
    }

    public double mouseX, mouseY;
    static double tWidth = Config.tWidth;
    static double tHeight = Config.tHeight;
    static double tHeightHalf = Config.tHeightHalft;
    static double tWidthHalf = Config.tWidthHalft;
    static double worldWidth = Config.worldWidth;
    static double worldHeight = Config.worldHeight;
    Timeline tl = new Timeline();

    static double increase = Config.increase;


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

        Button upButton = new Button("up");
        group.getChildren().add(upButton);
        upButton.setLayoutX(0);
        upButton.setLayoutY(90);
        upButton.setOnAction(event -> runUp());

        Button downButton = new Button("down");
        group.getChildren().add(downButton);
        downButton.setLayoutX(0);
        downButton.setLayoutY(120);
        downButton.setOnAction(event -> runDown());

        tickButton = new Button("Tick");
        group.getChildren().add(tickButton);
        tickButton.setLayoutX(0);
        tickButton.setLayoutY(30);

        defaultRoad = new Button("Straßenbeispiel");
        group.getChildren().add(defaultRoad);
        defaultRoad.setLayoutX(510);
        defaultRoad.setLayoutY(0);

        defaultRail = new Button("Schienenbeispiel");
        group.getChildren().add(defaultRail);
        defaultRail.setLayoutX(510);
        defaultRail.setLayoutY(30);

        removeButton = new Button("Löschen");
        group.getChildren().add(removeButton);
        removeButton.setLayoutX(640);
        removeButton.setLayoutY(0);

        vehicleButton = new Button("Road Vehicle");
        group.getChildren().add(vehicleButton);
        vehicleButton.setLayoutX(0);
        vehicleButton.setLayoutY(150);

        clearButton = new Button("clear");
        group.getChildren().add(clearButton);
        clearButton.setLayoutX(640);
        clearButton.setLayoutY(30);
        clearButton.setOnAction(e -> clearField());

        drawButton = new Button("draw");
        group.getChildren().add(drawButton);
        drawButton.setLayoutX(640);
        drawButton.setLayoutY(60);
        drawButton.setOnAction(e -> drawField());

        kartengeneratorButton = new Button("Kartengenerator");
        group.getChildren().add(kartengeneratorButton);
        kartengeneratorButton.setLayoutX(600);
        kartengeneratorButton.setLayoutY(650);
        kartengeneratorButton.setOnAction(e -> {
            e.consume();
            drawGeneratedMap();
        });


        Slider slider = new Slider();
        group.getChildren().add(slider);
        slider.setMin(0.1);
        slider.setMax(2);
        slider.setValue(1);
        slider.setLayoutX(0);
        slider.setLayoutY(650);
        slider.setMajorTickUnit(0.2);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);


        Label tickText = new Label("Tick:");
        group.getChildren().add(tickText);
        tickText.setLayoutX(0);
        tickText.setLayoutY(670);


        Label output = new Label("Ausgabe");
        group.getChildren().add(output);


        output.textProperty().bind(slider.valueProperty().asString());

        output.setLayoutX(25);
        output.setLayoutY(670);

        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                Duration tickFrequency = Duration.seconds((double) t1);
            }
        });

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
            canvas.setScaleX(scaleX);
            canvas.setScaleY(scaleY);
            canvas.setTranslateX(canvas.getTranslateX() - fx * dx);
            canvas.setTranslateY(canvas.getTranslateY() - fy * dy);

            canvasFront.setScaleX(scaleX);
            canvasFront.setScaleY(scaleY);
            canvasFront.setTranslateX(canvas.getTranslateX() - fx * dx);
            canvasFront.setTranslateY(canvas.getTranslateY() - fy * dy);


            //event.consume();
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

    private void drawGeneratedMap() {

        ebneMap();

        gameModel.getBuildingsList().forEach((key, b) -> {

            if (b.getSpecial().equals("factory")) {

                Graph relevantGraph = gameModel.gameGraph;

                int xId = generateRandomInt(Config.worldHeight-b.getWidth());
                int yId = generateRandomInt(Config.worldWidth-b.getDepth());
                String randomId = xId + "--" + yId;
                if(yId==0){
                    randomId = xId + "-" + yId;
                }

                MTile t = gameModel.getTileById(randomId);
                ArrayList<MTile> mitbesetzte = gameModel.getTilesToBeGroupedFactorie(b,t);

//                if(mitbesetzte != null){
//
//                    for(MTile mitbesetzt: mitbesetzte){
//                        if(mitbesetzt.isFree()){
//                            mitbesetzt.setState(EBuildType.factory);
//                            //mitbesetzt.setBuildingOnTile(b);
//                        }
//                    }
//                }
                if(t.isFree()){
                    t.setState(EBuildType.factory);
                    t.setBuildingOnTile(b);
                }

            }
        });
        //System.out.println("Anzahl Factories: " + countfactories);

        gameModel.getTileArray().forEach((tile) -> {
            int wirdhöhenrandom = generateRandomInt(40);
            int erhöhtodervertieft = generateRandomInt(2);
            int wiehoch = generateRandomInt(3);
            //wird erhöht mit Wahrscheinlichkeit von 30%
            if(wirdhöhenrandom<2){

                if(erhöhtodervertieft == 0){

                    boolean nachbarlevelniedrigeralseins = true;
                    for(MTile m : gameModel.getNeighbours(tile)){
                        if(m.getIncline()){
                            nachbarlevelniedrigeralseins = false;
                        }
                    }
                    if(nachbarlevelniedrigeralseins){
                        for(int i = 0; i<=wiehoch; i++){
                            setHigh(tile, true);
                        }
                    }

                }
                else {
                    if (wirdhöhenrandom < 1) {
                        boolean nachbarlevelniedrigeralseins = true;
                        for (MTile m : gameModel.getNeighbours(tile)) {
                            if (m.getIncline() || tile.getLevel() > 0) {
                                nachbarlevelniedrigeralseins = false;
                            }
                        }
                        if (nachbarlevelniedrigeralseins) {
                            setHigh(tile, false);
                        }
                    }
                }
            }
            tile.createCreateHoehenArray();
            VTile tempTileView = new VTile(tile);
            tempTileView.drawBackground(gc);
        });
        drawField();

    }

    public static int generateRandomInt(int i) {
        Random random = new Random();
        return random.nextInt(i);
    }

    private void ebneMap() {

        clearField();
        gameModel.getTileArray().forEach((tile) -> {
            tile.reset();
            for(MCoordinate m: tile.getPunkteNeu()){
                m.setZ(0);
                tile.createCreateHoehenArray();
            }
            VTile tempTileView = new VTile(tile);
            tempTileView.drawBackground(gc);
            if (tile.getState()==EBuildType.water){
                tile.setState(EBuildType.free);
            }
            });
        drawField();

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

        if(t.getLevel()==0 && factor<0){
            for(MTile nachbart: gameModel.getNeighbours(t)){
                for(int höhe : nachbart.höhen){
                    if(höhe>0){
                        nachbaristwasser = false;
                    }
                }
            }
        }

        if(t.getLevel()==0&&factor>0){
            for(MTile nachbart: gameModel.getNeighbours(t)){
                for(int höhe : nachbart.höhen){
                    if(höhe<0){
                        nachbaristwasser = false;
                    }
                }
            }
        }

        if(t.getLevel()==1&&factor>0){
            for(MTile nachbart: gameModel.getNeighbours(t)){
                for(MTile nachbar2 : gameModel.getNeighbours(nachbart)){
                    for(int höhe : nachbar2.höhen){
                        if(höhe<0){
                            nachbaristwasser = false;
                        }
                    }
                }
            }
        }


        //bei factor 1 auch level <0
        if (!t.getIncline()&&((factor>0 && t.getLevel()<2)||factor<0 && t.getLevel()>=0)&&nachbaristwasser) {

            HashMap<MTile, ArrayList> erhöheAmEnde = new HashMap<>();
            ArrayList<MTile> bereitserhöht = new ArrayList();
            LinkedList<MTile> openList = new LinkedList();
            openList.add(t);

            while(!openList.isEmpty()) {

                Collections.sort(openList, Comparator.comparingInt(current
                    -> current.höhendif().stream().mapToInt(Integer::intValue).sum()));
                Collections.reverse(openList);
                MTile currentMittelPunkt = openList.pollFirst();
                bereitserhöht.add(currentMittelPunkt);

                    erhöheAmEnde.put(currentMittelPunkt, currentMittelPunkt.getPunkte());

                    ArrayList<MTile> nachbarnsortiert = gameModel.getNeighbours(currentMittelPunkt);

                    Collections.sort(nachbarnsortiert, Comparator.comparingInt(nachbar -> nachbar.intersection(currentMittelPunkt).size()));
                    Collections.reverse(nachbarnsortiert);

                    for (MTile currentNachbar : nachbarnsortiert) {

                        if (!bereitserhöht.contains(currentNachbar)) {

                            switch (currentMittelPunkt.höhendif().stream().mapToInt(Integer::intValue).sum()) {
                                case 2: {
                                    if (currentNachbar.intersection(currentMittelPunkt).size() == 2
                                    ) {
                                        bereitserhöht.add(currentNachbar);
                                        ArrayList<MCoordinate> same = currentNachbar.intersection(currentMittelPunkt);
                                        ArrayList<MCoordinate> other = entferne(currentNachbar.getPunkte(), currentNachbar.intersection(currentMittelPunkt));
                                        for (MCoordinate o : other) {
                                            ArrayList<MCoordinate> zuerhöhende = new ArrayList<>();
                                            if ((factor>0&&currentNachbar.getMeZ(o)<currentNachbar.getMeZ(same.get(0)))||
                                                (factor<0&&currentNachbar.getMeZ(o)>currentNachbar.getMeZ(same.get(0)))){
                                               openList.add(currentNachbar);
                                                zuerhöhende.add(o);
                                            }
                                            erhöheAmEnde.put(currentNachbar, same);
                                            if (!zuerhöhende.isEmpty()) {
                                                ArrayList<MCoordinate> merge = erhöheAmEnde.get(currentNachbar);
                                                for (MCoordinate erhöhe : zuerhöhende) {
                                                    if(!merge.contains(erhöhe)) {
                                                        merge.add(erhöhe);
                                                    }
                                                }
                                                erhöheAmEnde.put(currentNachbar, merge);
                                            }
                                        }
                                    }
                                    break;
                                }
                                case 0, 1, 3: {
                                    bereitserhöht.add(currentNachbar);
                                    ArrayList<MCoordinate> same = currentNachbar.intersection(currentMittelPunkt);
                                    ArrayList<MCoordinate> other = entferne(currentNachbar.getPunkte(), currentNachbar.intersection(currentMittelPunkt));
                                    for (MCoordinate o : other) {
                                        ArrayList<MCoordinate> zuerhöhende = new ArrayList<>();
                                        if ((factor>0&&currentNachbar.getMeZ(o)<currentNachbar.getMeZ(same.get(0)))||
                                            (factor<0&&currentNachbar.getMeZ(o)>currentNachbar.getMeZ(same.get(0)))){
                                            openList.add(currentNachbar);
                                            zuerhöhende.add(o);
                                        }
                                        erhöheAmEnde.put(currentNachbar, same);
                                        if (!zuerhöhende.isEmpty()) {
                                            ArrayList<MCoordinate> merge = erhöheAmEnde.get(currentNachbar);
                                            for (MCoordinate erhöhe : zuerhöhende) {
                                                if(!merge.contains(erhöhe)) {
                                                    merge.add(erhöhe);
                                                }
                                            }
                                            erhöheAmEnde.put(currentNachbar, merge);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                }
            }

            boolean darferhöhtwerden = true;
            for(MTile key : erhöheAmEnde.keySet()){
                if(!(key.getState()==EBuildType.free||key.getState()==EBuildType.water)){
                    darferhöhtwerden = false;
                }
            }

            // ACHTUNG: hier wird nicht das Tile selbst verändert!
            if(darferhöhtwerden) {
                for (MTile key : erhöheAmEnde.keySet()) {

                    key.erhöhePunkte(erhöheAmEnde.get(key), factor);
                    key.createCreateHoehenArray();
                    key.höhendif();
                    key.setHoch(richtung);
                }
            }

        }

        // wenn Feld nicht eben/erhöhbar, gebe aus:
        else System.out.println("Feld ist zu schief");
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
        gameModel.getTileArray().forEach((tile) -> {
            VTile tempTileView = new VTile(tile);
            tempTileView.drawBackground(gc);
            if (tile.isFirstTile) {
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
        gcFront.clearRect(0, 0, canvasFront.getWidth(), canvasFront.getHeight());
    }

    // Kartesische Koordinaten werden zu isometrischen Koordinaten umgerechnet
    public static double[] toIso(double x, double y) {

        double i = (x - y) * tWidthHalf;
        double j = (x + y) * tHeightHalf;

        i += Config.XOffset;
        j += Config.YOffset;

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
        MCoordinate canvasCoord = new MCoordinate(coordX, coordY, 0);
        MCoordinate visibleCoord = canvasCoord.toVisibleCoord();
        debugCoord.setText("x: " + visibleCoord.getX() + "   y: " + visibleCoord.getY() + "     xGrid: " + coordX + "  yGrid: " + coordY);
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


    public void runUp() {
        setMouseMode(MouseMode.MOVE_UP);
    }

    public void runDown() {
        setMouseMode(MouseMode.MOVE_DOWN);
    }

    private static final Duration TICK_FREQUENCY = Duration.seconds(1);

    final EventHandler<ActionEvent> handler = event -> runTick();

    private void initTimeline() {
        KeyFrame keyframe = new KeyFrame(TICK_FREQUENCY, handler);
        tl.getKeyFrames().addAll(keyframe);
        tl.setCycleCount(Timeline.INDEFINITE);

        tl.play();
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
}
