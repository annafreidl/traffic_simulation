package planverkehr;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.geometry.Point2D;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import planverkehr.graph.Graph;
import planverkehr.graph.MTargetpointList;
import planverkehr.transportation.MTransportConnection;
import planverkehr.verkehrslinien.linienConfigController;
import planverkehr.verkehrslinien.linienConfigModel;
import planverkehr.verkehrslinien.linienConfigObject;
import planverkehr.verkehrslinien.linienConfigWindow;

import java.util.*;


public class Controller {
    MGame gameModel;
    VGame gameView;
    Buildings newBuilding;
    int tickNumber = 0;
    Timeline timeline = new Timeline();


    public Controller(MGame gameModel, VGame gameView) {
        this.gameView = gameView;
        this.gameModel = gameModel;
        LinkedList<MTile> selectedbefore = new LinkedList();

        initTimeline();
        gameView.getMenuBar().getMenus().forEach(m -> m.getItems().forEach(i -> i.setOnAction((event -> handleMenuClick(m, i)))));

        gameView.getLinienButton().addEventHandler(MouseEvent.MOUSE_CLICKED, onLinienButtonClick);
        gameView.getLinienButtonWeiter().addEventHandler(MouseEvent.MOUSE_CLICKED, onLinienButtonWeiterClick);
        gameView.getLinienButtonAbbrechen().addEventHandler(MouseEvent.MOUSE_CLICKED, onAbbrechenClick);
        gameView.getDefaultRoadButton().addEventHandler(MouseEvent.MOUSE_CLICKED, e ->{
                e.consume();
                Graph relevantGraph = gameModel.roadGraph;
                EBuildType buildingToBeBuiltType = EBuildType.road;
                boolean hasSpaceForBuilding = true;

                String newBuildingId = "busstop-nw-se";

                if (!gameModel.getSelectedTileId().equals("null") && gameModel.hasSpaceForBuilding(3, 6, 1)) {
                    MTile selectedTile = gameModel.getTileById(gameModel.getSelectedTileId());
                    int xSelectedTile = (int) selectedTile.getVisibleCoordinates().getX();
                    int ySelectedTile = (int) selectedTile.getIDCoordinates().getY() - 2;

                    //BusStop
                    Buildings buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    MTargetpointList relevantTargetpointlist = gameModel.listeDerBushaltestellen;

                    for (int x = xSelectedTile; x < 3 + xSelectedTile; x++) {
                        for (int y = 2 + ySelectedTile; y < 8 + ySelectedTile; y++) {
                            String tileId = x + "--" + y;
                            MTile feld = gameModel.getTileById(tileId);
                            ArrayList<MTile> relevantTiles = new ArrayList<>();
                            relevantTiles.add(feld);
                            if (x == 1 + xSelectedTile && (y == 2 + ySelectedTile || y == 4 + ySelectedTile || y == 7 + ySelectedTile)) {
                                new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantTiles, relevantGraph, true, relevantTargetpointlist);
                            }
                        }
                    }

                    newBuildingId = "busstop-ne-sw";

                    //BusStop
                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    for (int x = xSelectedTile; x < 3 + xSelectedTile; x++) {
                        for (int y = 2 + ySelectedTile; y < 8 + ySelectedTile; y++) {
                            String tileId = x + "--" + y;
                            MTile feld = gameModel.getTileById(tileId);
                            ArrayList<MTile> relevantTiles = new ArrayList<>();
                            relevantTiles.add(feld);
                            if ((x == xSelectedTile || x == 2 + xSelectedTile) && (y == 3 + ySelectedTile || y == 5 + ySelectedTile)) {
                                new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantTiles, relevantGraph, true, relevantTargetpointlist);
                            }
                        }
                    }


                    //Road-ne
                    newBuildingId = "road-ne";

                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    for (int x = xSelectedTile; x < 3 + xSelectedTile; x++) {
                        for (int y = 2 + ySelectedTile; y < 7 + ySelectedTile; y++) {
                            String tileId = x + "--" + y;
                            MTile feld = gameModel.getTileById(tileId);
                            ArrayList<MTile> relevantTiles = new ArrayList<>();
                            relevantTiles.add(feld);
                            if (feld.getConnectedBuilding() == null || (!feld.getConnectedBuilding().getSpecial().equals("busstop"))) {
                                new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantTiles, relevantGraph, true, relevantTargetpointlist);
                            }
                        }
                        x++;
                    }

                    //Road-sw
                    newBuildingId = "road-sw";

                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    for (int x = xSelectedTile; x < 3 + xSelectedTile; x++) {
                        for (int y = 3 + ySelectedTile; y < 8 + ySelectedTile; y++) {
                            String tileId = x + "--" + y;
                            MTile feld = gameModel.getTileById(tileId);
                            ArrayList<MTile> relevantTiles = new ArrayList<>();
                            relevantTiles.add(feld);
                            if (feld.getConnectedBuilding() == null || (!feld.getConnectedBuilding().getSpecial().equals("busstop"))) {
                                new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantTiles, relevantGraph, true, relevantTargetpointlist);
                            }
                        }
                        x++;
                    }

