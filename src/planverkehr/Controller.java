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
import planverkehr.verkehrslinien.linienConfigController;
import planverkehr.verkehrslinien.linienConfigModel;
import planverkehr.verkehrslinien.linienConfigObject;
import planverkehr.verkehrslinien.linienConfigWindow;

import java.util.*;


public class Controller {
    MGame gameModel;
    VGame gameView;
    Buildings newBuilding;

    public Controller(MGame gameModel, VGame gameView) {
        this.gameView = gameView;
        this.gameModel = gameModel;
        LinkedList<MTile> selectedBefore = new LinkedList<>();

        initTimeline();
        gameView.getMenuBar().getMenus().forEach(m -> m.getItems().forEach(i -> i.setOnAction((event -> handleMenuClick(m, i)))));

        gameView.getLinienButton().addEventHandler(MouseEvent.MOUSE_CLICKED, onLinienButtonClick);
        gameView.getLinienButtonWeiter().addEventHandler(MouseEvent.MOUSE_CLICKED, onLinienButtonWeiterClick);
        gameView.getLinienButtonAbbrechen().addEventHandler(MouseEvent.MOUSE_CLICKED, onAbbrechenClick);
        gameView.getTickButton().addEventHandler(MouseEvent.MOUSE_CLICKED, e ->{
                e.consume();
                runTick();
            });
        gameView.getPlayButton().addEventHandler(MouseEvent.MOUSE_CLICKED, e ->{
                e.consume();
                if (gameModel.gameStarted) {
                    gameModel.togglePlayPause();
                    gameView.togglePlayPause();
                    if (gameModel.gamePaused){
                        gameModel.timeline.pause();
                    } else {
                        gameModel.timeline.play();
                    }
                } else {
                    gameModel.setStartGame();
                    gameView.startGame();
                    gameModel.timeline.play();
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
                        if(!selectedBefore.contains(gameModel.getTileById(gameModel.selectedTileId))){
                        selectedBefore.add(gameModel.getTileById(gameModel.selectedTileId));}

                     

                        if(selectedBefore.size()>1){
                            if(selectedBefore.getFirst()==null){
                                selectedBefore.removeFirst();
                            }
                            else{
                                MTile vorherausgewählt = selectedBefore.pollFirst();
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

    private void handleMenuClick(Menu m, MenuItem i) {
        String selectedTileId = gameModel.getSelectedTileId();

        if (gameModel.autoSaveMode) {
            gameModel.setSavedBuilding(i.getId());
            gameView.updateSavedBuilding();
        }


            //Wenn ein Feld ausgewählt ist, dann füge den ausgewählten Gebäudetyp zum Feld hinzu
            if (!selectedTileId.equals("null") && (!gameModel.autoSaveMode || gameModel.getSelectedTile().getState().equals(EBuildType.free))) {
                gameModel.build(EBuildType.valueOf(m.getId()), i.getId());
            

                TreeSet<MTile> changedTiles = new TreeSet<>(gameModel.getGroupedTiles(gameModel.getSelectedTile().getConnectedBuilding().getWidth(),
                    gameModel.getSelectedTile().getConnectedBuilding().getDepth(),
                    gameModel.getSelectedTile().isFirstTile));

                gameView.drawChangedTiles(changedTiles);

            }
        }

    private void initTimeline() {

        KeyFrame keyframe = new KeyFrame(gameView.tickFrequency.divide(10), handler);
        gameModel.timeline.getKeyFrames().addAll(keyframe);
        gameModel.timeline.setCycleCount(Timeline.INDEFINITE);
    }

    final EventHandler<ActionEvent> handler = event -> runTick();


    EventHandler<MouseEvent> onLinienButtonClick = actionEvent -> {

        actionEvent.consume();
        gameModel.setCreateLine(true);
        gameView.toggleLinienInfoLabel(true);

    };

    EventHandler<MouseEvent> onLinienButtonWeiterClick = mouseEvent -> {
        mouseEvent.consume();
        if (gameModel.activeLinie.getType() != null) {
            linienConfigModel lcm = new linienConfigModel(gameModel.activeLinie.getType(), gameModel.gameConfig.getVehiclesList());
            linienConfigWindow lcw = new linienConfigWindow(lcm);
            linienConfigController lcc = new linienConfigController(lcw, lcm, gameModel.activeLinie);


            linienConfigObject lcO = lcc.showConfigWindow();
            if (lcO.shouldSave()) {
               
                gameModel.saveLinie(lcO);
                if (gameModel.connectLinienPunkte()) {
                    gameModel.addLinie();
                } else {
                   
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
        double tickNumber = gameModel.tickNumber;
        ECathedralState cathedralState = null;
        if(Config.gameMode.equals("own-scenario")){
            cathedralState =  gameModel.getCathedral().getCathedralState();
 
        }




        gameModel.productionInTicks(tickNumber);
        TreeSet<MTile> changedTiles = new TreeSet<>(gameModel.moveVehicles());

        if(Config.gameMode.equals("own-scenario")){

            if(cathedralState != gameModel.getCathedral().getCathedralState()){
                changedTiles.addAll( gameModel.getBuildingTiles(gameModel.getCathedral()));
            }

        }

        if(changedTiles.size() > 0) {
            gameView.drawChangedTiles(changedTiles);
        }

        gameModel.increaseTick(gameView.getTickFrequency().toSeconds() / 10);

    }

    //TODO -------------change this
    public Buildings getCurrentBuilding() {
        return newBuilding;
    }

}
