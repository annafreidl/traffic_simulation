package planverkehr;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import planverkehr.airport.MAirport;
import planverkehr.airport.MAirportManager;
import planverkehr.graph.Graph;
import planverkehr.graph.MKnotenpunkt;
import planverkehr.graph.MTargetpointList;
import planverkehr.graph.MWegKnotenpunkt;
import planverkehr.transportation.ESpecial;
import planverkehr.transportation.MRailBlock;
import planverkehr.transportation.MTransportConnection;
import planverkehr.verkehrslinien.MLinie;
import planverkehr.verkehrslinien.linienConfigObject;

import java.util.*;

public class MGame {
    HashMap<String, MTile> tileHashMap;
    ArrayList<MTile> tileArray;
    GameConfig gameConfig;
    Graph gameGraph;
    Graph roadGraph;
    Graph railGraph;
    Graph airportGraph;
    MAirportManager mAirportManager;
    String selectedTileId = "null";
    HashMap<String, Buildings> possibleBuildings;
    ArrayList<Buildings> constructedBuildings;
    ArrayList<MFactory> constructedFactories;
    ArrayList<MVehicles> visibleVehiclesArrayList;
    ArrayList<MVehicles> vehicleTypeList;
    int vehicleId = 0;
    MTargetpointList listeDerBushaltestellen, listeDerBahnhöfe, listeDerFlughafenGebäude;
    int linienID = 0;
    int tickNumber = 1;
    int haltestellenID = 0;
    private boolean createLine = false;
    ArrayList<MLinie> linienList;
    MLinie activeLinie;
    HashMap<Integer, MRailBlock> railBlockMap;
    HashMap<Integer, MHaltestelle> haltestelleHashMap;
    int lastKnownBlockID;
    boolean gameStarted = false;
    boolean gamePaused = false;
    boolean isBuilding = false;
    boolean autoSaveMode = true;
    Buildings savedBuilding;
    Timeline timeline = new Timeline();


    public MGame(GameConfig config) {
        this.gameConfig = config;
        createTileMap();
        roadGraph = new Graph();
        railGraph = new Graph();
        airportGraph = new Graph();
        mAirportManager = new MAirportManager(this);
        visibleVehiclesArrayList = new ArrayList<>();
        vehicleTypeList = gameConfig.getVehiclesList();
        linienList = new ArrayList<>();
        railBlockMap = new HashMap<>();
        railBlockMap.put(0, new MRailBlock(0));
        haltestelleHashMap = new HashMap<>();

        possibleBuildings = gameConfig.getBuildingsList();
        linkBuildings();
        constructedBuildings = new ArrayList<>(); // factories von Anfang an muessen rein
        if (constructedFactories == null) {
            constructedFactories = new ArrayList<>();
        }

        listeDerBushaltestellen = new MTargetpointList();
        listeDerBahnhöfe = new MTargetpointList();
        listeDerFlughafenGebäude = new MTargetpointList();

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

    public ArrayList<MTile> getNeighbours(MTile m1) {
        ArrayList<MTile> neighbours = new ArrayList<>();

        int x = (int) m1.getIDCoordinates().getX();
        int y = (int) m1.getIDCoordinates().getY();

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if ((-1 < x + i && x + i < Config.worldWidth) && (-1 < y + j && y + j < Config.worldHeight)) {
                    String idString = y + j > 0 ? x + i + "--" + (y + j) : x + i + "-" + (y + j);
                    if (!idString.equals(m1.id)) {
                        neighbours.add(getTileById(idString));
                    }
                }
            }
        }

//        for (MTile feld : getTileArray()) {
//
//            if (!m1.intersection(feld).isEmpty() && m1 != feld) {
//                neighbours.add(feld);
//            }
//        }
        return neighbours;
    }

    public ArrayList<MTile> getNeighboursOfSecondOrder(MTile m1) {
        ArrayList<MTile> neighbours = new ArrayList<>();

        int x = (int) m1.getIDCoordinates().getX();
        int y = (int) m1.getIDCoordinates().getY();

        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                if ((-2 < x + i && x + i < Config.worldWidth) && (-2 < y + j && y + j < Config.worldHeight)) {
                    String idString = y + j > 0 ? x + i + "--" + (y + j) : x + i + "-" + (y + j);
                    if (!idString.equals(m1.id)) {
                        neighbours.add(getTileById(idString));
                    }
                }
            }
        }

