package planverkehr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import javafx.util.Pair;
import planverkehr.graph.Graph;
import planverkehr.graph.MKnotenpunkt;

import java.util.*;

public class MGame {
    HashMap<String, MTile> tileHashMap;
    ArrayList<MTile> tileArray;
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


        MCoordinate visible1 = new MCoordinate(1, 1, 0);
        MCoordinate canvas2 = visible1.toCanvasCoord();
        MCoordinate visible2 = canvas2.toVisibleCoord();


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

    public ArrayList<MTile> getNeighbours(MTile m1){
        ArrayList<MTile> neighbours = new ArrayList<>();

        for (MTile feld : getTileArray()) {

            if (!m1.intersection(feld).isEmpty() && m1!=feld) {
                neighbours.add(feld);
            }
        }
        return neighbours;
    }

    public void createTileMap() {
        tileHashMap = new HashMap<>();
        tileArray = new ArrayList<MTile>();
        for (int x = 0; x < Config.worldWidth; x++) {
            for (int y = 0; y < Config.worldHeight; y++) {
                MCoordinate visibleCoordinates = new MCoordinate(x, y, 0);
                MCoordinate canvasCoordinates = visibleCoordinates.toCanvasCoord();
                String id = x + "-" + (y - Config.worldWidth + 1);
                MTile tempTileModel = new MTile(visibleCoordinates, canvasCoordinates, id);

                tileArray.add(tempTileModel);
                tileHashMap.put(id, tempTileModel);

            }
        }
    }
    public ArrayList<MTile> getTileArray() {
        return tileArray;
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

    public boolean selectTileByCoordinates(double x, double y) {

        String searchId = getFeldIdByCanvasCoords(x, y);
        Optional<MTile> tileOpt = findOptionalTileById(searchId);
        boolean[] selectedTile = new boolean[1];
        selectedTile[0] = false;
        tileOpt.ifPresent(tile -> {
            System.out.println("id: " + tile.getId());
            System.out.println("free? " + tile.isFree());
            System.out.println("state: " + tile.state);
            System.out.println("KnotenpunkteArray: " + tile.knotenpunkteArray);
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
            selectedTile[0] = true;

        });
        return selectedTile[0];
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
    public boolean hasSpaceForBuilding(int newBuildingWidth, int newBuildingDepth, int dz) {
        return getTilesToBeGrouped(newBuildingWidth, newBuildingDepth, dz) != null;
    }

    private MTile getSelectedTile() {
        return getTileById(selectedTileId);
    }

    public ArrayList<MTile> getGroupedTiles(int newBuildingWidth, int newBuildingDepth, boolean isFirst) {
        MTile selectedTile = getSelectedTile();
        MCoordinate selectedCoordinates = selectedTile.getIDCoordinates();
        int xCoord = (int) selectedCoordinates.getX();
        int yCoord = (int) selectedCoordinates.getY();
        ArrayList<MTile> tilesToBeGrouped = new ArrayList<>();


        //geht durch alle relevanten Tiles. selected Tile ist link unten vom Gebäude
        for (int x = 0; x < newBuildingWidth; x++) {
            for (int y = 0; y < newBuildingDepth; y++) {
                if (x + y > 0) {
                    int tileToCheckX = isFirst ? xCoord + x : xCoord - x;
                    int tileToCheckY = isFirst ? yCoord + y : yCoord - y;
                    String tileId = tileToCheckX + "--" + tileToCheckY;
                    MTile tile = getTileById(tileId);
                    if (tile != null) {
                        tilesToBeGrouped.add(tile);
                    }
                } else {
                    tilesToBeGrouped.add(selectedTile);
                }
            }
        }

        return tilesToBeGrouped;
    }

    public ArrayList<MTile> getTilesToBeGrouped(int newBuildingWidth, int newBuildingDepth, int dz) {
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
                        hasSpace = tileToCheck.isFree() && !tileToCheck.getIncline();
                        tilesToBeGrouped.add(tileToCheck);
                    }
                } else if (selectedTile.isFree() &&
                    (dz>0 ||(dz==0 && !selectedTile.getIncline()))) {
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
        MVehicles temp = vehicleTypeList.get(0);

        if (selectedTile.state == EBuildType.road || selectedTile.state == EBuildType.rail) {

            if (selectedTile.state == EBuildType.road) {
                temp = vehicleTypeList.get(0);

            } else {
                for (MVehicles mVehicles : vehicleTypeList) {
                    if (mVehicles.getKind().equals("engine")) {
                        temp = mVehicles;
                    }
                }
            }


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

        List<MProductions> productions = building.getProductions();

        return new Buildings(buildingName, buildMenu, width, depth, points, roads, rails, planes, dz, special, maxPlanes, combinesStrings, productions);
    }

//    public void removeKnotenpunkte() {
//        MTile selectedTile = getSelectedTile();
//        if (selectedTile != null &&
//            selectedTile.getConnectedBuilding() != null &&
//            (selectedTile.getConnectedBuildingType().equals(EBuildType.rail) || selectedTile.getConnectedBuildingType().equals(EBuildType.road))) {
//            System.out.println("Remove Knotenpunkt");
//            Buildings connectedBuilding = selectedTile.getConnectedBuilding();
//            EBuildType buildingType = connectedBuilding.getBuildType();
//            Graph relevantGraph = buildingType.equals(EBuildType.rail) ? railGraph : roadGraph;
//            String groupId;
//            if (!selectedTile.isFirstTile) {
//                groupId = selectedTileId + connectedBuilding.getBuildingName();
//
//            } else {
//                groupId = selectedTile.getGroupId();
//            }
//            relevantGraph.removeKnotenpunkteByFeld(groupId);
//        }
//    }

    public void resetTile() {
        MTile selectedTile = getSelectedTile();
        if (selectedTile != null &&
            selectedTile.getState() != EBuildType.free) {
            String groupId = "";
            Graph relevantGraph = selectedTile.getState().equals(EBuildType.road) ? roadGraph : railGraph;
            for (MTile mTile : getGroupedTiles(selectedTile.getConnectedBuilding().getWidth(), selectedTile.getConnectedBuilding().getDepth(),
                selectedTile.isFirstTile)) {
                if (mTile.getState().equals(EBuildType.rail) || mTile.getState().equals(EBuildType.road)) {


                    for (MKnotenpunkt k : mTile.getKnotenpunkteArray()) {
                        if (k.getGroupId().size() > 1) {
                            System.out.println("mehrere GroupIDs");
                            if (groupId.length() > 0) {
                                k.getGroupId().remove(groupId);
                            } else {
                                System.out.println("GroupID nicht gesetzt");
                            }
                        } else {
                            if (groupId.length() < 1) {
                                groupId = k.getGroupId().get(0);
                            }
                            relevantGraph.remove(k.getGridCoordinate().toStringCoordinates());
                        }
                    }
                }
                mTile.reset();
            }

        }
    }

    public String getFeldIdByCanvasCoords(double canvasX, double canvasY) {
        MCoordinate canvasCoordinate = new MCoordinate(canvasX, canvasY, 0);

        MCoordinate visibleCoordinate = canvasCoordinate.toVisibleCoord();


        // rufe Tile an der Stelle auf
        return (int) visibleCoordinate.getX() + "-" + (int) visibleCoordinate.getY();
    }
}
