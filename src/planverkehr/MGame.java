package planverkehr;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MGame {
    HashMap<String, MFeld> tileHashMap;
    GameConfig gameConfig;
    String selectedTileId = "null";

    public MGame(GameConfig config) {
        this.gameConfig = config;
        createTileMap();
    }

    public void createTileMap() {
        tileHashMap = new HashMap<>();
        for (int x = 0; x < Config.worldWidth; x++) {
            for (int y = 0; y < Config.worldHeight; y++) {
                double[] isoCoordinates = toIso(x, y);
                double xIso = isoCoordinates[0];
                double yIso = isoCoordinates[1];
                MFeld tempTileModel = new MFeld(x, y, xIso, yIso);
                String id = x + "-" + (y - Config.worldWidth + 1);
                tileHashMap.put(id, tempTileModel);

            }
        }
    }

    public static double[] toIso(double x, double y) {

        double xIso = (x - y) * Config.tWidth / 2;
        double yIso = (x + y) * Config.tWidth / 4;

        xIso += Config.offset;
        yIso += Config.offset;

        return new double[]{xIso, yIso};
    }

    public double[] toGrid(double x, double y) {
         double tWidth = 80;
         double tHeight = tWidth /2;
         double tHeightHalf = tHeight / 2;
         double tWidthHalf = tWidth / 2;
         double offset = Config.windowSize / 2 - 40;
         double worldWidth = 10;
         double worldHeight = 10;

        x -= offset;
        y -= offset;

        double i = ((x / tWidthHalf) + (y / tHeightHalf)) / 2;
        double j = -(((y / tHeightHalf) - (x / tWidthHalf)) / 2 -(int)worldWidth + 1);

        return new double[] { i, j };
    }

    public HashMap<String, MFeld> getTileHashMap() {
        return tileHashMap;
    }

    public Optional<MFeld> findById(String searchId) {
        MFeld tile = tileHashMap.get(searchId);
        return Optional.ofNullable(tile);
    }

    public List<Buildings> getBuildingsList(){
        return gameConfig.getBuildingsList();
    }

    public void setTileState(double x, double y) {
       double gridCoordinates[] = toGrid(x, y);
       int gridX = (int) gridCoordinates[0];
       int gridY = (int) gridCoordinates[1] - Config.worldWidth + 1;
       String searchId = gridX+"-"+gridY;
       System.out.println(searchId);
       findById(searchId).ifPresent(tile -> {
           System.out.println("tile present");
           tile.changeIsSelected(true);
           if(selectedTileId != "null") {
               tileHashMap.get(selectedTileId).changeIsSelected(false);
           }
           selectedTileId = searchId;

       });
    }
}
