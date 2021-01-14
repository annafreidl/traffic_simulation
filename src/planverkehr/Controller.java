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

        gameView.getMenuBar().getMenus().forEach(m -> m.getItems().forEach(i -> i.setOnAction(a -> System.out.println(a))));

        //  gameModel.getTileHashMap().forEach((integer, vTile) -> vTile.);


    }
}
