package planverkehr;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        GameConfig gameConfig = new GameConfig();

        MGame gameModel = new MGame(gameConfig);
        VGame gameView = new VGame(gameModel, primaryStage);
        new Controller(gameModel, gameView);

    }


    public static void main(String[] args) {



//        for (Vehicles vehicles : parser.getVehiclesFromJSON()) {
//            System.out.println(vehicles.getName());
//            System.out.println(vehicles.getKind());
//            System.out.println(vehicles.getGraphic());
//            System.out.println(vehicles.getCargo());
//            System.out.println(vehicles.getSpeed());
//            System.out.println("\n");
//        }
//
//        for (Buildings buildings : parser.getBuildingsFromJSON()) {
//            System.out.println(buildings.getBuildingName());
//            System.out.println(buildings.getBuildMenu());
//            System.out.println(buildings.getDepth());
//            System.out.println(buildings.getWidth());
//            System.out.println(buildings.getSpecial());
//            System.out.println(buildings.getDz());
//            System.out.println(buildings.getMaxPlanes());
//            System.out.println(buildings.getPlanes());
//            System.out.println(buildings.getRails());
//            System.out.println(buildings.getRoads());
//            System.out.println(buildings.getCombines());
//            System.out.println(buildings.getPoints());
//            System.out.println(buildings.getProductions());
//            System.out.println("\n");
//        }
        launch(args);
    }

}
