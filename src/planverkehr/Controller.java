package planverkehr;

import javafx.scene.input.MouseEvent;

public class Controller {
    MGame gameModel;
    VGame gameView;

    public Controller(MGame gameModel, VGame gameView) {
        this.gameView = gameView;
        this.gameModel = gameModel;

        //select tiles
        gameView.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            gameModel.setTileState(e.getX(), e.getY());
            gameView.drawField();
        });

        //show coordinates
        gameView.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            gameView.showCoordinates(e.getX(), e.getY());

        });

        //Build
        gameView.getMenuBar().getMenus().forEach(m -> {
            m.getItems().forEach(i -> {
                i.setOnAction(a -> {
                   String selectedTileId = gameModel.getSelectedTileId();

                   //Wenn ein Feld ausgewählt ist, dann setzte dann füge den ausgewählten Gebäudetyp zum Feld hinzu
                   if(!selectedTileId.equals("null")){
                       MTile feld = gameModel.getTileById(selectedTileId);
                       EBuildType feldBuildingType = feld.getState();
                       EBuildType buildingToBeBuiltType = EBuildType.valueOf(m.getId());
                       String newBuildingId = i.getId();
                       Buildings buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);

                       if(feldBuildingType == EBuildType.free) {

                           //Setzte den Status des Feldes auf den des neuen Gebäudes
                           //todo: prüfen, ob Gebäude überhaupt gebaut werden darf
                           feld.setState(buildingToBeBuiltType);

                           //verbinde Feld mit Gebäude
                           feld.addConnectedBuilding(buildingToBeBuilt);

                           //füge Knotenpunkt zum Graf hinzu
                           gameModel.createKnotenpunkt(feld, buildingToBeBuilt, buildingToBeBuiltType, true);
                           gameModel.roadGraph.print();
                           gameView.drawField();

                           //prüft ob Gebäude erweitert werden kann (bspw. Straße durch weitere Straße)
                       } else if(buildingToBeBuilt.getCombines().containsKey(feld.getConnectedBuilding().getBuildingName()) ){
                           String currentBuildingId = feld.getConnectedBuilding().getBuildingName();
                           String expandedBuildingId =  buildingToBeBuilt.getCombines().get(currentBuildingId).toString();
                           buildingToBeBuilt = gameModel.getBuildingById(expandedBuildingId);
                           System.out.println("can be combined");
                           feld.addConnectedBuilding(buildingToBeBuilt);
                           gameModel.createKnotenpunkt(feld, buildingToBeBuilt, buildingToBeBuiltType, false);
                           gameModel.roadGraph.print();
                           gameView.drawField();
                       }
                   }
                });
            });
        });

        //  gameModel.getTileHashMap().forEach((integer, vTile) -> vTile.);


    }
}
