package planverkehr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import com.sun.javafx.image.impl.IntArgb;
import javafx.util.Pair;
import planverkehr.airport.MAirport;
import planverkehr.airport.MAirportManager;
import planverkehr.graph.Graph;
import planverkehr.graph.MKnotenpunkt;
import planverkehr.graph.MTargetpointList;
import planverkehr.graph.MWegKnotenpunkt;
import planverkehr.transportation.ESpecial;
import planverkehr.transportation.MRailBlock;
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
                    String idString = y+j > 0 ? x + i + "--" + (y + j) : x + i + "-" + (y + j);
                    if(!idString.equals(m1.id)) {
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
                    activeLinie.addWegknotenpunkt(linienKnotenpunkt);

                }

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

        if (!hasSpace) {
            tilesToBeGrouped = null;
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
        for (MLinie l : linienList
        ) {

            MWegKnotenpunkt w = l.getListeAllerLinienKnotenpunkte().peekFirst();
            if (l.getType().equals(EBuildType.road)) {
                MCoordinate next = w.getKnotenpunkt().getVisibleCoordinate();
                MCoordinate current = l.getVehicle().getCurrentPosition();
                boolean isLeft = next.getX() < current.getY() || next.getY() < current.getY();
                if (w.getKnotenpunkt().isFreeFor(tickNumber + 1, isLeft)) {
                    setNextKnotenpunkt(l, w, isLeft);
                    l.getVehicle().setDrivesLeft(isLeft);
                } else {
                    l.getVehicle().setWaiting(true);
                    if (l.getVehicle().getCurrentKnotenpunkt() != null) {
                        l.getVehicle().getCurrentKnotenpunkt().addEntryToBlockedForTickList(tickNumber + 1, isLeft);
                        l.getVehicle().getCurrentKnotenpunkt().addEntryToBlockedForTickList(tickNumber + 2, isLeft);
                    }
                }

                System.out.println("vehicle: " + l.getVehicle().getName() + " , " + "nextNode: " + l.getVehicle().getCurrentKnotenpunkt().getVisibleCoordinate());

            } else if (l.getType().equals(EBuildType.rail)) {
                //Zug darf sich bewegen, wenn er in einem Block steht, der Block frei ist oder vorher schon im besetzten Block war
                int currentBlockId = 0;
                if (l.getVehicle() != null) {
                    currentBlockId = l.getVehicle().getCurrentKnotenpunkt().getBlockId();
                }
                int nextBlockId = w.getKnotenpunkt().getBlockId();
                if (nextBlockId != 0 && currentBlockId == nextBlockId || !railBlockMap.get(w.getKnotenpunkt().getBlockId()).isBlocked()) {
                    if (currentBlockId != nextBlockId) {
                        railBlockMap.get(currentBlockId).setBlocked(false);
                        railBlockMap.get(nextBlockId).setBlocked(true);
                    }
                    setNextKnotenpunkt(l, w, true);
                } else {
                    l.getVehicle().setWaiting(true);
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
                if(mTile.isStation){
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
        activeLinie = new MLinie(linienID);
        linienID++;
    }

    public void saveLinie(linienConfigObject lco) {
        activeLinie.setName(lco.getName());
        activeLinie.setCircle(lco.isCircle());
        MVehicles temp = lco.getVehicle();
        MVehicles copy = new MVehicles(temp.getName(), temp.getKind(), temp.getGraphic(), temp.getCargo(), temp.getSpeed());
        copy.setId(vehicleId);
        vehicleId++;

        activeLinie.setVehicle(copy);
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
    public List<MTile> getBuildingTiles(Buildings building) {
        List<MTile> buildingTiles = new ArrayList<>();

        MTile startTile = building.getStartTile();
        MCoordinate startCoordinates = startTile.getIDCoordinates();
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
                    MTile newTile = getTileById(tileID);
                    buildingTiles.add(newTile); //Tile vom Gebäude
                }
            }
        }
        return buildingTiles;
    }

    //gibt Liste mit Tiles die an Gebäude angrenzen
    public List<MTile> getNeighbourTiles(Buildings building) {
        List<MTile> neighbourTiles = new ArrayList<>();
        List<MTile> buildingTiles = getBuildingTiles(building);

        //getNeighbours Methode, aber die methode nimmt nur ein Tile
        //d.h., wir rufen diese methode inner forschleife auf und prüfen dann für jedes tile von buildingsTiles die nachbarn

        for (int i = 0; i < buildingTiles.size(); i++) {
            MTile currentTile = buildingTiles.get(i);
            //wenn Tile noch NICHT vorhanden in Liste
            neighbourTiles = getNeighbours(currentTile);

            System.out.println("Neighbour Tiles: " + neighbourTiles.toString());
        }

        return neighbourTiles;
    }


    //prüfen die Gebäude auf den angrenzenden Tiles
    public List<Buildings> getNeighbourBuildings(Buildings building) {

        List<Buildings> neighbourBuildings = new ArrayList<>();
        List<MTile> buildingTiles = getBuildingTiles(building);
        List<MTile> neighbourTiles = getNeighbourTiles(building);


        return neighbourBuildings;
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
}