                    //Road-nw
                    newBuildingId = "road-nw";

                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);

                    for (int x = 1 + xSelectedTile; x < 3 + xSelectedTile; x++) {
                        for (int y = 2 + ySelectedTile; y < 8 + ySelectedTile; y++) {
                            String tileId = x + "--" + y;
                            MTile feld = gameModel.getTileById(tileId);
                            ArrayList<MTile> relevantTiles = new ArrayList<>();
                            relevantTiles.add(feld);
                            if ((y == 2 + ySelectedTile || y == +ySelectedTile || y == 7 + ySelectedTile) && (feld.getConnectedBuilding() == null || !feld.getConnectedBuilding().getSpecial().equals("busstop"))) {
                                new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantTiles, relevantGraph, true, relevantTargetpointlist);
                            }
                        }
                    }

                    //Road-se
                    newBuildingId = "road-se";

                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);

                    for (int x = xSelectedTile; x < 2 + xSelectedTile; x++) {
                        for (int y = 2 + ySelectedTile; y < 8 + ySelectedTile; y++) {
                            String tileId = x + "--" + y;
                            MTile feld = gameModel.getTileById(tileId);
                            ArrayList<MTile> relevantTiles = new ArrayList<>();
                            relevantTiles.add(feld);
                            if ((y == 2 + ySelectedTile || y == 4 + ySelectedTile || y == 7 + ySelectedTile) && (feld.getConnectedBuilding() == null || !feld.getConnectedBuilding().getSpecial().equals("busstop"))) {
                                new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantTiles, relevantGraph, true, relevantTargetpointlist);
                            }
                        }
                    }


                    gameView.drawField();
                } else System.out.println("no space");

            });
        gameView.getDefaultRailButton().addEventHandler(MouseEvent.MOUSE_CLICKED, e ->{
                e.consume();
                Graph relevantGraph = gameModel.railGraph;
                EBuildType buildingToBeBuiltType = EBuildType.rail;
            MTargetpointList relevantTargetpointlist = gameModel.listeDerBahnhöfe;

                if (!gameModel.getSelectedTileId().equals("null") && gameModel.hasSpaceForBuilding(7, 9, 0)) {
                    MTile selectedTile = gameModel.getTileById(gameModel.getSelectedTileId());
                    int xSelectedTile = (int) selectedTile.getVisibleCoordinates().getX() - 1;
                    int ySelectedTile = (int) selectedTile.getIDCoordinates().getY();


                    //Geraden

                    String newBuildingId = "rail-ne-sw";
                    Buildings buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    for (int x = 1; x < 8; x++) {
                        for (int y = 0; y < 10; y++) {
                            if (((x == 1 || x == 7) && (y == 0 || y == 1 || y == 4))) {
                                drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);
                            }
                        }
                    }
                    newBuildingId = "rail-nw-se";
                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    for (int x = 1; x < 8; x++) {
                        for (int y = 0; y < 10; y++) {
                            if (((x == 4) && (y == 1 || y == 7 || y == 9))) {
                                drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);
                            }
                        }
                    }

                    //Switches
                    newBuildingId = "railswitch-ne-s";
                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    int x = 1;
                    int y = 2;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);

                    newBuildingId = "railswitch-sw-e";
                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    x = 1;
                    y = 5;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);

                    x = 1;
                    y = 7;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);


                    newBuildingId = "railswitch-sw-n";
                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    x = 7;
                    y = 5;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);

                    x = 7;
                    y = 7;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);

                    newBuildingId = "railswitch-ne-w";
                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    x = 7;
                    y = 2;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);

                    //Kurven
                    newBuildingId = "railcurve-se-n";
                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    x = 2;
                    y = 1;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);

                    newBuildingId = "railcurve-nw-e";
                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    x = 5;
                    y = 1;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);

                    newBuildingId = "railcurve-nw-s";
                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    x = 5;
                    y = 7;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);

                    x = 5;
                    y = 9;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);

                    newBuildingId = "railcurve-se-w";
                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    x = 2;
                    y = 7;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);

                    x = 2;
                    y = 9;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);

