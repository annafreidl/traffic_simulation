package planverkehr;

import javafx.util.Pair;
import planverkehr.airport.MAirport;

import java.util.*;

public class Buildings {

    private final String buildingName;
    private final String buildMenu;
    private final String special;
    private final int width;
    private final int depth;
    private final int maxPlanes;
    private final int dz;
    int stationID;
    //manche Datentypen sind wahrscheinlich nicht optimal, falls jemand eine bessere idee hat, bitte melden
    private final java.util.Map<String, MCoordinate> points;
    private final java.util.Map<String, String> combinesStrings;
    private final java.util.Map<String, Buildings> combinesBuildings;
    private final List<Pair<String, String>> roads;
    private final List<Pair<String, String>> rails;
    private final List<Pair<String, String>> planes;
    private final List<MProductions> productions;

    ECathedralState cathedralState;


    //Verbindungen des Zweiten Gebäudeteils

    private EBuildType buildType;
    private MTile startTile;

    MAirport associatedAirport;//Airport zu dem das Gebäude gehört

    public Buildings(String buildingName, String buildMenu, int width, int depth, java.util.Map<String, MCoordinate> points,
                     List<Pair<String, String>> roads, List<Pair<String, String>> rails,
                     List<Pair<String, String>> planes, int dz, String special, int maxPlanes,
                     java.util.Map<String, String> combines, List<MProductions> productions) {

        this.buildingName = buildingName;
        this.buildMenu = buildMenu;
        this.width = width;
        this.depth = depth;
        this.combinesStrings = combines;
        this.points = points;
        this.roads = roads;
        this.rails = rails;
        this.planes = planes;
        this.dz = dz;
        this.special = special;
        this.maxPlanes = maxPlanes;
        this.productions = productions;
        combinesBuildings = new HashMap<>();

        setBuildType();
    }


    //Konstruktor, der das Building was wir ihm geben kopiert
    //nimmt Werte vom alten Objekt
    public Buildings(Buildings building) {
        this.stationID = building.stationID;
        this.startTile = building.startTile;
        this.buildingName = building.getBuildingName();
        this.buildMenu = building.getBuildMenu();
        this.width = building.getWidth();
        this.depth = building.getDepth();
        this.combinesStrings = building.getCombinesStrings();
        this.points = building.getPoints();
        this.roads = building.getRoads();
        this.rails = building.getRails();
        this.planes = building.getPlanes();
        this.dz = building.getDz();
        this.special = building.getSpecial();
        this.maxPlanes = building.getMaxPlanes();
        this.productions = building.getProductions();
        combinesBuildings = building.getCombinesBuildings();
        associatedAirport = null;
        setBuildType();
        setZNull();
    }

    private void setZNull() {
        this.points.forEach((name, coord) -> coord.setZ(0));
    }

    public void setCathedralState(ECathedralState cathedralState) {
        this.cathedralState = cathedralState;
    }

    public ECathedralState getCathedralState() {
        return cathedralState;
    }

    private void setBuildType() {
        if (getRoads().size() > 0) {
            buildType = EBuildType.road;
        } else if (getRails().size() > 0 || getBuildMenu().equals("rail")) {
            buildType = EBuildType.rail;
        } else if (getPlanes().size() > 0) {
            buildType = EBuildType.airport;
        } else if (getBuildingName().equals("cathedral")) {
            buildType = EBuildType.cathedral;
        } else if (getProductions().size() > 0) {
            buildType = EBuildType.factory;
        } else if (buildMenu.equals("nature")) {
            buildType = EBuildType.nature;
        } else {
            buildType = EBuildType.unknown;
        }

    }

    public EBuildType getBuildType() {
        return buildType;
    }

    public void setCathedralBuildTypeToFoundation() {
        if (getBuildingName().equals("cathedral") && getEbuildType().equals(EBuildType.cathedral)) {
            buildType = EBuildType.cathedral_foundation;
        }
    }

    public void setCathedralBuildTypeToNave() {
        if (getBuildingName().equals("cathedral") && getEbuildType().equals(EBuildType.cathedral_foundation)) {
            buildType = EBuildType.cathedral_nave;
        }
    }

    public String getBuildingName() {
        return buildingName;
    }

    public String getBuildMenu() {
        return buildMenu;
    }

    public String getSpecial() {
        return special;
    }

    public int getWidth() {
        return width;
    }

    public int getDepth() {
        return depth;
    }

    public int getMaxPlanes() {
        return maxPlanes;
    }

    public int getDz() {
        return dz;
    }

    public Map<String, MCoordinate> getPoints() {
        return points;
    }

    public Map<String, String> getCombinesStrings() {
        return combinesStrings;
    }

    public List<Pair<String, String>> getRoads() {
        return roads;
    }

    public List<Pair<String, String>> getRails() {
        return rails;
    }

    public List<Pair<String, String>> getPlanes() {
        return planes;
    }

    public List<MProductions> getProductions() {
        return productions;
    }

    public void addCombinedBuilding(String b1, Buildings b2) {
        combinesBuildings.put(b1, b2);
    }

    public Map<String, Buildings> getCombinesBuildings() {
        return combinesBuildings;
    }

    public void setStartTile(MTile startTile) {
        this.startTile = startTile;
    }

    public MTile getStartTile() {
        return startTile;
    }

    public EBuildType getEbuildType() {
        return buildType;
    }

    public void setAssociatedAirport(MAirport associatedAirport) {
        this.associatedAirport = associatedAirport;
    }

    public MAirport getAssociatedAirport() {
        return associatedAirport;
    }

}
