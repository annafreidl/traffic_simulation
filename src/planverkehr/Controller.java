package planverkehr;

import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Optional;

public class Controller {
    MGame gameModel;
    VGame gameView;

    public Controller(MGame gameModel, VGame gameView) {
        this.gameView = gameView;
        this.gameModel = gameModel;

        //select tiles
        gameView.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            gameModel.selectTileByCoordinates(e.getX(), e.getY());
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
                        EBuildType feldBuildingType = feld.getState();
                        EBuildType buildingToBeBuiltType = EBuildType.valueOf(m.getId());
                        String newBuildingId = i.getId();
                        Buildings buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                        int newBuildingDepth = buildingToBeBuilt.getDepth();
                        int newBuildingWidth = buildingToBeBuilt.getWidth();

                        if (newBuildingDepth == newBuildingWidth && newBuildingWidth == 1) {

                            if (feldBuildingType == EBuildType.free) {

                                //Setzte den Status des Feldes auf den des neuen Gebäudes
                                //todo: prüfen, ob Gebäude überhaupt gebaut werden darf
                                feld.setState(buildingToBeBuiltType);

                                //verbinde Feld mit Gebäude
                                feld.addConnectedBuilding(buildingToBeBuilt);

                                //füge Knotenpunkt zum Graf hinzu
                                gameModel.createKnotenpunkt(feld, buildingToBeBuilt, buildingToBeBuiltType, true, false);
                                gameModel.roadGraph.print();
                                gameView.drawField();


                            }
                            //prüft ob Gebäude erweitert werden kann (z.B. Straße durch weitere Straße)
                            else if (buildingToBeBuilt.getCombines().containsKey(feld.getConnectedBuilding().getBuildingName())) {
                                String currentBuildingId = feld.getConnectedBuilding().getBuildingName();
                                String expandedBuildingId = buildingToBeBuilt.getCombines().get(currentBuildingId).toString();
                                buildingToBeBuilt = gameModel.getBuildingById(expandedBuildingId);
                                feld.addConnectedBuilding(buildingToBeBuilt);
                                gameModel.createKnotenpunkt(feld, buildingToBeBuilt, buildingToBeBuiltType, false, false);
                                gameModel.roadGraph.print();
                                gameView.drawField();
                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Bau-Error");
                                alert.setHeaderText("Bau-Error");
                                alert.setContentText("Das Gebäude kann auf dem ausgewähltem Feld nicht gebaut werden");

                                alert.showAndWait();
                            }
                        } else if (newBuildingDepth < 3 && newBuildingWidth < 3) {
                            //tiefe = Y
                            Buildings finalBuildingToBeBuilt = buildingToBeBuilt;
                            Optional<ArrayList<MTile>> tileOptional = gameModel.getTilesToBeGrouped(newBuildingWidth, newBuildingDepth);
                            tileOptional.ifPresentOrElse(tilesArray -> {
                                feld.setState(buildingToBeBuiltType);
                                feld.addConnectedBuilding(finalBuildingToBeBuilt);
                                gameModel.createKnotenpunkt(feld, finalBuildingToBeBuilt, buildingToBeBuiltType, true, false);
                                for (MTile tile : tilesArray) {
                                    tile.setState(buildingToBeBuiltType);
                                    tile.setShouldDraw(false);
                                    tile.addConnectedBuilding(finalBuildingToBeBuilt);
                                    gameModel.createKnotenpunkt(tile, finalBuildingToBeBuilt, buildingToBeBuiltType, true, true);
                                    gameModel.connectTiles(tile, feld);
                                }

                                //füge Knotenpunkt zum Graf hinzu
                                // gameModel.createKnotenpunkt(feld, finalBuildingToBeBuilt, buildingToBeBuiltType, true);
                                gameModel.railGraph.print();
                                gameView.drawField();
                            }, () -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Bau-Error");
                                alert.setHeaderText("Bau-Error");
                                alert.setContentText("Für das ausgewählte Gebäude ist hier kein Platz");

                                alert.showAndWait();
                            });

                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Bau-Error");
                            alert.setHeaderText("Bau-Error");
                            alert.setContentText("Es können zur Zeit keine Gebäude gebaut werden, die größer sind als ein Feld");

                            alert.showAndWait();
                        }
                    }
                });
            });
        });
    }


}
