package planverkehr;

import java.util.HashMap;
import java.util.Optional;

public class MGame {
    HashMap<String, MTile> tileHashMap;
    GameConfig gameConfig;
    Graph gameGraph;
    Graph roadGraph;
    Graph railGraph;
    String selectedTileId = "null";


    public MGame(GameConfig config) {
        this.gameConfig = config;
        createTileMap();
        roadGraph = new Graph();

    }

    public void createTileMap() {
        tileHashMap = new HashMap<>();
        for (int x = 0; x < Config.worldWidth; x++) {
            for (int y = 0; y < Config.worldHeight; y++) {
                MCoordinate gridCoordinates = new MCoordinate(x, y);
                MCoordinate isoCoordinates = gridCoordinates.toIso();
                String id = x + "-" + (y - Config.worldWidth + 1);
                MTile tempTileModel = new MTile(gridCoordinates, isoCoordinates, id);

                tileHashMap.put(id, tempTileModel);

            }
        }
    }

    public double[] toGrid(double x, double y) {
        double tWidth = 80;
        double tHeight = tWidth / 2;
        double tHeightHalf = tHeight / 2;
        double tWidthHalf = tWidth / 2;
        double worldWidth = 10;
        double worldHeight = 10;

        x -= Config.Xoffset;
        y -= Config.Yoffset;

        double i = ((x / tWidthHalf) + (y / tHeightHalf)) / 2;
        double j = (((y / tHeightHalf) - (x / tWidthHalf)) / 2 + 1);

        return new double[]{i, j};
    }

    public HashMap<String, MTile> getTileHashMap() {
        return tileHashMap;
    }

    public Optional<MTile> findOptionalTileById(String searchId) {
        MTile tile = tileHashMap.get(searchId);
        return Optional.ofNullable(tile);
    }

    public MTile getTileById(String id) {
        return tileHashMap.get(id);
    }

    public HashMap<String, Buildings> getBuildingsList() {
        return gameConfig.getBuildingsList();
    }

    public void setTileState(double x, double y) {
        double gridCoordinates[] = toGrid(x, y);
        int gridX = (int) gridCoordinates[0];
        int gridY = (int) gridCoordinates[1] - Config.worldWidth + 1;
        String searchId = gridX + "-" + gridY;
        System.out.println(searchId);
        Optional<MTile> tileOpt = findOptionalTileById(searchId);
//        if(tileOpt.isEmpty()){
//            System.out.println("is empty");
//            resetSelectedTile();
//        } else {
        tileOpt.ifPresent(tile -> {
            if (selectedTileId.equals(searchId)) {
                resetSelectedTile();
            } else if (!selectedTileId.equals("null")) {
                resetSelectedTile();
                tile.changeIsSelected(true);
                selectedTileId = searchId;
            } else {
                tile.changeIsSelected(true);
                selectedTileId = searchId;
            }

        });

    }

    public Buildings getBuildingById(String id) {
        return gameConfig.getBuildingsList().get(id);
    }

    private void resetSelectedTile() {
        tileHashMap.get(selectedTileId).changeIsSelected(false);
        selectedTileId = "null";
    }

    public String getSelectedTileId() {
        return selectedTileId;
    }

    public void createKnotenpunkt(MTile feld, Buildings buildingToBeBuilt, EBuildType buildType, boolean newNode) {
        String idString = feld.getId();     // full file name
        int indexOfSeperator = idString.indexOf("-"); //this finds the first occurrence of "."
//in string thus giving you the index of where it is in the string

// Now iend can be -1, if lets say the string had no "." at all in it i.e. no "." is found.
//So check and account for it.

        String xString;
        String yString;
        if (indexOfSeperator != -1) {
            xString = idString.substring(0, indexOfSeperator);
            yString = idString.substring(indexOfSeperator + 2, idString.length());

            int x = Integer.parseInt(xString);
            int y = Integer.parseInt(yString);


            switch (buildType) {
                case road -> {
                    RoadKnotenpunkt roadNode = newNode ? new RoadKnotenpunkt(feld.getId()) :  roadGraph.get(feld.getId());

                    buildingToBeBuilt.getPoints().forEach((key, coord) -> {
                        String neighbourId;
                        switch (key) {
                            case "nw":
                                roadNode.addPossibleConnection(ERoadDircetion.se);
                                neighbourId = (x - 1) + "--" + y;
                                checkForRoadNeighboursAndConnect(neighbourId, roadNode, ERoadDircetion.nw);
                                break;
                            case "ne":
                                roadNode.addPossibleConnection(ERoadDircetion.sw);
                                neighbourId = (x) + "--" + (y + 1);
                                checkForRoadNeighboursAndConnect(neighbourId, roadNode, ERoadDircetion.ne);
                                break;
                            case "se":
                                roadNode.addPossibleConnection(ERoadDircetion.nw);
                                neighbourId = (x + 1) + "--" + y;
                                checkForRoadNeighboursAndConnect(neighbourId, roadNode, ERoadDircetion.se);
                                break;
                            case "sw":
                                roadNode.addPossibleConnection(ERoadDircetion.ne);
                                neighbourId = (x) + "--" + (-1);
                                checkForRoadNeighboursAndConnect(neighbourId, roadNode, ERoadDircetion.sw);
                                break;
                            case "c":
                                break;
                        }
                    });
                    roadGraph.put(feld.getId(), roadNode);
                }
            }
        }
    }

    private void checkForRoadNeighboursAndConnect(String neighbourId, RoadKnotenpunkt roadNode, ERoadDircetion dircetion) {
        RoadKnotenpunkt neighbour = roadGraph.get(neighbourId);
        if (neighbour != null && roadGraph.canConnectToNeighbour(neighbour, dircetion)) {
            roadNode.addConnectedNode(neighbour);
            neighbour.addConnectedNode(roadNode);
        }
    }


}
