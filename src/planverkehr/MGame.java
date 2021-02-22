package planverkehr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import javafx.util.Pair;
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
    MAirport mAirport;
    String selectedTileId = "null";
    HashMap<String, Buildings> possibleBuildings;
    ArrayList<Buildings> constructedBuildings;
    ArrayList<MVehicles> visibleVehiclesArrayList;
    ArrayList<MVehicles> vehicleTypeList;
    int vehicleId = 0;
    MTargetpointList listeDerBushaltestellen, listeDerBahnhöfe, listeDerFlughafenGebäude;
    int linienID = 0;
    int tickNumber = 0;
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
        constructedBuildings = new ArrayList<>(); //factories von Anfang an muessen rein


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
                if ((-1 < x + i && x + i < Config.worldWidth) && (-1 < y + j && y + j < Config.worldWidth)) {
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

    private boolean isInTile(double px_x, double px_y, MTile tile) {
        double w_factor = Config.tWidth / 2.0;
        double h_factor = Config.tHeight / 2.0;

        try {

            double p_W_x = tile.getWest().toCanvasCoord().getX();
            double p_W_y = tile.getWest().toCanvasCoord().getY();

            double p_N_x = tile.getNorth().toCanvasCoord().getX();
            double p_N_y = tile.getNorth().toCanvasCoord().getY();

            double p_E_x = tile.getEast().toCanvasCoord().getX();
            double p_E_y = tile.getEast().toCanvasCoord().getY();

            double p_S_x = tile.getSouth().toCanvasCoord().getX();
            double p_S_y = tile.getSouth().toCanvasCoord().getX();

            double low_y = Math.min(p_N_y, Math.min(p_W_y, p_E_y));
            double high_y = Math.max(p_S_y, Math.max(p_W_y, p_E_y));

            // Check borders
            if ((px_x > p_W_x && px_x < p_E_x) &&
                (px_y > low_y && px_y < high_y)) {

                double rel_x = px_x - p_W_x;
                double rel_y = px_y - p_N_y;
                // between x points and y points
                if (rel_x > w_factor) {
                    // right half of diamond
                    rel_x = rel_x - w_factor;
                    //if (rel_y > h_factor) {
                    if (rel_y > (p_E_y - p_N_y)) {
                        // lower half of diamond
                        double steigung = (p_E_y - p_S_y) / (p_E_x - p_S_x);
                        double y_bei_rel_x = p_S_y + rel_x * steigung;
                        boolean hit = y_bei_rel_x > px_y;
                        if (hit) {
                            System.out.println(rel_y);
                            System.out.println("Click (right lower) tile: (" + String.valueOf(tile.getId())+ " )");
                        }
                        return hit;

                    } else {
                        // upper half of diamond
                        double steigung = (p_E_y - p_N_y) / (p_E_x - p_N_x);
                        double y_bei_rel_x = p_N_y + rel_x * steigung;
                        boolean hit = y_bei_rel_x < px_y;
                        if (hit) {
                            System.out.println("Click (right upper) tile: ("+ String.valueOf(tile.getId())+ " )");
                        }
                        return hit;
                    }
                } else {
                    // left half of diamond#
                    //if (rel_y > h_factor) {
                    if (rel_y > (p_W_y - p_N_y)) {
                        // lower half of diamond
                        double steigung = (p_S_y - p_W_y) / (p_S_x - p_W_x);
                        double y_bei_rel_x = p_W_y + rel_x * steigung;
                        boolean hit = y_bei_rel_x > px_y;
                        if (hit) {
                            //System.out.println("Click (left lower) tile: (" + String.valueOf(tile_x) + ", " + String.valueOf(tile_y) + ")");
                        }
                        return hit;
                    } else {
                        // upper half of diamond
                        double steigung = (p_N_y - p_W_y) / (p_N_x - p_W_x);
                        double y_bei_rel_x = p_W_y + rel_x * steigung;
                        boolean hit = y_bei_rel_x < px_y;
                        if (hit) {
                            //System.out.println("Click (left upper) tile: (" + String.valueOf(tile_x) + ", " + String.valueOf(tile_y) + ")");
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

    public boolean selectTileByCoordinates(double x, double y) {

        //MTile tileOptional = getTileByPixelCoordinate(x,y);

        String searchId = getFeldIdByCanvasCoords(x, y);
        //String searchId = tileOptional.getId();
        Optional<MTile> tileOpt = findOptionalTileById(searchId);
        boolean[] selectedTile = new boolean[1];
        selectedTile[0] = false;
        tileOpt.ifPresent(tile -> {
            System.out.println("id: " + tile.getId());
            System.out.println("free? " + tile.isFree());
            System.out.println("state: " + tile.state);
            System.out.println("BuildingOnTile: " + tile.getBuildingOnTile());
            if(tile.getBuildingOnTile()!= null)System.out.println("associated Airport: " + tile.getBuildingOnTile().getAssociatedAirport());
            System.out.println("KnotenpunkteArray: " + tile.knotenpunkteArray);
            System.out.println("DZ "+ tile.TileDz());
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
                        activeLinie.addWegknotenpunkt(linienKnotenpunkt);
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

        // Nicht benutzt, aber tiles müssen sich in dem intervall ]x_lower, x_higher[ befinden, um in frage zu kommen
        int x_point_lower = (int) Math.floor(x / w_factor);
        int x_point_higher = (int) Math.ceil(y / w_factor);

        // Iterate vorne nach hinten
        int depth = Config.worldHeight;
        int width = Config.worldWidth;

        Optional<MTile> tileOpt =null;
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
                    if(tile_y==0){
                        idpiep = tile_x + "-" + tile_y;
                    }
                    else idpiep = tile_x + "--" + tile_y;
                    MTile piep = getTileById(idpiep);
                    System.out.println(idpiep);
                    if (isInTile(x, y, piep)) {
                        System.out.println("Click at tile: (" + String.valueOf(tile_x) + ", " + String.valueOf(tile_y) + ")");
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

    MTile getSelectedTile() {
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

    public ArrayList<MTile> getTilesToBeGroupedFactorie(Buildings b, MTile selectedTile) {
        MCoordinate selectedCoordinates = selectedTile.getIDCoordinates();
        int xCoord = (int) selectedCoordinates.getX();
        int yCoord = (int) selectedCoordinates.getY();
        ArrayList<MTile> tilesToBeGrouped = new ArrayList<>();

        boolean hasSpace = true;

        //geht durch alle relevanten Tiles. selected Tile ist link unten vom Gebäude
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
                } else if (selectedTile.isFree() &&
                    (b.getDz() > 0 || (b.getDz() == 0 && !selectedTile.getIncline()))) {
                    tilesToBeGrouped.add(selectedTile);

                } else {
                    hasSpace = false;
                }
            }
        }


        return tilesToBeGrouped;
    }

    //todo: Achtung, ist wahrscheinlich doppelt mit getBuildingTiles
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
                    (dz > 0 || (dz == 0 && !selectedTile.getIncline()))) {
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

    //todo: prüfen ob Tile noch existiert
    public void moveVehicles() {
        updateBlockIds();
        for (Iterator<MLinie> it = linienList.iterator(); it.hasNext(); ) {
            //Das nächste Flugzeug wird ausgewählt
            MLinie l = it.next();

            MWegKnotenpunkt w = l.getListeAllerLinienKnotenpunkte().peekFirst();
            if (l.getType().equals(EBuildType.road)) {
                MCoordinate next = w.getKnotenpunkt().getVisibleCoordinate();
                MCoordinate current = l.getVehicle().getCurrentPosition();
                boolean isLeft = (next.getX() > current.getX() && next.getY() == current.getY()) || (next.getY() > current.getY() && next.getX() == current.getX());
                if (roadGraph.containsKey(next.toStringCoordinates())) {
                    if (w.getKnotenpunkt().equals(l.getVehicle().getCurrentKnotenpunkt()) || w.getKnotenpunkt().isFreeFor(tickNumber + 1, isLeft)) {
                        setNextKnotenpunkt(l, w, isLeft);
                        l.getVehicle().setDrivesLeft(isLeft);
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
                //Zug darf sich bewegen, wenn er in einem Block steht, der Block frei ist oder vorher schon im besetzten Block war
                int currentBlockId = 0;
                if (l.getVehicle() != null) {
                    currentBlockId = l.getVehicle().getCurrentKnotenpunkt().getBlockId();
                }
                int nextBlockId = w.getKnotenpunkt().getBlockId();
                if (railGraph.containsKey(w.getKnotenpunkt().getVisibleCoordinate().toStringCoordinates())) {
                    if (nextBlockId != 0 && currentBlockId == nextBlockId || !railBlockMap.get(w.getKnotenpunkt().getBlockId()).isBlocked()) {
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


        tickNumber++;
    }

    private void setNextKnotenpunkt(MLinie l, MWegKnotenpunkt w, boolean isLeft) {
        l.getVehicle().setWaiting(false);
        l.getListeAllerLinienKnotenpunkte().pollFirst();
        w.getKnotenpunkt().addEntryToBlockedForTickList(tickNumber + 1, isLeft);
        w.getKnotenpunkt().addEntryToBlockedForTickList(tickNumber + 2, isLeft);

        w.getKnotenpunkt().removeAllBlockedForTicksSmallerThen(tickNumber);

        l.getVehicle().setCurrentKnotenpunkt(w.getKnotenpunkt());
        l.addWegknotenpunktToBack(w);
    }

    //todo: dynamisch klonen, damit es auch noch funktioniert wenn Attribute hinzugefügt werden (bsp combinesBuildings). Muss in die Buildingsklasse
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
        MCoordinate canvasCoordinate = new MCoordinate(canvasX, canvasY, 0);

        MCoordinate visibleCoordinate = canvasCoordinate.toVisibleCoord();


        // rufe Tile an der Stelle auf
        return (int) visibleCoordinate.getX() + "-" + (int) visibleCoordinate.getY();
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
            MVehicles copy = new MVehicles(temp.getName(), temp.getKind(), temp.getGraphic(), temp.getCargo(), temp.getSpeed());
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
                MVehicles copy = new MVehicles(temp.getName(), temp.getKind(), temp.getGraphic(), temp.getCargo(), temp.getSpeed());
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
            }
        } else if (activeLinie.getType().equals(EBuildType.road)) {
            if (w.getKnotenpunkt().isFreeFor(tickNumber, true)) {

                activeLinie.getVehicle().setCurrentKnotenpunkt(w.getKnotenpunkt());
                activeLinie.addWegknotenpunktToBack(w);
            } else {
                System.out.println("die erste Haltestelle ist zurZeit blockiert, es wird gewartet bis sie frei wird");
                activeLinie.getListeAllerLinienKnotenpunkte().add(w);
            }
        }

        if (canCreateLinie) {
            linienList.add(activeLinie);
            activeLinie.getVehicle().setDrivesLeft(true);
            visibleVehiclesArrayList.add(activeLinie.getVehicle());
        }
    }

    //holen uns hier alle Tiles die zu einem Building gehören
    public List<MTile> getBuildingTiles(Buildings building){
        List<MTile> buildingTiles = new ArrayList<>();

        MTile startTile = building.getStartTile();
        MCoordinate startCoordinates = startTile.getIDCoordinates();
        buildingTiles.add(startTile); //damit unsere Starttile da drin auch gesafed ist
        int buildingWidth = building.getWidth();
        int buildingDepth = building.getDepth();

        int xCoord = (int) startCoordinates.getX();
        int yCoord = (int) startCoordinates.getY();

        //geht durch alle relevanten Tiles.StartTile ist links unten vom Gebäude
        for (int x = 0; x < buildingWidth; x++) {
            for (int y = 0; y < buildingDepth; y++) {
                if (x + y > 0) {
                    int tileX = xCoord + x;
                    int tileY = yCoord + y;
                    String tileID = tileX + "--" + tileY;
                    MTile newTile = getTileById(tileID); //den PArt nehmen, und statt XY coord gehe ich da durch und rechne mir da sämtliche Nachbarn aus
                    //ein tile is links unten, eins rechts oben und dann alle Tiles die dazwischen liegen
                    //
                    buildingTiles.add(newTile); //Tile vom Gebäude
                }
            }
        }
        return buildingTiles;
    }

    //gibt Liste mit Buildings die an Gebäude angrenzen
    public List<Buildings> getNeighbourBuildings(Buildings building) {
        List<Buildings> neighbourBuildings = new ArrayList<>();
        List<MTile> buildingTiles = getBuildingTiles(building);

        //getNeighbours Methode, aber die methode nimmt nur ein Tile
        //d.h., wir rufen diese methode inner forschleife auf und prüfen dann für jedes tile von buildingsTiles die nachbarn

        for (int i = 0; i < buildingTiles.size() ; i++) {
            MTile currentTile = buildingTiles.get(i);
            MCoordinate currentCoordinates = currentTile.getIDCoordinates(); //holen uns ID von current tile
            int xCoord = (int) currentCoordinates.getX();
            int yCoord = (int) currentCoordinates.getY();

            int shiftFactor = 1; //beschreibt wie weit wir nach oben, unten, links, rechts von OG feld gehen
            //beschreibt die Tiefe in der wir die Felder anschauen

            //gehen jetzt nachbartiles durch, indem wir wir immer shiftfactor draufrechnen
            //schauen oben, unten, links und rechts tiles an
            for (int x = shiftFactor * (-1); x <= shiftFactor; x++) {
                for (int y = shiftFactor * (-1); y <= shiftFactor; y++) {
                    if (x + y != 0 && Math.abs(x + y) < 2) { //wollen nur zB 4 Felder
                        int newX = xCoord + x;
                        int newY = yCoord + y;
                        String tileID = newX + "--" + newY;
                        MTile newTile = getTileById(tileID);
                        if (newTile != null) { //wenn es diese ID in der Hashmap nicht gibt (e.g. Randteil), dann nehmen wir es nicht
                            Buildings buildingOnTile = newTile.getBuildingOnTile();
                            if (buildingOnTile != null) { //wollen es nur einspeichern, wenn ein gebäude vorhanden ist
                                neighbourBuildings.add(buildingOnTile);
                            }
                        }
                    }
                }
            }
        }//end for

        //Linked Hashset ist eine Liste, in der keine Duplikate vorkommen dürfen
        //wenn ich den kram normal in der arraylist speicher, habe ich mehrmals das gleiche nachbargebäude falls dieses an mehreren tiles angrenzt
        LinkedHashSet<Buildings> duplicateRemover = new LinkedHashSet<>(neighbourBuildings);
        ArrayList<Buildings> borderingBuildings = new ArrayList<>(duplicateRemover);
        borderingBuildings.remove(building); //haben liste mit den neighbourbuildings und kicken da unser eigenes building raus

        return borderingBuildings;
    }


    public void createStation(MTile feld, Buildings buildingToBeBuilt) {
        MHaltestelle h = new MHaltestelle(haltestellenID, buildingToBeBuilt, feld);
        haltestelleHashMap.put(haltestellenID, h);
        feld.setStation(haltestellenID);
        haltestellenID++;

    }

    public void addBuildingToStation(MTile feld, Buildings buildingToBeBuilt) {
        TreeSet<Integer> neighbourStationIDs = getNeighbourStationIDs(feld, buildingToBeBuilt.getDepth(), buildingToBeBuilt.getWidth());
        if (neighbourStationIDs.size() == 1) {
            MHaltestelle h = haltestelleHashMap.get(neighbourStationIDs.first());
            h.addBuilding(feld, buildingToBeBuilt);
            feld.setStation(neighbourStationIDs.first());
        }
    }

    public TreeSet<Integer> getNeighbourStationIDs(MTile feld, int depth, int width) {
        ArrayList<MTile> neighbours;
        if (depth == width && depth == 1) {
            neighbours = getNeighbours(feld);
        } else {
            //System.out.println("building too big - no station check");
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
        for (Buildings constructedBuilding : constructedBuildings) {
            constructedBuilding.startProductionAndConsumption();
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

    private void createBuildingNodeByCenter(EBuildType buildingToBeBuiltType, Buildings newBuilding) {

        String startPoint = getSelectedTileId(); //get Startpoint
        MTile field = getTileById(startPoint);
        double startPointX = field.getIDCoordinates().getX(); //get startpoint X and Y
        double startPointY = field.getIDCoordinates().getY();
        double centerX = startPointX + 0.5;   //PUNKT AUF SÜDWEST UNTEN LINKS

      /*  double buildingX = newBuilding.getWidth(); //get Höhe und Breite des Buildings
        double buildingY = newBuilding.getDepth();
        double centerX = x + (buildingX/2); //calculate Building Center
        double centerY = y + (buildingY/2);*/

        String key = "centerNode";
        double level = field.getLevel(); //checking hoehe der tile for coords
        MCoordinate coords;
        coords = new MCoordinate(centerX, startPointY, level);

        //GOTTA ADD JSON POINTS TO AIRPORT BUILDINGS THAT HAVE THEM
        if (newBuilding.getSpecial().equals("runway") || (newBuilding.getSpecial().equals("terminal") || (newBuilding.getSpecial().equals("taxiway")))) {
            newBuilding.getPoints().forEach((name, point) -> {

                double finalPointX = startPointX + point.getX(); //points aus JSON auf Startpoint rechnen
                double finalPointY = startPointY + point.getY();

                MCoordinate finalCoords;
                finalCoords = new MCoordinate(finalPointX, finalPointY, level);

                MKnotenpunkt buildingPoint = createBuildingNode(finalCoords, name, buildingToBeBuiltType, newBuilding);
                //System.out.println(buildingPoint);
                //System.out.println("Hi");
            });

            //IF NOT THOSE 3, SET POINT TO SOUTHWEST
        } else {
            MKnotenpunkt buildingPoint = createBuildingNode(coords, key, buildingToBeBuiltType, newBuilding);
        }
    }

    private MKnotenpunkt createBuildingNode(MCoordinate coords, String name, EBuildType buildingToBeBuiltType, Buildings newBuilding) {

        // EBuildType buildingToBeBuiltType = EBuildType.factory;
        //EBuildType buildingType = EBuildType.factory; //we know we gonna need factory
        //GET BUILDTYPE DYNAMIC FROM BUILDING WE BUILD
        Graph buildingGraph = new Graph();

        //node ID setzt sich zusammen aus string, building type and Nummer (reihenfolge) (ID +1)
        String nodeId = "" + buildingToBeBuiltType + buildingGraph.getIncreasedId();
        //System.out.println("nodeID:" + nodeId); //ID DOESNT GET INCREASED FOR SOME REASON

        String selectedTileId = getSelectedTileId(); //getting the tile we selected

        //Wenn selected tile NICHT null ist, dann mach den Kram
        if (!selectedTileId.equals("null")) {
            MTile buildingField = getTileById(selectedTileId);
            String buildingNameID = newBuilding.getBuildingName(); //getting the Name of the copied building we placed
            String groupId = buildingField.getId() + "-" + buildingNameID;

            //speichern koordinate vom aktuellen Feld
            MCoordinate fieldGridCoordinates = buildingField.getVisibleCoordinates();

            //speichern koordinaten vom NODE nicht vom Feld
            //feld koordinate is das geklickte feld, node coordinate ist die eigtl koordinate

            // MCoordinate nodeCoordinate = new MCoordinate(fieldGridCoordinates.getX() + coords.getX(), fieldGridCoordinates.getY() - coords.getY(), buildingField.getLevel());
            //WE GIVE HIM OUR OWN NODE ISNTEAD

            MKnotenpunkt knotenpunkt;

            //wenn graph kooordinaten diese koordinaten enthält, dann geben wir dem knotenpunkt diese aktuellen werte und fügen den der group ID hinzu
            //wenn er das NICHT hat (else), dann machen wir neuen Knotenpunkt und adden den in graph
            if (buildingGraph.containsKey(coords.toStringCoordinates())) {
                knotenpunkt = buildingGraph.get(coords.toStringCoordinates());
                knotenpunkt.addGroupId(groupId);
            } else {
                knotenpunkt = new MKnotenpunkt(nodeId, groupId, coords, buildingToBeBuiltType, name, buildingField.getId(), coords.getRoadDirection(), coords.isEdge());
                //System.out.println("knotenpunkt:"+ knotenpunkt);
                buildingGraph.put(coords.toStringCoordinates(), knotenpunkt);
            }
            return knotenpunkt;
        }
        return null;
    }


    public void build(EBuildType buildingToBeBuiltType, String newBuildingId, String buildingName) {
        if (!selectedTileId.equals("null")) {
            MTile feld = getTileById(selectedTileId);
            Buildings buildingToBeBuilt = getBuildingById(newBuildingId);
            boolean hasSpaceForBuilding = hasSpaceForBuilding(buildingToBeBuilt.getWidth(), buildingToBeBuilt.getDepth(), buildingToBeBuilt.getDz());
            ArrayList<MTile> relevantTiles = getTilesToBeGrouped(buildingToBeBuilt.getWidth(), buildingToBeBuilt.getDepth(), buildingToBeBuilt.getDz());

            if (buildingToBeBuiltType.equals(EBuildType.rail) || buildingToBeBuiltType.equals(EBuildType.road) || buildingToBeBuiltType.equals(EBuildType.airport)) {

                if (buildingToBeBuilt.getSpecial().equals("busstop") || buildingToBeBuilt.getSpecial().equals("railstation") || buildingToBeBuiltType.equals(EBuildType.airport)) {
                    EStationStatus stationStatus = checkForStation(feld, buildingToBeBuilt.getDepth(), buildingToBeBuilt.getWidth());
                    switch (stationStatus) {
                        case ONE -> addBuildingToStation(feld, buildingToBeBuilt);
                        case NONE -> createStation(feld, buildingToBeBuilt);
                        case TOOMANY -> hasSpaceForBuilding = false;
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
                if(hasSpaceForBuilding && buildingToBeBuiltType.equals(EBuildType.airport)) { //TODO hasSpaceForBuilding muss Randteile berücksichtigen/abchecken
                    Buildings newBuilding = new Buildings(buildingToBeBuilt); //new Building thats copied
                    newBuilding.setStartTile(feld);
                    if(mAirportManager.createOrConnectToAirport(newBuilding)) {

                        for (int j = 0; j < relevantTiles.size(); j++) {
                            MTile relevantTile = relevantTiles.get(j);
                            relevantTile.setBuildingOnTile(newBuilding); //damit Buildings auf ALLEN tiles drauf sind
                            relevantTile.addConnectedBuilding(newBuilding);
                        }
                        newBuilding.startProductionAndConsumption();
                        createBuildingNodeByCenter(buildingToBeBuiltType, buildingToBeBuilt);
                        new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, true, relevantTiles, relevantGraph, false, relevantTargetpointlist);
                    } else mAirportManager.showAirportAlert();
                } else new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantTiles, relevantGraph, false, relevantTargetpointlist);

            } else if (feld.getState().equals(EBuildType.free) && (buildingToBeBuiltType.equals(EBuildType.factory) || buildingToBeBuiltType.equals(EBuildType.nature) || buildingToBeBuiltType.equals(EBuildType.building))) {
               if(hasSpaceForBuilding) {
                   feld.setState(buildingToBeBuiltType);

                   Buildings newBuilding = new Buildings(buildingToBeBuilt); //new Building thats copied

                   for (int j = 0; j < relevantTiles.size(); j++) {
                       MTile relevantTile = relevantTiles.get(j);
                       relevantTile.setBuildingOnTile(newBuilding); //damit Buildings auf ALLEN tiles drauf sind
                       relevantTile.addConnectedBuilding(newBuilding);
                   }
                   newBuilding.setStartTile(feld);
                   newBuilding.startProductionAndConsumption();
                   createBuildingNodeByCenter(buildingToBeBuiltType, buildingToBeBuilt);

               }

            }
        }
    }

    public void clearLists() {
        constructedBuildings.clear();

    }
}
