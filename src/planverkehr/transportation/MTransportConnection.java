package planverkehr.transportation;

import javafx.scene.control.Alert;
import javafx.util.Pair;
import planverkehr.Buildings;
import planverkehr.EBuildType;
import planverkehr.MCoordinate;
import planverkehr.MTile;
import planverkehr.graph.Graph;
import planverkehr.graph.MKnotenpunkt;
import planverkehr.graph.MTargetpointList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MTransportConnection {
    MTile feld;
    EBuildType feldBuildingType;
    EBuildType buildingToBeBuiltType;
    String buildingToBeBuiltID;
    Buildings buildingToBeBuilt, currentBuilding;
    int newBuildingDepth;
    int newBuildingWidth;
    String groupId;
    HashMap<String, MKnotenpunkt> mKnotenpunktHashMap; //rename to newKnotenpunktMap
    HashMap<String, MKnotenpunkt> mKnotenpunktHashMapSecondNode; //rename to newKnotenpunktMap
    Map<String, MCoordinate> newPoints;
    boolean isTwoTileTransport, isConnection, relevantTilesFree, shouldAlwaysDraw;
    MTargetpointList targetpointList;

    ArrayList<MTile> relevantTiles;
    Graph relevantGraph;

    public MTransportConnection(MTile feld, EBuildType buildingToBeBuiltType, Buildings buildingToBeBuilt, String newBuildingId, boolean relevantTilesFree, ArrayList<MTile> relevantTiles, Graph relevantGraph, boolean shouldAlwaysDraw, MTargetpointList relevantTargetpointlist) {
        this.feld = feld;
        feldBuildingType = feld.getState();
        this.buildingToBeBuiltType = buildingToBeBuiltType;
        this.buildingToBeBuiltID = newBuildingId;
        this.buildingToBeBuilt = buildingToBeBuilt;
        newBuildingDepth = buildingToBeBuilt.getDepth();
        newBuildingWidth = buildingToBeBuilt.getWidth();
        this.relevantTiles = relevantTiles;
        this.relevantGraph = relevantGraph;
        this.shouldAlwaysDraw = shouldAlwaysDraw;
        this.targetpointList = relevantTargetpointlist;

        groupId = feld.getId() + "-" + buildingToBeBuiltID;
        mKnotenpunktHashMap = new HashMap<>();
        mKnotenpunktHashMapSecondNode = new HashMap<>();
        this.relevantTilesFree = relevantTilesFree;


        checkIsTwoTileTransport();

        checkIsConnection();

        //1. Prüfen ob Platz für Straße ist
        //todo: dz und Haltestellen einbeziehen
        if (checkForSpace()) {

            //2. State und Building von Kachel setzten
            setTileStateAndConnectBuilding();

            //3. Koordinaten abhängig von Points erstellen und zum Graph hinzufügen
            createKnotenpunkteByBuildingPoints();

            //4. Koordinaten zum Feld hinzufügen
            addKnotenpunktToFeldAndMergeMaps();

            //5. check for TargetTypes
            checkForTargetTypes();

            //6. Knotenpunkte verbinden
            connectKnotenpunkte();

        }
        else {
            showAlert();
        }


    }

    private void checkForTargetTypes() {

        switch (buildingToBeBuilt.getSpecial()){
            case "railstation" -> mKnotenpunktHashMap.forEach((id, knoten) -> {
                knoten.setTargetType(ESpecial.RAILSTATION);
                targetpointList.add(knoten);
            });
            case "busstop" -> mKnotenpunktHashMap.forEach((id, knoten) -> {
                if(knoten.getName().equals("c")) {
                    knoten.setTargetType(ESpecial.BUSSTOP);
                    targetpointList.add(knoten);
                }
            });
            case "airport" -> mKnotenpunktHashMap.forEach((id, knoten) -> {
                knoten.setTargetType(ESpecial.AIRPORT);
                targetpointList.add(knoten);
            });
            case "signal" -> mKnotenpunktHashMap.forEach((id, knoten) -> knoten.setTargetType(ESpecial.SIGNAL));


        }
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Bau-Error");
        alert.setHeaderText("Bau-Error");
        alert.setContentText("Für das ausgewählte Gebäude ist hier kein Platz");

        alert.showAndWait();
    }

    private void connectKnotenpunkte() {
        double feldX = feld.getVisibleCoordinates().getX();
        double feldY = feld.getVisibleCoordinates().getY();
        List<Pair<String, String>> connectionList = null;


        switch (buildingToBeBuiltType) {
            case rail -> connectionList = buildingToBeBuilt.getRails();
            case road -> connectionList = buildingToBeBuilt.getRoads();
            case airport -> connectionList = buildingToBeBuilt.getPlanes();   /** Morgen fragen, ob getPlanes richtig **/
        }

        if (connectionList != null) {
            connectionList.forEach((pair) -> {
                String firstNodeName = pair.getKey();
                String secondNodeName = pair.getValue();
                final MKnotenpunkt[] firstNode = new MKnotenpunkt[1];


                MCoordinate firstCoord = buildingToBeBuilt.getPoints().get(firstNodeName);
                MCoordinate secondCoord = buildingToBeBuilt.getPoints().get(secondNodeName);

                MCoordinate firstGridCoord = new MCoordinate(feldX + firstCoord.getX(), feldY - firstCoord.getY(), firstCoord.getZ());
                MCoordinate secondGridCoord = new MCoordinate(feldX + secondCoord.getX(), feldY - secondCoord.getY(), secondCoord.getZ());


                feld.getNodeByCoordinatesString(firstGridCoord.toStringCoordinates()).ifPresentOrElse((node) -> {
                    firstNode[0] = node;
                }, () -> firstNode[0] = mKnotenpunktHashMap.get(firstGridCoord.toStringCoordinates()));

                final MKnotenpunkt[] secondNode = new MKnotenpunkt[1];

                feld.getNodeByCoordinatesString(secondGridCoord.toStringCoordinates()).ifPresentOrElse((node) -> {
                    secondNode[0] = node;
                }, () -> secondNode[0] = mKnotenpunktHashMap.get(secondGridCoord.toStringCoordinates()));


                firstNode[0].addConnectedNode(secondNode[0]);
                secondNode[0].addConnectedNode(firstNode[0]);
            });
        } else {
            System.out.println("connectNewNodes: unknown transportation");
        }

    }

    private void addKnotenpunktToFeldAndMergeMaps() {
        mKnotenpunktHashMap.forEach(((s, mKnotenpunkt) -> feld.addKnotenpunkt(mKnotenpunkt)));
        if (relevantTiles != null) {
            for (MTile relevantTile : relevantTiles) {
                if (!relevantTile.getIsSelected() && !shouldAlwaysDraw) {
                    mKnotenpunktHashMapSecondNode.forEach(((s, mKnotenpunkt) -> relevantTile.addKnotenpunkt(mKnotenpunkt)));
                }
            }
            if(mKnotenpunktHashMapSecondNode.size() > 0) {
                mKnotenpunktHashMap.putAll(mKnotenpunktHashMapSecondNode);
            }
        }

    }

    private void setTileStateAndConnectBuilding() {

        if (isConnection) {
            String currentBuildingId = feld.getConnectedBuilding().getBuildingName();
            currentBuilding = feld.getConnectedBuilding();
            buildingToBeBuilt = buildingToBeBuilt.getCombinesBuildings().get(currentBuildingId);
        }


        if (relevantTiles != null) {
            for (MTile relevantTile : relevantTiles) {
                relevantTile.setState(buildingToBeBuiltType);

                //Koordinaten sollen nicht gezeichnet werden, wird vom Hauptfeld übernommen
                if (!relevantTile.getIsSelected() && !shouldAlwaysDraw) {
                    relevantTile.setFirstTile(false);
                    relevantTile.addConnectedBuilding(buildingToBeBuilt);
                }
            }
        }

        feld.addConnectedBuilding(buildingToBeBuilt);

    }

    private boolean checkForSpace() {
        return relevantTilesFree || isConnection;

    }

    private void createKnotenpunkteByBuildingPoints() {
        buildingToBeBuilt.getPoints().forEach((key, coord) -> {
            if (!isConnection || !currentBuilding.getPoints().containsKey(key)) {
                MKnotenpunkt punkt = createKnotenpunkt(coord, key);
                if (coord.isSecondTile()) {
                    mKnotenpunktHashMapSecondNode.put(punkt.getGridCoordinate().toStringCoordinates(), punkt);
                } else {
                    mKnotenpunktHashMap.put(punkt.getGridCoordinate().toStringCoordinates(), punkt);
                }
            }
        });

    }

    private MKnotenpunkt createKnotenpunkt(MCoordinate coords, String name) {
        String nodeId = "" + buildingToBeBuiltType + relevantGraph.getIncreasedId();
        MCoordinate feldGridCoordinates = feld.getVisibleCoordinates();



        MCoordinate nodeCoordinate = new MCoordinate(feldGridCoordinates.getX() + coords.getX(), feldGridCoordinates.getY() - coords.getY(), feld.getLevel());

        MKnotenpunkt knotenpunkt;

        if (relevantGraph.containsKey(nodeCoordinate.toStringCoordinates())) {
            knotenpunkt = relevantGraph.get(nodeCoordinate.toStringCoordinates());
            knotenpunkt.addGroupId(groupId);
        } else {
            knotenpunkt = new MKnotenpunkt(nodeId, groupId, nodeCoordinate, buildingToBeBuiltType, name, feld.getId(), coords.getRoadDirection(), coords.isEdge());
            relevantGraph.put(nodeCoordinate.toStringCoordinates(), knotenpunkt);
        }

        return knotenpunkt;

    }

    private void checkIsConnection() {
        isConnection = !feld.isFree() && buildingToBeBuilt.getCombinesStrings().size() > 0 &&
            buildingToBeBuilt.getCombinesStrings().containsKey(feld.getConnectedBuilding().getBuildingName());
    }

    private void checkIsTwoTileTransport() {
        isTwoTileTransport = (newBuildingDepth > 1 || newBuildingWidth > 1);
    }


}
