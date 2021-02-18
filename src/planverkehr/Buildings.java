package planverkehr;

import javafx.util.Pair;
import planverkehr.transportation.EDirections;

import java.util.*;

public class Buildings {

    private String buildingName;
    private String buildMenu;
    private String special;
    private int width;
    private int depth;
    private int maxPlanes;
    private int dz;
    //manche Datentypen sind wahrscheinlich nicht optimal, falls jemand eine bessere idee hat, bitte melden
    private java.util.Map<String, MCoordinate> points;
    private java.util.Map<String, String> combinesStrings;
    private java.util.Map<String, Buildings> combinesBuildings;
    private List<Pair<String, String>> roads;
    private List<Pair<String, String>> rails;
    private List<Pair<String, String>> planes;
    private List<MProductions> productions;

    private EnumSet<EDirections> possibleConnections;
    private EnumSet<EDirections> directions;

    //Verbindungen des Zweiten Gebäudeteils
    private EnumSet<EDirections> possibleConnectionsSecondTile;
    private EnumSet<EDirections> directionsSecondTile;
    private EBuildType buildType;
    private MTile startTile;
    //todo: buildType hinzufügen


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
        setDirections();
        setPossibleConnection();
    }


    //Konstruktor, der das Building was wir ihm geben kopiert
    //nimmt Werte vom alten Objekt
    public Buildings (Buildings building) {

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
        combinesBuildings = new HashMap<>();
        setBuildType();
        setDirections();
        setPossibleConnection();
    }



    private void setBuildType() {
        if (getRoads().size() > 0) {
            buildType = EBuildType.road;
        } else if (getRails().size() > 0) {
            buildType = EBuildType.rail;
        } else if (getPlanes().size() > 0){
            buildType = EBuildType.airport;
        } else if (getProductions().size() > 0){
            buildType = EBuildType.factory;
        } else {
            buildType = EBuildType.unknown;
        }

    }

    public EBuildType getBuildType() {
        return buildType;
    }

    private void setDirections() {
        this.directions = EnumSet.noneOf(EDirections.class);
        this.directionsSecondTile = EnumSet.noneOf(EDirections.class);
        if (this.getPoints().size() > 0) {
            this.getPoints().forEach((key, coord) -> {
                if (coord.isEdge()) {
                    if (coord.isSecondTile()) {
                        directionsSecondTile.add(coord.getRoadDirection());
                    }
                    this.directions.add(coord.getRoadDirection());
                }
            });
        }
    }

    //Optimiert für Rails und Roads, Airport und Stations müssen noch geprüft werden
    private void setPossibleConnection() {
        this.possibleConnections = EnumSet.noneOf(EDirections.class);
        this.possibleConnectionsSecondTile = EnumSet.noneOf(EDirections.class);
        if (this.getDirections().size() > 0) {
            addConnection(false);
        }
        if (this.getDirectionsSecondTile().size() > 0) {
            addConnection(true);
        }
    }

    private void addConnection(boolean isSecondTile) {
        EnumSet<EDirections> relevantConnectionSet = isSecondTile ? getPossibleConnectionsSecondTile() : getPossibleConnections();
        EnumSet<EDirections> relevantDirectionSet = isSecondTile ? getDirectionsSecondTile() : getDirections();

        relevantDirectionSet.forEach((coord) -> {
            switch (coord) {
                case NW -> relevantConnectionSet.add(EDirections.SE);
                case NE -> relevantConnectionSet.add(EDirections.SW);
                case SE -> relevantConnectionSet.add(EDirections.NW);
                case SW -> relevantConnectionSet.add(EDirections.NE);
            }
        });
    }
    //called when building is placed
    public void startProductionandCosumption(){
        List<MProductions> productionsList = productions;
        if (productions!= null ) {
            for (int i = 0; i < productionsList.size(); i++) {
                MProductions production = productionsList.get(i);
                production.consumeAndProduce();
            }
        }
    }



    public EnumSet<EDirections> getPossibleConnections() {
        return possibleConnections;
    }

    public EnumSet<EDirections> getPossibleConnectionsSecondTile() {
        return possibleConnectionsSecondTile;
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

    public EnumSet<EDirections> getDirections() {
        return directions;
    }

    public EnumSet<EDirections> getDirectionsSecondTile() {
        return directionsSecondTile;
    }

    public void addCombinedBuilding(String b1, Buildings b2) {
        combinesBuildings.put(b1, b2);
    }

    public Map<String, Buildings> getCombinesBuildings() {
        return combinesBuildings;
    }

    public void setStartTile(MTile startTile){
        this.startTile = startTile;
    }

    public MTile getStartTile() {
        return startTile;
    }
}
