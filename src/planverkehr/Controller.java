package planverkehr;

import javafx.scene.input.MouseEvent;

public class Controller {
    MGame gameModel;
    VGame gameView;

    public Controller(MGame gameModel, VGame gameView) {
        this.gameView = gameView;
        this.gameModel = gameModel;

        gameView.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            gameModel.setTileState(e.getX(), e.getY());
            gameView.drawField();
        });

        gameView.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            gameView.showCoordinates(e.getX(), e.getY());

        });

        gameView.getMenuBar().getMenus().forEach(m -> {
            m.getItems().forEach(i -> {
                i.setOnAction(a -> {
                   String selectedTileId = gameModel.getSelectedTileId();

                   //Wenn ein Feld ausgewählt ist, dann setzte dann füge den ausgewählten Gebäudetyp zum Feld hinzu
                   if(!selectedTileId.equals("null")){
                       MTile feld = gameModel.getTileById(selectedTileId);
                       EBuildType currentBuilding = feld.getState();
                       EBuildType buildingType = EBuildType.valueOf(m.getId());
                       Buildings buildingToBeBuilt = gameModel.getBuildingById(i.getId());

                       //Setzte den Status des Feldes auf den des neuen Gebäudes
                       //todo: prüfen, ob Gebäude überhaupt gebaut werden darf
                       feld.setState(buildingType);

                       //verbinde Feld mit Gebäude
                       feld.setConnectedBuilding(buildingToBeBuilt);

                       //füge Knotenpunkt zum Graf hinzu
                       gameModel.createKnotenpunkt(feld, buildingToBeBuilt, buildingType);
                       gameModel.roadGraph.print();
                       gameView.drawField();
                   }
                });
            });
        });

        //  gameModel.getTileHashMap().forEach((integer, vTile) -> vTile.);


    }
}