//Eckverbindungen
                    newBuildingId = "rail-nw-sw";
                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    x = 2;
                    y = 2;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);

                    newBuildingId = "rail-se-sw";
                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    x = 6;
                    y = 2;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);

                    newBuildingId = "rail-ne-se";
                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    x = 6;
                    y = 6;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);

                    x = 6;
                    y = 8;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);

                    newBuildingId = "rail-ne-nw";
                    buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                    x = 2;
                    y = 6;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);

                    x = 2;
                    y = 8;
                    drawDefaultNode(x + xSelectedTile, y + ySelectedTile, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, relevantGraph, relevantTargetpointlist);

                    gameView.drawField();
                }

            });
        gameView.getTickButton().addEventHandler(MouseEvent.MOUSE_CLICKED, e ->{
                e.consume();
                runTick();
            });
        gameView.getPlayButton().addEventHandler(MouseEvent.MOUSE_CLICKED, e ->{
                e.consume();
                if (gameModel.gameStarted) {
                    gameModel.togglePlayPause();
                    gameView.togglePlayPause();
                    gameView.getTl().stop();
                } else {
                    gameModel.setStartGame();
                    gameView.startGame();
                    gameView.getTl().play();
                }

            });
        gameView.getBuildButton().addEventHandler(MouseEvent.MOUSE_CLICKED, e ->{
                e.consume();
                gameModel.toggleBuilding();
                gameView.toggleBuildingMode();
            });
        gameView.getBackButton().addEventHandler(MouseEvent.MOUSE_CLICKED, e ->{
                e.consume();
                gameModel.resetSpecialModes();
                gameView.showNormalView();
            });
        gameView.getSaveBuildingButton().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                e.consume();
                gameModel.toggleAutoSave();
                gameView.toggleAutoSave();
            });
        gameView.getDownButton().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if(!gameModel.getSelectedTileId().equals("null")){
                gameView.setHigh(gameModel.getSelectedTile(), false);

                //Hinzugefügt für Performance-Verbesserung
                // -- zeichnet nur angehobenes Tile & alle Nachbarn bis zur 2.Ordnung
                TreeSet<MTile> changedTiles = new TreeSet<>();
                changedTiles.add(gameModel.getSelectedTile());
                gameView.drawChangedTiles(changedTiles);

                //--falls Fehler auftritt, wieder gameView.drawField() nutzen und darüberstehendes auskommentieren
                //gameView.drawField();
            }
        });
        gameView.getUpButton().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if(!gameModel.getSelectedTileId().equals("null")){
                gameView.setHigh(gameModel.getSelectedTile(), true);

                //Hinzugefügt für Performance-Verbesserung
                // -- zeichnet nur angehobenes Tile & alle Nachbarn bis zur 2.Ordnung
                TreeSet<MTile> changedTiles = new TreeSet<>();
                changedTiles.add(gameModel.getSelectedTile());
                gameView.drawChangedTiles(changedTiles);

                //--falls Fehler auftritt, wieder gameView.drawField() nutzen und darüberstehendes auskommentieren
                //gameView.drawField();
            }
        });

        //show coordinates
        gameView.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, e -> gameView.showCoordinates(e.getX(), e.getY()));


        //select tiles
        gameView.getScene().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                double canvasX = e.getX();
                double canvasY = e.getY();


                // Umwandlung von Canvas-Panel-Koordinate
                // auf Canvas-Bild-Koordinate
                // (Beachten von Scaling, Translation)
                Point2D canvasPoint = this.gameView.canvas.sceneToLocal(canvasX, canvasY);
                canvasX = canvasPoint.getX();
                canvasY = canvasPoint.getY();

                if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
                    //select tiles
                    boolean isTile = gameModel.selectTileByCoordinates(canvasX, canvasY);
                    if (!gameModel.selectedTileId.equals("null") && ((gameModel.autoSaveMode && gameModel.savedBuilding != null))) {
                        gameModel.build(gameModel.savedBuilding.getBuildType(), gameModel.savedBuilding.getBuildingName());
                    }
                    if (isTile) {

                        //Hinzugefügt für Performance-Verbesserung
                        // -- zeichnet nur selectedTile und vorheriges selectedTile neu
                        TreeSet<MTile> changedTiles = new TreeSet<>();
                        if(!selectedbefore.contains(gameModel.getTileById(gameModel.selectedTileId))){
                        selectedbefore.add(gameModel.getTileById(gameModel.selectedTileId));}

                        System.out.println(selectedbefore.toString());

                        if(selectedbefore.size()>1){
                            if(selectedbefore.getFirst()==null){
                                selectedbefore.removeFirst();
                            }
                            else{
                                MTile vorherausgewählt = selectedbefore.pollFirst();
                                changedTiles.add(vorherausgewählt);
                            }

                        }
                        if(gameModel.getSelectedTile()!=null){
                            changedTiles.add(gameModel.getSelectedTile());
                        }
                       // changedTiles.removeAll(Collections.singleton(null));
                            gameView.drawChangedTiles(changedTiles);

                        //--falls Fehler auftritt, wieder gameView.drawField() nutzen und darüberstehendes auskommentieren
                        //gameView.drawField();
                    }
                }
            });

        //todo: remove connected knotenpunkte
        gameView.getRemoveButton().setOnAction(e -> {
                //   gameModel.removeKnotenpunkte();
            MTile selectedTile = gameModel.getSelectedTile();

            if(selectedTile.getState() != EBuildType.factory && selectedTile.getState() != EBuildType.free
            && selectedTile.getState() != EBuildType.water){

                TreeSet<MTile> changedTiles = new TreeSet<>();

                for (MTile mitbebaut : gameModel.getGroupedTiles(selectedTile.getConnectedBuilding().getWidth(), selectedTile.getConnectedBuilding().getDepth(), selectedTile.isFirstTile)) {
                    gameView.clearTiles(mitbebaut);
                    changedTiles.add(mitbebaut);


                }

                changedTiles.add(gameModel.getSelectedTile());



                gameModel.resetTile();
                gameView.clearTiles(selectedTile);
                gameView.drawChangedTiles(changedTiles);
            }

            });

    }

    private ArrayList<?> removeDuplicate(ArrayList<?> list) {
        Set<?> set = transformListIntoSet(list);
        return transformSetIntoList(set);
    }

    public static Set<?> transformListIntoSet(ArrayList<?> list) {
        Set<Object> set = new LinkedHashSet<>();
        set.addAll(list);
        return set;
    }

    public static ArrayList<?> transformSetIntoList(Set<?> set) {
        ArrayList<Object> list = new ArrayList<>();
        list.addAll(set);
        return list;
    }

    private void handleMenuClick(Menu m, MenuItem i) {
        String selectedTileId = gameModel.getSelectedTileId();

        if (gameModel.autoSaveMode) {
            gameModel.setSavedBuilding(i.getId());
            gameView.updateSavedBuilding();
        }


            //Wenn ein Feld ausgewählt ist, dann füge den ausgewählten Gebäudetyp zum Feld hinzu
            if (!selectedTileId.equals("null") && (!gameModel.autoSaveMode || gameModel.getSelectedTile().getState().equals(EBuildType.free))) {
                gameModel.build(EBuildType.valueOf(m.getId()), i.getId());
                System.out.println(m.getId());
                System.out.println(i.getId());
                System.out.println(i.getText());

                TreeSet<MTile> changedTiles = new TreeSet<>(gameModel.getGroupedTiles(gameModel.getSelectedTile().getConnectedBuilding().getWidth(),
                    gameModel.getSelectedTile().getConnectedBuilding().getDepth(),
                    gameModel.getSelectedTile().isFirstTile));

                gameView.drawChangedTiles(changedTiles);
                //gameView.drawField();
                // gameModel.railGraph.print();
            }
        }


    private void drawDefaultNode(int x, int y, EBuildType buildingToBeBuiltType, Buildings buildingToBeBuilt, String newBuildingId, Graph relevantGraph, MTargetpointList relevantTargetpointlist) {
        String tileId = x + "--" + y;
        MTile feld = gameModel.getTileById(tileId);
        if (feld == null) {
            tileId = x + "-" + y;
            feld = gameModel.getTileById(tileId);
        }
        gameModel.selectedTileId = tileId;
        feld.isSelected = true;
        ArrayList<MTile> relevantTiles = gameModel.getTilesToBeGrouped(buildingToBeBuilt.getWidth(), buildingToBeBuilt.getDepth(), buildingToBeBuilt.getDz());

        boolean hasSpace = gameModel.hasSpaceForBuilding(buildingToBeBuilt.getWidth(), buildingToBeBuilt.getDepth(), buildingToBeBuilt.getDz());

        new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpace, relevantTiles, relevantGraph, false, relevantTargetpointlist);

        feld.isSelected = false;
    }

    private void initTimeline() {
        KeyFrame keyframe = new KeyFrame(Config.tickFrequency, handler);
        gameView.getTl().getKeyFrames().addAll(keyframe);
        gameView.getTl().setCycleCount(Timeline.INDEFINITE);
    }

    final EventHandler<ActionEvent> handler = event -> {
        runTick();
    };


    EventHandler<MouseEvent> onLinienButtonClick = actionEvent -> {

        actionEvent.consume();
        gameModel.setCreateLine(true);
        gameView.toggleLinienInfoLabel(true);

        // gameModel.createVehicle();
        //gameView.drawField();
    };

    EventHandler<MouseEvent> onLinienButtonWeiterClick = mouseEvent -> {
        mouseEvent.consume();
        if (gameModel.activeLinie.getType() != null) {
            linienConfigModel lcm = new linienConfigModel(gameModel.activeLinie.getType(), gameModel.gameConfig.getVehiclesList());
            linienConfigWindow lcw = new linienConfigWindow(lcm);
            linienConfigController lcc = new linienConfigController(lcw, lcm, gameModel.activeLinie);


            linienConfigObject lcO = lcc.showConfigWindow();
            if (lcO.shouldSave()) {
                System.out.println("Save Action");
                gameModel.saveLinie(lcO);
                if (gameModel.connectLinienPunkte()) {
                    gameModel.addLinie();
                } else {
                    System.out.println("not connectable");
                }
                gameView.toggleLinienInfoLabel(false);
                gameModel.setCreateLine(false);

            }

        }


    };

    EventHandler<MouseEvent> onAbbrechenClick = mouseEvent -> {
        mouseEvent.consume();
        gameModel.setCreateLine(false);
        gameView.toggleLinienInfoLabel(false);
    };


    private void runTick(){

        //gameModel.moveVehicles();

//        ArrayList<MTile> changedTiles = new ArrayList<>();
//        gameModel.getTileArray().forEach((tile) -> {
//            if(tile.getState()==EBuildType.road){
//                changedTiles.add(tile);
//                //changedTiles.addAll(gameModel.getNeighbours(tile));
//                gameView.clearTiles(tile);
//
//            }
//        });
        TreeSet<MTile> changedTiles = new TreeSet<>(gameModel.moveVehicles());
        for(MTile toClear : changedTiles){
            gameView.clearTiles(toClear);
        }
        gameView.drawChangedTiles(changedTiles);
        gameModel.productionInTicks(tickNumber+1);
        tickNumber++;
        System.out.println("Tick: " + tickNumber);
    }

    //TODO -------------change this
    public Buildings getCurrentBuilding() {
        return newBuilding;
    }

}
