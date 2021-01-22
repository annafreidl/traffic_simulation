package planverkehr;

import planverkehr.transportation.EDirections;

import java.util.ArrayList;
import java.util.EnumSet;
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
        railGraph = new Graph();

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

        x -= Config.XOffset;
        y -= Config.YOffset;

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

    public void selectTileByCoordinates(double x, double y) {
        double[] gridCoordinates = toGrid(x, y);
        int gridX = (int) gridCoordinates[0];
        int gridY = (int) gridCoordinates[1] - Config.worldWidth + 1;
        String searchId = gridX + "-" + gridY;
        Optional<MTile> tileOpt = findOptionalTileById(searchId);

        tileOpt.ifPresent(tile -> {
            System.out.println("id: " + tile.getId());
            System.out.println("free? " + tile.isFree());
            System.out.println("state? " + tile.state);
            System.out.println();
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

    public void createKnotenpunkt(MTile feld, Buildings buildingToBeBuilt, EBuildType buildType, boolean newNode, boolean isSecondTile) {
        MCoordinate feldIDCoordinates = feld.getIDCoordinates();     // full file name

        int x = (int) feldIDCoordinates.getX();
        int y = (int) feldIDCoordinates.getY();

        if (y > 0) {
            //Fügt Knotenpunkt je nach BuildType hinzu
            switch (buildType) {
                case road -> {
                    RoadKnotenpunkt roadNode = newNode ? new RoadKnotenpunkt(feld.getId()) : roadGraph.get(feld.getId());
                    buildSingleTileTransportation(roadNode, buildingToBeBuilt, x, y, EBuildType.road, isSecondTile);
                    roadGraph.put(feld.getId(), roadNode);
                }
                case rail -> {
                    RoadKnotenpunkt railNode = newNode ? new RoadKnotenpunkt(feld.getId()) : railGraph.get(feld.getId());
                    buildSingleTileTransportation(railNode, buildingToBeBuilt, x, y, EBuildType.rail, isSecondTile);
                    railGraph.put(feld.getId(), railNode);
                }
            }
        } else {
            System.out.println("keine Koordinate erstellt");
        }
    }

    //bereitet vor, um nachbarKnotenpunkte zu suchen
    private void buildSingleTileTransportation(RoadKnotenpunkt roadNode, Buildings buildingToBeBuilt, int x, int y, EBuildType transportationType, boolean isSecondTile) {
        Graph relevantGraph = null;
        switch (transportationType) {
            case rail -> relevantGraph = railGraph;
            case road -> relevantGraph = roadGraph;
            default -> System.out.println("Unknown Transportation Kind");
        }

        Graph finalRelevantGraph = relevantGraph;

        EnumSet<EDirections> relevantConnections = isSecondTile ? buildingToBeBuilt.getPossibleConnectionsSecondTile() :  buildingToBeBuilt.getPossibleConnections();
        EnumSet<EDirections> relevantDirections = isSecondTile ? buildingToBeBuilt.getDirectionsSecondTile() :  buildingToBeBuilt.getDirections();


        relevantConnections.forEach(roadNode::addPossibleConnection);


        //prüft in welche Richtungen Verbindungen abzweigen und prüft gezielt Nachbarn
        relevantDirections.forEach((value) -> {
            String neighbourId = "";
            switch (value) {
                case ne -> neighbourId = (x) + "--" + (y + 1);
                case nw -> neighbourId = (x - 1) + "--" + y;
                case se -> neighbourId = (x + 1) + "--" + y;
                case sw -> neighbourId = (x) + "--" + (-1);
            }

            if (neighbourId.length() > 1 && finalRelevantGraph != null) {
                checkForRoadNeighboursAndConnect(neighbourId, roadNode, value, finalRelevantGraph);
            }

        });

    }

    private void checkForRoadNeighboursAndConnect(String neighbourId, RoadKnotenpunkt roadNode, EDirections direction, Graph relevantGraph) {
        RoadKnotenpunkt neighbour = relevantGraph.get(neighbourId);
        if (neighbour != null && relevantGraph.canConnectToNeighbour(neighbour, direction)) {
            roadNode.addConnectedNode(neighbour);
            neighbour.addConnectedNode(roadNode);
        }
    }

    //Depth = Y
    public boolean hasSpaceForBuilding(int newBuildingWidth, int newBuildingDepth) {
        MTile selectedTile = getSelectedTile();
        MCoordinate selectedCoordinates = selectedTile.getIDCoordinates();
        int xCoord = (int) selectedCoordinates.getX();
        int yCoord = (int) selectedCoordinates.getY();
        boolean hasSpace = true;

        //geht durch alle relevanten Tiles. selected Tile ist link unten vom Gebäude
        for (int x = 0; x < newBuildingWidth && hasSpace; x++) {
            for (int y = 0; y < newBuildingDepth && hasSpace; y++) {
                if (x + y > 0) {
                    int tileToCheckX = xCoord + x;
                    int tileToCheckY = yCoord + y;
                    String checkId = tileToCheckX + "--" + tileToCheckY;
                    hasSpace = getTileById(checkId).isFree();
                }
            }
        }
        return hasSpace;
    }

    private MTile getSelectedTile() {
        return getTileById(selectedTileId);
    }

    public Optional<ArrayList<MTile>> getTilesToBeGrouped(int newBuildingWidth, int newBuildingDepth) {
        MTile selectedTile = getSelectedTile();
        MCoordinate selectedCoordinates = selectedTile.getIDCoordinates();
        int xCoord = (int) selectedCoordinates.getX();
        int yCoord = (int) selectedCoordinates.getY();
        ArrayList<MTile> tilesToBeGrouped = new ArrayList<>();

        boolean hasSpace = true;

        //geht durch alle relevanten Tiles. selected Tile ist link unten vom Gebäude
        for (int x = 0; x < newBuildingWidth && hasSpace; x++) {
            for (int y = 0; y < newBuildingDepth && hasSpace; y++) {
                if (x + y > 0) {
                    int tileToCheckX = xCoord + x;
                    int tileToCheckY = yCoord + y;
                    String checkId = tileToCheckX + "--" + tileToCheckY;
                    MTile tileToCheck = getTileById(checkId);
                    if (tileToCheck != null) {
                        hasSpace = tileToCheck.isFree();
                        tilesToBeGrouped.add(tileToCheck);
                    }
                }
            }
        }

        if (!hasSpace) {
            tilesToBeGrouped = null;
        }

        return Optional.ofNullable(tilesToBeGrouped);
    }

    public void connectTiles(MTile tile1, MTile tile2) {
       RoadKnotenpunkt roadNode1 = railGraph.get(tile1.getId());
       RoadKnotenpunkt roadNode2 = railGraph.get(tile2.getId());

       roadNode1.addConnectedNode(roadNode2);
       roadNode2.addConnectedNode(roadNode1);
    }

}
