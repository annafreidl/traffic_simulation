package planverkehr;

import javafx.util.Pair;
import planverkehr.graph.Graph;
import planverkehr.graph.MKnotenpunkt;

import java.util.*;

public class MGame {
    HashMap<String, MTile> tileHashMap;
    GameConfig gameConfig;
    Graph gameGraph;
    Graph roadGraph;
    Graph railGraph;
    String selectedTileId = "null";
    HashMap<String, Buildings> possibleBuildings;
    ArrayList<Buildings> constructedBuildings;
    ArrayList<MVehicles> visibleVehiclesArrayList;
    ArrayList<MVehicles> vehicleTypeList;
    int vehicleId = 0;


    public MGame(GameConfig config) {
        this.gameConfig = config;
        createTileMap();
        roadGraph = new Graph();
        railGraph = new Graph();
        visibleVehiclesArrayList = new ArrayList<>();
        vehicleTypeList = gameConfig.getVehiclesList();

        possibleBuildings = gameConfig.getBuildingsList();
        linkBuildings();
        constructedBuildings = new ArrayList<>(); //factories von Anfang an muessen rein

    }

    private void linkBuildings() {
        possibleBuildings.forEach((s, building) -> {

            Map<String, String> combinesMap = building.getCombinesStrings();
            combinesMap.forEach((s1, s2) -> {
                Buildings buildingAfterCombination = possibleBuildings.get(s2);
                building.addCombinedBuilding(s1, buildingAfterCombination);
            });
        });
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
            System.out.println("state: " + tile.state);
            System.out.println("connections: " + tile.possibleConnections);
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

    //Depth = Y
    public boolean hasSpaceForBuilding(int newBuildingWidth, int newBuildingDepth) {
        return getTilesToBeGrouped(newBuildingWidth, newBuildingDepth) != null;
    }

    private MTile getSelectedTile() {
        return getTileById(selectedTileId);
    }

    public ArrayList<MTile> getTilesToBeGrouped(int newBuildingWidth, int newBuildingDepth) {
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
                } else if (selectedTile.isFree()) {
                    tilesToBeGrouped.add(selectedTile);
                } else {
                    hasSpace = false;
                }
            }
        }

        if (!hasSpace) {
            tilesToBeGrouped = null;
        }

        return tilesToBeGrouped;
    }

    public void createVehicle() {
        MTile selectedTile = getSelectedTile();
        if (selectedTile.state == EBuildType.road) {
            MVehicles temp = vehicleTypeList.get(0);

            //todo: vehicle clone Methode
            MVehicles truck = new MVehicles(temp.getName(), temp.getKind(), temp.getGraphic(), temp.getCargo(), temp.getSpeed());

            Optional<MKnotenpunkt> knotenpunktOptional = getSelectedTile().getNodeByName("c");

            knotenpunktOptional.ifPresentOrElse((truck::setCurrentKnotenpunkt), (() -> {
                truck.setCurrentKnotenpunkt(getSelectedTile().getKnotenpunkteArray().get(0));
                System.out.println("center not found");
            }));
            truck.setId(vehicleId);
            vehicleId++;
            visibleVehiclesArrayList.add(truck);

        }
    }

    public void moveVehicles() {
        System.out.println(visibleVehiclesArrayList);
        visibleVehiclesArrayList.forEach((vehicle) -> {
            MKnotenpunkt nextKnotenpunkt = vehicle.getNextKnotenpunkt();

            vehicle.setCurrentKnotenpunkt(nextKnotenpunkt);
        });
    }

    //todo: dynamisch klonen, damit es auch noch funktioniert wenn Attribute hinzugefügt werden (bsp combinesBuildings)
    public Buildings copyBuilding(Buildings building) {

        String buildingName = building.getBuildingName();
        String buildMenu = building.getBuildMenu();
        int width = building.getWidth();
        int depth = building.getDepth();
        java.util.Map<String, MCoordinate> points = building.getPoints();
        java.util.List<Pair<String, String>> roads = building.getRoads();
        List<Pair<String, String>> rails = building.getRails();
        List<Pair<String, String>> planes = building.getPlanes();
        int dz = building.getDz();
        String special = building.getSpecial();
        int maxPlanes = building.getMaxPlanes();
        java.util.Map<String, String> combinesStrings = building.getCombinesStrings();

        List<Object> productions = building.getProductions();

        return new Buildings(buildingName, buildMenu, width, depth, points, roads, rails, planes, dz, special, maxPlanes, combinesStrings, productions);
    }
}