//        for (MTile feld : getTileArray()) {
//
//            if (!m1.intersection(feld).isEmpty() && m1 != feld) {
//                neighbours.add(feld);
//            }
//        }
        return neighbours;
    }


    public void createTileMap() {
        tileHashMap = new HashMap<>();
        tileArray = new ArrayList<MTile>();
        for (int x = 0; x < Config.worldWidth; x++) {
            for (int y = 0; y < Config.worldHeight; y++) {
                MCoordinate visibleCoordinates = new MCoordinate(x, y, 0);
                MCoordinate canvasCoordinates = visibleCoordinates.toCanvasCoord();
                String id = x + "-" + (y - Config.worldHeight + 1);
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

    private boolean isInTile(double px_x, double px_y, MTile tile) {
        double w_factor = Config.tWidth / 2.0;
        double h_factor = Config.tHeight / 2.0;

        try {

            double p_W_x = tile.getWest().toCanvasCoord().getX();
            double p_W_y = tile.getWest().toCanvasCoord().getY() + tile.getWest().toCanvasCoord().getZ();

            double p_N_x = tile.getNorth().toCanvasCoord().getX();
            double p_N_y = tile.getNorth().toCanvasCoord().getY() + tile.getNorth().toCanvasCoord().getZ();

            double p_E_x = tile.getEast().toCanvasCoord().getX();
            double p_E_y = tile.getEast().toCanvasCoord().getY() + tile.getEast().toCanvasCoord().getZ();

            double p_S_x = tile.getSouth().toCanvasCoord().getX();
            double p_S_y = tile.getSouth().toCanvasCoord().getY() + tile.getSouth().toCanvasCoord().getZ();

            double low_y = Math.min(p_N_y, Math.min(p_W_y, p_E_y));
            double high_y = Math.max(p_S_y, Math.max(p_W_y, p_E_y));

            // Check borders
            if ((px_x > p_W_x && px_x < p_E_x) && (px_y > low_y && px_y < high_y)) {

                double rel_x = px_x - p_W_x;
                double rel_y = px_y - p_N_y;
                // between x points and y points
                if (rel_x > w_factor) {
                    // right half of diamond
                    rel_x = rel_x - w_factor;
                    // if (rel_y > h_factor) {
                    if (rel_y > (p_E_y - p_N_y)) {
                        // lower half of diamond
                        double steigung = (p_E_y - p_S_y) / (p_E_x - p_S_x);
                        double y_bei_rel_x = p_S_y + rel_x * steigung;
                        boolean hit = y_bei_rel_x > px_y;
                        if (hit) {
                            System.out.println(rel_y);
                            System.out.println("Click (right lower) tile: (" + String.valueOf(tile.getId()) + " )");
                        }
                        return hit;

                    } else {
                        // upper half of diamond
                        double steigung = (p_E_y - p_N_y) / (p_E_x - p_N_x);
                        double y_bei_rel_x = p_N_y + rel_x * steigung;
                        boolean hit = y_bei_rel_x < px_y;
                        if (hit) {
                            System.out.println("Click (right upper) tile: (" + String.valueOf(tile.getId()) + " )");
                        }
                        return hit;
                    }
                } else {
                    // left half of diamond#
                    // if (rel_y > h_factor) {
                    if (rel_y > (p_W_y - p_N_y)) {
                        // lower half of diamond
                        double steigung = (p_S_y - p_W_y) / (p_S_x - p_W_x);
                        double y_bei_rel_x = p_W_y + rel_x * steigung;
                        boolean hit = y_bei_rel_x > px_y;
                        if (hit) {
                            // System.out.println("Click (left lower) tile: (" + String.valueOf(tile_x) + ",
                            // " + String.valueOf(tile_y) + ")");
                        }
                        return hit;
                    } else {
                        // upper half of diamond
                        double steigung = (p_N_y - p_W_y) / (p_N_x - p_W_x);
                        double y_bei_rel_x = p_W_y + rel_x * steigung;
                        boolean hit = y_bei_rel_x < px_y;
                        if (hit) {
                            // System.out.println("Click (left upper) tile: (" + String.valueOf(tile_x) + ",
                            // " + String.valueOf(tile_y) + ")");
                        }
                        return hit;
                    }
                }
            }
            return false;

        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public boolean checkTileFit(MTile tile, double x, double y, Canvas canvas){
        double xMax = Double.MIN_VALUE;
        double xMin = Double.MAX_VALUE;
        double yMax = Double.MIN_VALUE;
        double yMin = Double.MAX_VALUE;
        MCoordinate westAbsolutVisible = tile.getIDCoordinates();
        for (MCoordinate mCoordinate : tile.getPunkteNeu()) {
            MCoordinate canvasCoord = new MCoordinate(westAbsolutVisible.getX() + mCoordinate.getX(), westAbsolutVisible.getY() + mCoordinate.getY(), mCoordinate.getZ()).toCanvasCoordWithoutOffset();
            Point2D canvasPoint = canvas.localToScene(canvasCoord.getX(), canvasCoord.getY());
            xMax = Math.max(canvasPoint.getX(), xMax);
            xMin = Math.min(canvasPoint.getX(), xMin);
            yMax = Math.max(canvasPoint.getY(), yMax);
            yMin = Math.min(canvasPoint.getY(), yMin);
        }

        return xMin < x && xMax > x && yMin < y && yMax > y;
    }

    public boolean selectTileByCoordinates(double x, double y, Canvas canvas) {

        // MTile tileOptional = getTileByPixelCoordinate(x,y);

        String searchId = getFeldIdByCanvasCoords(x, y);
        // String searchId = tileOptional.getId();
        Optional<MTile> tileOpt = findOptionalTileById(searchId);
        boolean[] selectedTile = new boolean[1];
        selectedTile[0] = false;
        tileOpt.ifPresent(tile -> {
      /*   if (!checkTileFit(tile, x, y, canvas)){
             ArrayList<MTile> possiblyClickedTiles = new ArrayList<>();
             possiblyClickedTiles = getNeighboursOfSecondOrder(tile);

             for (MTile possibleClickedTile : possiblyClickedTiles) {
                if(checkTileFit(possibleClickedTile, x, y, canvas)) {
                    tile = possibleClickedTile;
                    break;
                }
             }
         } */




            System.out.println("id: " + tile.getId());
            System.out.println("state: " + tile.state);
            System.out.println("BuildingOnTile: " + tile.getBuildingOnTile());
            if (tile.getKnotenpunkteArray().size() > 0) {
                System.out.println("BlockID: " + tile.getKnotenpunkteArray().get(0).getBlockId());
            }
            if (tile.getBuildingOnTile() != null)
                System.out.println("associated Airport: " + tile.getBuildingOnTile().getAssociatedAirport());
            System.out.println("is schief?: " + tile.getIncline());
            System.out.println();

            if (selectedTileId.equals(searchId)) {
                resetSelectedTile();
            } else {
                if (!selectedTileId.equals("null")) {
                    resetSelectedTile();
                }
                tile.changeIsSelected(true);
                selectedTileId = searchId;

                if (createLine && (tile.isStation() || tile.getState().equals(EBuildType.factory))) {
                    boolean searching = true;
                    MKnotenpunkt linienKnotenpunkt = null;
                    for (int i = 0; i < tile.knotenpunkteArray.size() && searching; i++) {
                        ESpecial targetType = tile.knotenpunkteArray.get(i).getTargetType();
                        if (targetType != ESpecial.NOTHING) {
                            linienKnotenpunkt = tile.knotenpunkteArray.get(i);
                            searching = false;
                        }
                    }
                    assert linienKnotenpunkt != null;
                    if (!activeLinie.contains(linienKnotenpunkt)) {
                        activeLinie.addHaltestelle(linienKnotenpunkt);
                    }

                }

            }
            selectedTile[0] = true;

        });
        return selectedTile[0];
    }

    public MTile getTileByPixelCoordinate(double x, double y) {
        double w_factor = Config.tWidth / 2.0;
        double h_factor = Config.tHeight / 2.0;

        // Nicht benutzt, aber tiles müssen sich in dem intervall ]x_lower, x_higher[
        // befinden, um in frage zu kommen
        int x_point_lower = (int) Math.floor(x / w_factor);
        int x_point_higher = (int) Math.ceil(y / w_factor);

        // Iterate vorne nach hinten
        int depth = Config.worldHeight;
        int width = Config.worldWidth;

        Optional<MTile> tileOpt = null;
        String searchId = null;

        int tile_x = -1;
        int tile_y = -1;
        for (int k = depth + width - 2; k >= 0; k--) {
            for (int j = 0; j <= k; j++) {
                int i = k - j;
                if (i < width && j < depth) {
                    tile_x = i;
                    tile_y = (depth - 1 - j);
                    String idpiep;
                    if (tile_y == 0) {
                        idpiep = tile_x + "-" + tile_y;
                    } else
                        idpiep = tile_x + "--" + tile_y;
                    MTile piep = getTileById(idpiep);
                    System.out.println(idpiep);
                    if (isInTile(x, y, piep)) {
                        System.out.println(
                            "Click at tile: (" + String.valueOf(tile_x) + ", " + String.valueOf(tile_y) + ")");
                        return piep;
                    }
                }
            }
        }

        return null;
    }

    public Buildings getBuildingById(String id) {
        return gameConfig.getBuildingsList().get(id);
    }

    public Buildings getBuildingCopyById(String id) {
        return new Buildings(gameConfig.getBuildingsList().get(id));
    }

    private void resetSelectedTile() {
        tileHashMap.get(selectedTileId).changeIsSelected(false);
        selectedTileId = "null";
    }

    public String getSelectedTileId() {
        return selectedTileId;
    }

    // Depth = Y
    public boolean hasSpaceForBuilding(int newBuildingWidth, int newBuildingDepth, int dz) {
        return getTilesToBeGrouped(newBuildingWidth, newBuildingDepth, dz) != null;
    }

    MTile getSelectedTile() {
        return getTileById(selectedTileId);
    }

    public ArrayList<MTile> getGroupedTiles(int newBuildingWidth, int newBuildingDepth, boolean isFirst) {
        MTile selectedTile = getSelectedTile();
        MCoordinate selectedCoordinates = selectedTile.getIDCoordinates();
        int xCoord = (int) selectedCoordinates.getX();
        int yCoord = (int) selectedCoordinates.getY();
        ArrayList<MTile> tilesToBeGrouped = new ArrayList<>();

        // geht durch alle relevanten Tiles. selected Tile ist link unten vom Gebäude
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

    public ArrayList<MTile> getTilesToBeGroupedFactorie(Buildings b, MTile selectedTile) {
        MCoordinate selectedCoordinates = selectedTile.getIDCoordinates();
        int xCoord = (int) selectedCoordinates.getX();
        int yCoord = (int) selectedCoordinates.getY();
        ArrayList<MTile> tilesToBeGrouped = new ArrayList<>();

        boolean hasSpace = true;

        // geht durch alle relevanten Tiles. selected Tile ist link unten vom Gebäude
        for (int x = 0; x < b.getWidth() && hasSpace; x++) {
            for (int y = 0; y < b.getDepth() && hasSpace; y++) {
                if (x + y > 0) {
                    int tileToCheckX = xCoord + x;
                    int tileToCheckY = yCoord + y;
                    String checkId = tileToCheckX + "--" + tileToCheckY;
                    MTile tileToCheck = getTileById(checkId);
                    if (tileToCheck != null) {
                        hasSpace = tileToCheck.isFree() && !tileToCheck.getIncline();
                        tilesToBeGrouped.add(tileToCheck);
                    }
                } else if (selectedTile.isFree() && (b.getDz() > 0 || (b.getDz() == 0 && !selectedTile.getIncline()))) {
                    tilesToBeGrouped.add(selectedTile);

                } else {
                    hasSpace = false;
                }
            }
        }

        return tilesToBeGrouped;
    }

    // todo: Achtung, ist wahrscheinlich doppelt mit getBuildingTiles
    public ArrayList<MTile> getTilesToBeGrouped(int newBuildingWidth, int newBuildingDepth, int dz) {
        MTile selectedTile = getSelectedTile();
        MCoordinate selectedCoordinates = selectedTile.getIDCoordinates();
        int xCoord = (int) selectedCoordinates.getX();
        int yCoord = (int) selectedCoordinates.getY();
        ArrayList<MTile> tilesToBeGrouped = new ArrayList<>();

        boolean hasSpace = true;

        // geht durch alle relevanten Tiles. selected Tile ist link unten vom Gebäude
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
                } else if (selectedTile.isFree() && (dz > 0 || (dz == 0 && !selectedTile.getIncline()))) {
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

    public void updateBlockIds() {
        if (lastKnownBlockID < railGraph.getBlockId()) {
            while (lastKnownBlockID < railGraph.getBlockId()) {
                lastKnownBlockID++;
                railBlockMap.put(lastKnownBlockID, new MRailBlock(lastKnownBlockID));
            }
        }
    }

    public ArrayList<MTile> moveVehicles() {
        updateBlockIds();
        ArrayList<MTile> moveTiles = new ArrayList<>();
        for (Iterator<MLinie> it = linienList.iterator(); it.hasNext(); ) {
            // Das nächste Flugzeug wird ausgewählt
            MLinie l = it.next();

            MWegKnotenpunkt w = l.getListeAllerLinienKnotenpunkte().peekFirst();
            System.out.println(w.getKnotenpunkt().getFeldId());
            moveTiles.add(getTileById(w.getKnotenpunkt().getFeldId()));
            if (l.getVehicle().isVisible()) {
                moveTiles.add(getTileById(l.getVehicle().getCurrentKnotenpunkt().getFeldId()));
            }
            if (l.getType().equals(EBuildType.road)) {
                MCoordinate next = w.getKnotenpunkt().getVisibleCoordinate();
                MCoordinate current;
                if (l.getVehicle().isVisible) {
                    current = l.getVehicle().getCurrentPosition();
                } else {
                    current = next;
                }
                boolean isLeft = (next.getX() > current.getX() && next.getY() == current.getY())
                    || (next.getY() > current.getY() && next.getX() == current.getX());
                if (roadGraph.containsKey(next.toStringCoordinates())) {
                    if (next.equals(current) || w.getKnotenpunkt().isFreeFor(tickNumber + 1, isLeft)) {
                        setNextKnotenpunkt(l, w, isLeft);
                        l.getVehicle().setDrivesLeft(isLeft);
                        l.getVehicle().setVisible(true);
                    } else {
                        l.getVehicle().setWaiting(true);
                        if (l.getVehicle().getCurrentKnotenpunkt() != null) {
                            l.getVehicle().getCurrentKnotenpunkt().addEntryToBlockedForTickList(tickNumber + 1, isLeft);
                            l.getVehicle().getCurrentKnotenpunkt().addEntryToBlockedForTickList(tickNumber + 2, isLeft);
                        }
                    }

                } else {
                    visibleVehiclesArrayList.remove(l.getVehicle());
                    it.remove();

                    System.out.println("linie removed");
                }

            } else if (l.getType().equals(EBuildType.rail)) {
                // Zug darf sich bewegen, wenn er in einem Block steht, der Block frei ist oder
                // vorher schon im besetzten Block war
                int currentBlockId = 0;
                if (l.getVehicle() != null) {
                    currentBlockId = l.getVehicle().getCurrentKnotenpunkt().getBlockId();
                }
                int nextBlockId = w.getKnotenpunkt().getBlockId();
                if (railGraph.containsKey(w.getKnotenpunkt().getVisibleCoordinate().toStringCoordinates())) {
                    if (nextBlockId != 0 && currentBlockId == nextBlockId
                        || !railBlockMap.get(w.getKnotenpunkt().getBlockId()).isBlocked()) {
                        if (currentBlockId != nextBlockId) {
                            railBlockMap.get(currentBlockId).setBlocked(false);
                            railBlockMap.get(nextBlockId).setBlocked(true);
                        }
                        setNextKnotenpunkt(l, w, true);
                    } else {
                        l.getVehicle().setWaiting(true);
                    }
                } else {
                    visibleVehiclesArrayList.remove(l.getVehicle());
                    it.remove();
                }
            }
        }

        //tickNumber++;
        return moveTiles;
    }

    private void setNextKnotenpunkt(MLinie l, MWegKnotenpunkt w, boolean isLeft) {
        MVehicles v = l.getVehicle();
        v.setWaiting(false);
        l.getListeAllerLinienKnotenpunkte().pollFirst();
        w.getKnotenpunkt().addEntryToBlockedForTickList(tickNumber + 1, isLeft);
        w.getKnotenpunkt().addEntryToBlockedForTickList(tickNumber + 2, isLeft);

        w.getKnotenpunkt().removeAllBlockedForTicksSmallerThen(tickNumber);

        v.setCurrentKnotenpunkt(w.getKnotenpunkt());

        l.addWegknotenpunktToBack(w);

        if (w.getKnotenpunkt().getHaltestelle() != null) {
            MHaltestelle haltestelle = w.getKnotenpunkt().getHaltestelle();
            HashMap<MCommodity, Integer> commodityStorage = haltestelle.getCommodities();
            ArrayList<MCommodity> commoditiesToRemove = new ArrayList<>();

            // prüfen welche Waren aufgenommen werden können
            commodityStorage.forEach(((mCommodity, quantity) -> {
                if (mCommodity.haltestellenStops.size() > 0) {
                    if (l.getListOfHaltestellen().containsKey(
                        mCommodity.haltestellenStops.peek().getKnotenpunkt().getHaltestelle().getId())) {
                        if (v.takeGoodsFromFactory(mCommodity, quantity)) {
                            haltestelle.removeFromStroage(mCommodity, quantity);
                            System.out.println(quantity + " " + mCommodity + " von Haltestelle " + haltestelle.getId()
                                + " an Fahrzeug " + v.getId() + " abgegeben.");
                            System.out.println(mCommodity);
                            System.out.println(haltestelle);
                            System.out.println(v);
                        }
                    }
                } else {
                    commoditiesToRemove.add(mCommodity);
                }
            }));

            for (MCommodity commodity : commoditiesToRemove) {
                haltestelle.getCommodities().remove(commodity);
            }

            commoditiesToRemove.clear();

            // prüfen welche Waren an Haltestelle abgegeben werden können
            HashMap<MCommodity, Integer> cargoStorage = v.getCargoCurrentCommodity();

            cargoStorage.forEach((mCommodity, integer) -> {
                if (mCommodity.haltestellenStops.size() > 0) {
                    if (mCommodity.haltestellenStops.peek().getKnotenpunkt().getHaltestelle().equals(haltestelle)) {
                        haltestelle.takeGoodsFromVehicle(mCommodity, integer);
                        v.removeGoodFromVerhicle(mCommodity, integer);
                        mCommodity.haltestellenStops.pop();

                        System.out.println(integer + " " + mCommodity + " von Fahrzeug " + v.getId()
                            + " an Haltestelle " + haltestelle.getId() + " abgegeben.");
                        System.out.println(mCommodity);
                        System.out.println(haltestelle);
                        System.out.println(v);

                    }
                } else {
                    commoditiesToRemove.add(mCommodity);
                }
            });

            for (MCommodity commodity : commoditiesToRemove) {
                haltestelle.getCommodities().remove(commodity);
            }

        }
    }

    public void resetTile() {
        MTile selectedTile = getSelectedTile();
        if (selectedTile != null && selectedTile.getState() != EBuildType.free) {
            String groupId = "";

            /* Wenn das zu löschende Gebäude vom Typ Airport ist, wird dessen Referenz aus dem zugehörigen Airport entfernt.
            Sind danach im Airport keine Gebäude mehr gesetzt bzw. ist dieser nicht mehr vollständig, wird dieser gelöscht
            bzw. aus der fullyBuilt-Liste entfernt. */
            if (selectedTile.getState().equals(EBuildType.airport)) {
                Buildings buildingOnTile = selectedTile.getBuildingOnTile();
                String buildingName = buildingOnTile.getBuildingName();
                MAirport associatedAirport = buildingOnTile.getAssociatedAirport();
                mAirportManager.removeBuildingFromAirport(buildingOnTile, associatedAirport, buildingName);
                if (associatedAirport.isNoBuildingsSet()) //wenn keine Airport Gebäude existieren von dem Airport
                    mAirportManager.removeFromAirportList(associatedAirport); //dann soll Airport aus Liste gelöscht werden
                else if (!associatedAirport.isFullyBuilt()) mAirportManager.removeFromFullyBuiltList(associatedAirport);
            }

            for (MTile mTile : getGroupedTiles(selectedTile.getConnectedBuilding().getWidth(),
                selectedTile.getConnectedBuilding().getDepth(), selectedTile.isFirstTile)) {
                if (mTile.getState().equals(EBuildType.rail) || mTile.getState().equals(EBuildType.road)
                    || mTile.getState().equals(EBuildType.airport)) {

                    Graph relevantGraph;
                    if (selectedTile.getState().equals(EBuildType.road))
                        relevantGraph = roadGraph;
                    else if (selectedTile.getState().equals(EBuildType.rail))
                        relevantGraph = railGraph;
                    else
                        relevantGraph = airportGraph;

                    for (MKnotenpunkt k : mTile.getKnotenpunkteArray()) {
                        if (k.getListOfGroupId().size() > 1) {
                            System.out.println("mehrere GroupIDs");
                            if (groupId.length() > 0) {
                                k.getListOfGroupId().remove(groupId);
                            } else {
                                System.out.println("GroupID nicht gesetzt");
                            }
                        } else {
                            if (groupId.length() < 1) {
                                groupId = k.getListOfGroupId().get(0);
                            }
                            relevantGraph.remove(k.getVisibleCoordinate().toStringCoordinates());
                        }
                    }
                }
                if (mTile.isStation) {
                    MHaltestelle h = haltestelleHashMap.get(mTile.haltestellenID);
                    h.removeFeld(mTile);
                }
                mTile.reset();
            }
        }
    }

    public String getFeldIdByCanvasCoords(double canvasX, double canvasY) {
        MCoordinate visibleCoordinate = getVisibleCoordsByCanvasCoords(canvasX, canvasY);

        // rufe Tile an der Stelle auf
        return (int) visibleCoordinate.getX() + "-" + (int) visibleCoordinate.getY();
    }

    public MCoordinate getVisibleCoordsByCanvasCoords(double canvasX, double canvasY) {
        MCoordinate canvasCoordinate = new MCoordinate(canvasX, canvasY, 0);

        return canvasCoordinate.toVisibleCoord();
    }

    public boolean isCreateLine() {
        return createLine;
    }

    public void setCreateLine(boolean b) {
        createLine = b;
        if (b) {
            activeLinie = new MLinie(linienID);
            linienID++;
        }
    }

    public void saveLinie(linienConfigObject lco) {
        if (activeLinie.getID() == linienID - 1) {
            activeLinie.setName(lco.getName());
            activeLinie.setCircle(lco.isCircle());
            MVehicles temp = lco.getVehicle();
            MVehicles copy = new MVehicles(temp.getName(), temp.getKind(), temp.getGraphic(), temp.getCargo(),
                temp.getSpeed());
            copy.setId(vehicleId);
            vehicleId++;

            activeLinie.setVehicle(copy);
        } else {
            if (!activeLinie.getName().equals(lco.getName())) {
                activeLinie.setName(lco.getName());
            }
            if (activeLinie.isCircle() != lco.isCircle()) {
                activeLinie.setCircle(lco.isCircle());
            }
            if (!activeLinie.getVehicle().getName().equals(lco.getVehicle().getName())) {
                MVehicles temp = lco.getVehicle();
                MVehicles copy = new MVehicles(temp.getName(), temp.getKind(), temp.getGraphic(), temp.getCargo(),
                    temp.getSpeed());
                copy.setId(vehicleId);
                vehicleId++;
                activeLinie.setVehicle(copy);
            }
        }
    }

    public boolean connectLinienPunkte() {
        return activeLinie.connect();
    }

    public void addLinie() {
        updateBlockIds();
        MWegKnotenpunkt w = activeLinie.getListeAllerLinienKnotenpunkte().pollFirst();
        boolean canCreateLinie = true;
        if (activeLinie.getType().equals(EBuildType.rail)) {
            int nextBlockID = w.getKnotenpunkt().getBlockId();
            if (nextBlockID == 0) {
                System.out.println("baue erst Signale um eine sichere Fahrt zu gewährleisten");
                canCreateLinie = false;
            } else if (railBlockMap.get(nextBlockID).isBlocked()) {
                System.out.println("die erste Haltestelle ist zurZeit blockiert, es wird gewartet bis sie frei wird");
                activeLinie.getListeAllerLinienKnotenpunkte().add(w);
            } else {
                railBlockMap.get(nextBlockID).setBlocked(true);
                activeLinie.getVehicle().setCurrentKnotenpunkt(w.getKnotenpunkt());
                activeLinie.addWegknotenpunktToBack(w);
                activeLinie.getVehicle().setVisible(true);
            }
        } else if (activeLinie.getType().equals(EBuildType.road)) {
            if (w.getKnotenpunkt().isFreeFor(tickNumber, true)) {

                activeLinie.getVehicle().setCurrentKnotenpunkt(w.getKnotenpunkt());
                activeLinie.getVehicle().setVisible(true);
                activeLinie.addWegknotenpunktToBack(w);
            } else {
                System.out.println("die erste Haltestelle ist zurZeit blockiert, es wird gewartet bis sie frei wird");
                activeLinie.getListeAllerLinienKnotenpunkte().add(w);
            }
        } else if (activeLinie.getType().equals(EBuildType.airport)) {
            activeLinie.getVehicle().setCurrentKnotenpunkt(w.getKnotenpunkt());
            activeLinie.getVehicle().setVisible(true);
            activeLinie.addWegknotenpunktToBack(w);
        }


        if (canCreateLinie) {
            linienList.add(activeLinie);
            activeLinie.getVehicle().setDrivesLeft(true);
            if (activeLinie.getVehicle().isVisible()) {
                visibleVehiclesArrayList.add(activeLinie.getVehicle());
            }
        }
    }

    /** Diese Methode gibt eine Liste der Tiles, die ein Gebäude belegt. */
    public List<MTile> getBuildingTiles(Buildings building) {
        List<MTile> buildingTiles = new ArrayList<>();

        MTile startTile = building.getStartTile();
        MCoordinate startCoordinates = startTile.getIDCoordinates();
        buildingTiles.add(startTile); // damit unsere Starttile da drin auch gesaved ist
        int buildingWidth = building.getWidth();
        int buildingDepth = building.getDepth();

        int xCoord = (int) startCoordinates.getX();
        int yCoord = (int) startCoordinates.getY();

        // geht durch alle relevanten Tiles. StartTile ist links unten vom Gebäude
        // prozess ist: nehme das tile links unten und dann das rechts oben und berechne alle Tiles die dazwischen liegen
        for (int x = 0; x < buildingWidth; x++) {
            for (int y = 0; y < buildingDepth; y++) {
                if (x + y > 0) {
                    int tileX = xCoord + x;
                    int tileY = yCoord + y;
                    String tileID = tileX + "--" + tileY;
                    MTile newTile = getTileById(tileID);
                    buildingTiles.add(newTile); // Tile vom Gebäude
                }
            }
        }
        return buildingTiles;
    }

    /** Diese Methode gibt eine Liste der Buildings, die an das Gebäude angrenzen (ohne Dupilkate oder sich selbst). */
    public List<Buildings> getNeighbourBuildings(Buildings building) {
        List<Buildings> neighbourBuildings = new ArrayList<>();
        List<MTile> buildingTiles = getBuildingTiles(building);

        // wir iterieren über unsere Gebäude Tiles und prüfen dann für jedes tile von buildingsTiles die Nachbarn
        for (MTile currentTile : buildingTiles) {
            MCoordinate currentCoordinates = currentTile.getIDCoordinates(); // holen uns ID von current tile
            int xCoord = (int) currentCoordinates.getX();
            int yCoord = (int) currentCoordinates.getY();

            // beschreibt wie weit wir nach oben, unten, links, rechts von OG feld aus gehen; basically die Tiefe in der wir die Felder anschauen
            int shiftFactor = 1;

            // gehen jetzt nachbartiles durch, indem wir wir immer shiftfactor draufrechnen; schauen oben, unten, links und rechts tiles an
            for (int x = shiftFactor * (-1); x <= shiftFactor; x++) {
                for (int y = shiftFactor * (-1); y <= shiftFactor; y++) {
                    if (x + y != 0 && Math.abs(x + y) < 2) { // abs = Betrag; wollen zB nur 4 Felder, nicht die Eckfelder
                        int newX = xCoord + x;
                        int newY = yCoord + y;
                        String tileID = newX + "--" + newY;
                        MTile newTile = getTileById(tileID);
                        if (newTile != null) { // wenn es diese ID in der Hashmap nicht gibt (e.g. Randteil), dann nehmen wir es nicht
                            Buildings buildingOnTile = newTile.getBuildingOnTile();
                            if (buildingOnTile != null) { // wollen es nur einspeichern, wenn ein gebäude vorhanden ist
                                neighbourBuildings.add(buildingOnTile);
                            }
                        }
                    }
                }
            }
        } // end for

        // Linked Hashset ist eine Liste, in der keine Duplikate vorkommen dürfen
        // wenn ich den kram normal in einer Arrayliste speicher, habe ich mehrmals das gleiche Nachbargebäude falls dieses an mehreren tiles angrenzt
        LinkedHashSet<Buildings> duplicateRemover = new LinkedHashSet<>(neighbourBuildings);
        ArrayList<Buildings> borderingBuildings = new ArrayList<>(duplicateRemover);
        borderingBuildings.remove(building); // haben liste mit den neighbourbuildings und kicken da unser eigenes building raus

        return borderingBuildings;
    }

    public MHaltestelle createFactoryStation(MTile feld, MFactory factory) {
        MHaltestelle h = new MHaltestelle(haltestellenID, factory, feld);
        connectHaltestelle(h, factory, feld);
        return h;
    }

    private void connectHaltestelle(MHaltestelle h, Buildings buildingToBeBuilt, MTile feld) {
        setNodeToStation(feld, h);
        buildingToBeBuilt.stationID = haltestellenID;
        haltestelleHashMap.put(haltestellenID, h);
        feld.setStation(haltestellenID);
        haltestellenID++;
    }

    public void createStation(MTile feld, Buildings buildingToBeBuilt) {
        MHaltestelle h = new MHaltestelle(haltestellenID, buildingToBeBuilt, feld);
        connectHaltestelle(h, buildingToBeBuilt, feld);
    }

    public void addBuildingToStation(MTile feld, Buildings buildingToBeBuilt) {
        TreeSet<Integer> neighbourStationIDs = getNeighbourStationIDs(feld, buildingToBeBuilt.getDepth(),
            buildingToBeBuilt.getWidth());
        if (neighbourStationIDs.size() == 1) {
            MHaltestelle h = haltestelleHashMap.get(neighbourStationIDs.first());
            h.addBuilding(feld, buildingToBeBuilt);
            feld.setStation(neighbourStationIDs.first());
            buildingToBeBuilt.stationID = haltestellenID;
            setNodeToStation(feld, h);
        }
    }

    private void setNodeToStation(MTile feld, MHaltestelle h) {
        boolean knotenpunktSet = false;
        for (MKnotenpunkt knotenpunkt : feld.getKnotenpunkteArray()) {
            if (knotenpunkt.getName().equals("c")) {
                knotenpunkt.setHaltestelle(h);
                h.addKnotenpunkt(knotenpunkt);
                knotenpunktSet = true;
                break;
            }
        }

        if (!knotenpunktSet) {
            MKnotenpunkt k = feld.getKnotenpunkteArray().get(0);
            k.setHaltestelle(h);
            h.addKnotenpunkt(k);
        }
    }

    public TreeSet<Integer> getNeighbourStationIDs(MTile feld, int depth, int width) {
        ArrayList<MTile> neighbours;
        if (depth == width && depth == 1) {
            neighbours = getNeighbours(feld);
        } else {
            System.out.println("building too big - no station check");
            neighbours = new ArrayList<>();
        }

        TreeSet<Integer> neighbourStationIDs = new TreeSet<>();

        neighbours.forEach(t -> {
            if (t.isStation()) {
                neighbourStationIDs.add(t.haltestellenID);
            }
        });

        return neighbourStationIDs;
    }

    public EStationStatus checkForStation(MTile feld, int depth, int width) {
        TreeSet<Integer> neighbourStations = getNeighbourStationIDs(feld, depth, width);

        return switch (neighbourStations.size()) {
            case 0 -> EStationStatus.NONE;
            case 1 -> EStationStatus.ONE;
            default -> EStationStatus.TOOMANY;
        };

    }

    public void setStartGame() {
        gameStarted = true;
        for (MFactory factory : constructedFactories) {
            factory.setListOfFactoriesNodes(constructedFactories);
        }
    }

    public void productionInTicks(int tickNumber) {
        for (MFactory factory : constructedFactories) {
            factory.startProductionAndConsumption(tickNumber);
        }
    }

    public void togglePlayPause() {
        gamePaused = !gamePaused;
    }

    public void toggleBuilding() {
        isBuilding = !isBuilding;
        autoSaveMode = true;
        savedBuilding = null;
    }

    public void resetSpecialModes() {
        isBuilding = false;
        createLine = false;
        activeLinie = null;
        savedBuilding = null;

    }

    public String getSavedBuilding() {
        if (savedBuilding != null) {
            return savedBuilding.getBuildingName();
        } else {
            return "no Building Saved";
        }
    }

    public void setSavedBuilding(String buidlingId) {
        this.savedBuilding = getBuildingById(buidlingId);
    }

    public void toggleAutoSave() {
        autoSaveMode = !autoSaveMode;
        if (!autoSaveMode) {
            savedBuilding = null;
        }
    }

    /** Diese Methode ruft die createBuildingNode auf und setzt die Koordinaten auf Südwesten unten links bei der StartTile. */
    private void createBuildingNodeByCenter(EBuildType buildingToBeBuiltType, Buildings newBuilding) {
        String startPoint = getSelectedTileId(); // get Startpoint
        MTile field = getTileById(startPoint);
        double startPointX = field.getIDCoordinates().getX(); // get startpoint X and Y
        double startPointY = field.getIDCoordinates().getY();
        double centerX = startPointX + 0.5; // PUNKT AUF SÜDWEST UNTEN LINKS

        String key = "centerNode";
        double level = field.getLevel(); // checking Höhe der tile for coords
        MCoordinate coords;
        coords = new MCoordinate(centerX, startPointY, level);

        // GOTTA ADD JSON POINTS TO AIRPORT BUILDINGS THAT HAVE THEM
        if (newBuilding.getSpecial().equals("runway")
            || (newBuilding.getSpecial().equals("terminal") || (newBuilding.getSpecial().equals("taxiway")))) {
            newBuilding.getPoints().forEach((name, point) -> {

                double finalPointX = startPointX + point.getX(); // points aus JSON auf Startpoint rechnen
                double finalPointY = startPointY + point.getY();

                MCoordinate finalCoords;
                finalCoords = new MCoordinate(finalPointX, finalPointY, level);

                MKnotenpunkt buildingPoint = createBuildingNode(finalCoords, name, buildingToBeBuiltType, newBuilding);
            });

            // IF NOT THOSE 3, SET POINT TO SOUTHWEST
        } else {
            MKnotenpunkt buildingPoint = createBuildingNode(coords, key, buildingToBeBuiltType, newBuilding);
        }
    }

    /** Diese Methode erstellt einen Knotenpunkt für das übergebene Gebäude mit den übergebenen Koordinaten. */
    private MKnotenpunkt createBuildingNode(MCoordinate coords, String name, EBuildType buildingToBeBuiltType, Buildings newBuilding) {
        Graph buildingGraph = new Graph(); //GET BUILDTYPE DYNAMIC FROM BUILDING WE BUILD

        //node ID setzt sich zusammen aus string, building type and Nummer (reihenfolge) (ID +1)
        String nodeId = "" + buildingToBeBuiltType + buildingGraph.getIncreasedId();
        String selectedTileId = getSelectedTileId(); //getting the tile we selected

        //Wenn selected tile NICHT null ist, dann mach den Kram
        if (!selectedTileId.equals("null")) {
            MTile buildingField = getTileById(selectedTileId);
            String buildingNameID = newBuilding.getBuildingName(); //getting the Name of the copied building we placed
            String groupId = buildingField.getId() + "-" + buildingNameID;

            MKnotenpunkt knotenpunkt;

            //wenn graph kooordinaten diese koordinaten enthält, dann geben wir dem knotenpunkt diese aktuellen werte und fügen den der group ID hinzu
            //wenn er das NICHT hat (else), dann machen wir neuen Knotenpunkt und adden den in graph
            if (buildingGraph.containsKey(coords.toStringCoordinates())) {
                knotenpunkt = buildingGraph.get(coords.toStringCoordinates());
                knotenpunkt.addGroupId(groupId);
            } else {
                knotenpunkt = new MKnotenpunkt(nodeId, groupId, coords, buildingToBeBuiltType, name, buildingField.getId(), coords.getRoadDirection(), coords.isEdge());
                buildingGraph.put(coords.toStringCoordinates(), knotenpunkt);
            }
            return knotenpunkt;
        }
        return null;
    }

    public void build(EBuildType buildingToBeBuiltType, String newBuildingId) {
        if (!selectedTileId.equals("null")) {
            MTile feld = getTileById(selectedTileId);
            Buildings buildingToBeBuilt = getBuildingCopyById(newBuildingId);
            boolean hasSpaceForBuilding = hasSpaceForBuilding(buildingToBeBuilt.getWidth(), buildingToBeBuilt.getDepth(), buildingToBeBuilt.getDz());
            ArrayList<MTile> relevantTiles = getTilesToBeGrouped(buildingToBeBuilt.getWidth(), buildingToBeBuilt.getDepth(), buildingToBeBuilt.getDz());

            if (buildingToBeBuiltType.equals(EBuildType.rail) || buildingToBeBuiltType.equals(EBuildType.road) || buildingToBeBuiltType.equals(EBuildType.airport)) {
                EStationStatus stationStatus = EStationStatus.IRRELEVANT;
                if (hasSpaceForBuilding && (buildingToBeBuilt.getSpecial().equals("busstop") || buildingToBeBuilt.getSpecial().equals("railstation") || buildingToBeBuiltType.equals(EBuildType.airport))) {
                    stationStatus = checkForStation(feld, buildingToBeBuilt.getDepth(), buildingToBeBuilt.getWidth());
                    if (stationStatus.equals(EStationStatus.TOOMANY)) {
                        hasSpaceForBuilding = false;
                    }
                }

                Graph relevantGraph;
                MTargetpointList relevantTargetpointlist;

                switch (buildingToBeBuiltType) {
                    case rail -> {
                        relevantGraph = railGraph;
                        relevantTargetpointlist = listeDerBahnhöfe;
                    }
                    case road -> {
                        relevantGraph = roadGraph;
                        relevantTargetpointlist = listeDerBushaltestellen;
                    }
                    case airport -> {
                        relevantGraph = airportGraph;
                        relevantTargetpointlist = listeDerFlughafenGebäude;
                    }
                    default -> {
                        relevantGraph = gameGraph;
                        relevantTargetpointlist = listeDerBushaltestellen;
                        System.out.println("Achtung - falsche Targetpointlist ");
                    }
                }
                /* Wenn das zu setzende Gebäude vom Typ Airport ist und Platz besteht, dann
                 wird zusätzlich versucht, dieses einem Airport hinzuzufügen bzw. einen neuen für dieses zu erstellen. */
                if (hasSpaceForBuilding && buildingToBeBuiltType.equals(EBuildType.airport)) {
                    Buildings newBuilding = new Buildings(buildingToBeBuilt); //new Building thats copied
                    newBuilding.setStartTile(feld);
                    if (mAirportManager.createOrConnectToAirport(newBuilding)) {
                        newBuilding.setBuildingID(newBuildingId);
                        for (MTile relevantTile : relevantTiles) {
                            relevantTile.setBuildingOnTile(newBuilding); //damit Buildings auf ALLEN tiles drauf sind
                            relevantTile.addConnectedBuilding(newBuilding);
                        }
                        new MTransportConnection(feld, buildingToBeBuiltType, newBuilding, newBuildingId, true, relevantTiles, relevantGraph, true, relevantTargetpointlist, this);

                    } else mAirportManager.showAirportAlert();
                } else {
                    new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantTiles, relevantGraph, false, relevantTargetpointlist, this);
                    if (stationStatus.equals(EStationStatus.ONE))  addBuildingToStation(feld, buildingToBeBuilt);
                    else if (stationStatus.equals(EStationStatus.NONE)) createStation(feld, buildingToBeBuilt);
                }
            } else if (hasSpaceForBuilding && feld.getState().equals(EBuildType.free) && (buildingToBeBuiltType.equals(EBuildType.factory) || buildingToBeBuiltType.equals(EBuildType.nature) || buildingToBeBuiltType.equals(EBuildType.building))) {

                feld.setState(buildingToBeBuiltType);

                Buildings newBuilding = new Buildings(buildingToBeBuilt); //new Building thats copied
                newBuilding.setBuildingID(newBuildingId);

                    for (MTile relevantTile : relevantTiles) {
                        relevantTile.setBuildingOnTile(newBuilding); //damit Buildings auf ALLEN tiles drauf sind
                        relevantTile.addConnectedBuilding(newBuilding);
                        relevantTile.setState(buildingToBeBuiltType);
                    }
                    newBuilding.setStartTile(feld);
                    createBuildingNodeByCenter(buildingToBeBuiltType, buildingToBeBuilt);
            }
        }
    }


    public void clearLists() {
        constructedBuildings.clear();

    }

    public ArrayList<MFactory> getConstructedFactories() {
        return constructedFactories;
    }

    public void addFactoryToConstructedFactories(MFactory f) {
        if (constructedFactories == null) {
            constructedFactories = new ArrayList<>();
        }

        constructedFactories.add(f);
    }

    public MAirportManager getmAirportManager() {
        return mAirportManager;
    }
}
