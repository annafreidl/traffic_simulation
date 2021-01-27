package planverkehr;

import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.geometry.Point2D;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import planverkehr.graph.Graph;
import planverkehr.transportation.MTransportConnection;

import java.util.ArrayList;
import java.util.Optional;


enum MouseMode {
    MOVE_UP, MOVE_DOWN
}

public class Controller {
    MGame gameModel;
    VGame gameView;

    public Controller(MGame gameModel, VGame gameView) {
        this.gameView = gameView;
        this.gameModel = gameModel;

        //  initTimeline();

        //select tiles
        gameView.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                double x = e.getX();
                double y = e.getY();

                // Umwandlung von Canvas-Panel-Koordinate
                // auf Canvas-Bild-Koordinate
                // (Beachten von Scaling, Translation)
                Point2D canvasPoint = this.gameView.canvas.sceneToLocal(x, y);
                x = canvasPoint.getX();
                y = canvasPoint.getY();

                if (e.getButton() == MouseButton.SECONDARY) {

                    switch(gameView.getMouseMode()){

                        case MOVE_UP: {
                            double gridCoordinates[] = gameModel.toGrid(x, y);
                            int gridX = (int) gridCoordinates[0];
                            int gridY = (int) gridCoordinates[1] - Config.worldWidth + 1;

                            String searchId = gridX + "-" + gridY;
                            System.out.println(searchId);
                            MTile feld = gameModel.getTileById(searchId);
                            feld.yNew = feld.yNew - Config.tHeightHalft;
                            gameView.setHigh(feld, true);
                            gameModel.selectTileByCoordinates(x, y);

                            System.out.println(feld.höhen.toString());
                    }

                        case MOVE_DOWN: {
                            double gridCoordinates[] = gameModel.toGrid(x, y);
                            int gridX = (int) gridCoordinates[0];
                            int gridY = (int) gridCoordinates[1] - Config.worldWidth + 1;

                            String searchId = gridX + "-" + gridY;
                            System.out.println(searchId);
                            MTile feld = gameModel.getTileById(searchId);
                            feld.yNew = feld.yNew - Config.tHeightHalft;
                            gameView.setHigh(feld, false);
                            gameModel.selectTileByCoordinates(x, y);

                            System.out.println(feld.höhen.toString());
                        }
                    }
                    gameView.drawField();
                }
                if(e.getButton() == MouseButton.PRIMARY){
                    //select tiles
                    gameModel.selectTileByCoordinates(x, y);
                    gameView.drawField();
                }
        });


        gameView.getVehicleButton().setOnAction(e -> {
            gameModel.createVehicle();
        });

        gameView.getTickButton().setOnAction(e -> {
            gameModel.moveVehicles();
            gameView.drawField();
        });


        //show coordinates
        gameView.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, e -> gameView.showCoordinates(e.getX(), e.getY()));

        //Build
        gameView.getMenuBar().getMenus().forEach(m -> {
            m.getItems().forEach(i -> {
                i.setOnAction(a -> {
                    String selectedTileId = gameModel.getSelectedTileId();

                    //Wenn ein Feld ausgewählt ist, dann setzte dann füge den ausgewählten Gebäudetyp zum Feld hinzu
                    if (!selectedTileId.equals("null")) {
                        MTile feld = gameModel.getTileById(selectedTileId);
                        EBuildType buildingToBeBuiltType = EBuildType.valueOf(m.getId());
                        String newBuildingId = i.getId();
                        Buildings buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                        boolean hasSpaceForBuilding = gameModel.hasSpaceForBuilding(buildingToBeBuilt.getWidth(),  buildingToBeBuilt.getDepth());
                        ArrayList<MTile> relevantTiles = gameModel.getTilesToBeGrouped(buildingToBeBuilt.getWidth(),  buildingToBeBuilt.getDepth());



                        if (buildingToBeBuiltType.equals(EBuildType.rail) || buildingToBeBuiltType.equals(EBuildType.road)) {

                            Graph relevantGraph;

                            switch (buildingToBeBuiltType) {
                                case rail -> relevantGraph = gameModel.railGraph;
                                case road -> relevantGraph = gameModel.roadGraph;
                                default -> relevantGraph = gameModel.gameGraph;
                            }
                            new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantTiles, relevantGraph);


                        } else if(buildingToBeBuiltType.equals(EBuildType.factory) || buildingToBeBuiltType.equals( EBuildType.airport) || buildingToBeBuiltType.equals( EBuildType.nature)){
                            feld.setState(buildingToBeBuiltType);
                            feld.setBuilding(i.getText()); //damit wissen wir welches Menu Ding genau wir angeklickt haben, e.g. chemical plant

                            Buildings newBuilding = gameModel.copyBuilding(buildingToBeBuilt); //new Building that's copied
                            feld.setBuildingOnTile(newBuilding);
                            feld.addConnectedBuilding(newBuilding);

                        }
                        gameView.drawField();
                    }
                });
            });
        });

    }

    private void initTimeline() {
        KeyFrame keyframe = new KeyFrame(Config.tickFrequency, handler);
        gameView.getTl().getKeyFrames().addAll(keyframe);
        gameView.getTl().setCycleCount(Timeline.INDEFINITE);


        gameView.getTl().play();
    }

    final EventHandler<ActionEvent> handler = event -> {
        gameModel.moveVehicles();
        gameView.drawField();
    };


}
