package planverkehr;

import javafx.util.Pair;
import planverkehr.transportation.EDirections;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

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
    private java.util.Map<String, Object> combines;
    private List<Pair<String, String>> roads;
    private List<Pair<String, String>> rails;
    private List<Pair<String, String>> planes;
    private List<Object> productions;

    private EnumSet<EDirections> possibleConnections;
    private EnumSet<EDirections> directions;

    //Verbindungen des Zweiten Gebäudeteils
    private EnumSet<EDirections> possibleConnectionsSecondTile;
    private EnumSet<EDirections> directionsSecondTile;
    private EBuildType buildType;
    //todo: buildType hinzufügen


    public Buildings(String buildingName, String buildMenu, int width, int depth, java.util.Map<String, MCoordinate> points,
                     List<Pair<String, String>> roads, List<Pair<String, String>> rails,
                     List<Pair<String, String>> planes, int dz, String special, int maxPlanes,
                     java.util.Map<String, Object> combines, List<Object> productions) {

        this.buildingName = buildingName;
        this.buildMenu = buildMenu;
        this.width = width;
        this.depth = depth;
        this.combines = combines;
        this.points = points;
        this.roads = roads;
        this.rails = rails;
        this.planes = planes;
        this.dz = dz;
        this.special = special;
        this.maxPlanes = maxPlanes;
        this.productions = productions;
        setBuildType();
        setDirections();
        setPossibleConnection();
    }

    private void setBuildType() {

    }

    private void setDirections() {
        this.directions = EnumSet.noneOf(EDirections.class);
        this.directionsSecondTile = EnumSet.noneOf(EDirections.class);
        if (this.getPoints().size() > 0) {
            this.getPoints().forEach((key, coord) -> {
                if (coord.isEdge()) {
                    MCoordinate temp = new MCoordinate(coord.getX(), coord.getY());

                    if (temp.isSecondTile()) {

                        this.directionsSecondTile.add(temp.getRoadDirection());
                    } else {
                        this.directions.add(coord.getRoadDirection());
                    }

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
                case nw -> relevantConnectionSet.add(EDirections.se);
                case ne -> relevantConnectionSet.add(EDirections.sw);
                case se -> relevantConnectionSet.add(EDirections.nw);
                case sw -> relevantConnectionSet.add(EDirections.ne);
            }
        });
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

    public Map<String, Object> getCombines() {
        return combines;
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

    public List<Object> getProductions() {
        return productions;
    }

    public EnumSet<EDirections> getDirections() {
        return directions;
    }

    public EnumSet<EDirections> getDirectionsSecondTile() {
        return directionsSecondTile;
    }
}
