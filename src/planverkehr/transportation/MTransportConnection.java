package planverkehr.transportation;

import javafx.scene.control.Alert;
import javafx.util.Pair;
import planverkehr.*;
import planverkehr.airport.MAirport;
import planverkehr.airport.MAirportManager;
import planverkehr.graph.Graph;
import planverkehr.graph.MKnotenpunkt;
import planverkehr.graph.MTargetpointList;

import java.util.*;

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
    MGame model;

    ArrayList<MTile> relevantTiles;
    Graph relevantGraph;

    public MTransportConnection(MTile feld, EBuildType buildingToBeBuiltType, Buildings buildingToBeBuilt, String newBuildingId, boolean relevantTilesFree, ArrayList<MTile> relevantTiles, Graph relevantGraph, boolean shouldAlwaysDraw, MTargetpointList relevantTargetpointlist, MGame model) {
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
        this.model = model;


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

            //6. Knotenpunkte verbinden
            connectKnotenpunkte();

            //5. check for TargetTypes
            checkForTargetTypes();



        } else {
            showAlert();
        }


    }

    private void checkForTargetTypes() {

        switch (buildingToBeBuilt.getSpecial()) {
            case "railstation" -> mKnotenpunktHashMap.forEach((id, knoten) -> {
                knoten.setTargetType(ESpecial.RAILSTATION);
                targetpointList.add(knoten);
                feld.setStation(true);
            });
            case "busstop" -> mKnotenpunktHashMap.forEach((id, knoten) -> {
                if (knoten.getName().equals("c")) {
                    knoten.setTargetType(ESpecial.BUSSTOP);
                    targetpointList.add(knoten);
                    feld.setStation(true);
                }
            });
            case "airport" -> mKnotenpunktHashMap.forEach((id, knoten) -> {
                knoten.setTargetType(ESpecial.AIRPORT);
                targetpointList.add(knoten);
            });

            case "signal" -> {
                mKnotenpunktHashMap.forEach((id, knoten) -> {
                    if (knoten.getName().equals("c")) {
                        knoten.setTargetType(ESpecial.SIGNAL);
                        findBlock(knoten);
                    }
                });


            }
        }
    }

    private void findBlock(MKnotenpunkt knotenpunkt) {
        MKnotenpunkt connection1 = knotenpunkt.getConnectedKnotenpunkteArray().get(0);
        MKnotenpunkt connection2 = knotenpunkt.getConnectedKnotenpunkteArray().get(1);

        if (knotenpunkt.getBlockId().equals(0)) {

            HashMap<String, MKnotenpunkt> registeredKnotenpunkte = findNodesForBlock(knotenpunkt, connection2);
            MKnotenpunkt connection2Neighbour = null;
            for (MKnotenpunkt c : connection2.getConnectedKnotenpunkteArray()
            ) {
                if (!c.getKnotenpunktId().equals(knotenpunkt.getKnotenpunktId())) {
                    connection2Neighbour = c;
                }
            }

            if (connection2Neighbour != null && registeredKnotenpunkte.containsKey(connection2Neighbour.getKnotenpunktId())) {
                registeredKnotenpunkte.put(connection2.getKnotenpunktId(), connection2);

            }
        } else {
            findNodesForBlock(knotenpunkt, connection2);
            findNodesForBlock(knotenpunkt, connection1);
        }
    }

    public HashMap<String, MKnotenpunkt> findNodesForBlock(MKnotenpunkt startNode, MKnotenpunkt forbiddenNode) {
        HashMap<String, MKnotenpunkt> registeredKnotenpunkte = new HashMap<>();
        Stack<MKnotenpunkt> geseheneKnotenpunkte = new Stack<>();
        geseheneKnotenpunkte.add(startNode);
        relevantGraph.increaseBlockId();

        while (!geseheneKnotenpunkte.isEmpty()) {
            MKnotenpunkt temp = geseheneKnotenpunkte.pop();
            //todo: setID
            temp.setBlockId(relevantGraph.getBlockId());
            registeredKnotenpunkte.put(temp.getKnotenpunktId(), temp);
            temp.getConnectedKnotenpunkteArray().forEach(k -> {
                if (!k.getTargetType().equals(ESpecial.SIGNAL) && !registeredKnotenpunkte.containsKey(k.getKnotenpunktId()) && !geseheneKnotenpunkte.contains(k) && k != forbiddenNode) {
                    geseheneKnotenpunkte.add(k);
                }
            });
        }

        return registeredKnotenpunkte;

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
            case airport -> connectionList = buildingToBeBuilt.getPlanes();
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
            //der gerade fertig gebaute Flughafen wird mit allen fully built Airports auf der Karte über Fluglinien verknüpft (von runway zu runway)
            if (buildingToBeBuilt.getEbuildType().equals(EBuildType.airport)) {
                if (buildingToBeBuilt.getAssociatedAirport().isFullyBuilt()) {
                    MAirportManager mAirportManager = model.getmAirportManager();
                    List<MAirport> fullyBuiltAirports = mAirportManager.getFullyBuiltAirports();

                    if (!fullyBuiltAirports.isEmpty()) {
                        MAirport airport = buildingToBeBuilt.getAssociatedAirport();
                        Buildings runway = airport.getRunway();
                        List<Pair<String, String>> connectionListRunway = runway.getPlanes();
                        double runwayTileX = runway.getStartTile().getVisibleCoordinates().getX();
                        double runwayTileY = runway.getStartTile().getVisibleCoordinates().getY();

                        String firstNode = connectionListRunway.get(0).getKey();
                        final MKnotenpunkt[] firstRunwayNode = new MKnotenpunkt[1];
                        MCoordinate firstCoord = runway.getPoints().get(firstNode);
                        MCoordinate firstGridCoord = new MCoordinate(runwayTileX + firstCoord.getX(), runwayTileY - firstCoord.getY(), firstCoord.getZ());

                        for (MAirport fullyBuiltAirport : fullyBuiltAirports) {
                            if (fullyBuiltAirport!= airport){ //need to make sure das er sich nich mit sich selbst verbindet
                            Buildings runwayToConnect = fullyBuiltAirport.getRunway();
                            List<Pair<String, String>> connectionListRunway2 = runwayToConnect.getPlanes();

                            String secondNode = connectionListRunway2.get(0).getKey();
                            MCoordinate secondCoord = runwayToConnect.getPoints().get(secondNode);
                            MCoordinate secondGridCoord = new MCoordinate(runwayTileX + secondCoord.getX(), runwayTileY - secondCoord.getY(), secondCoord.getZ());

                            runway.getStartTile().getNodeByCoordinatesString(firstGridCoord.toStringCoordinates()).ifPresentOrElse((node) -> {
                                firstRunwayNode[0] = node;
                            }, () -> firstRunwayNode[0] = mKnotenpunktHashMap.get(firstGridCoord.toStringCoordinates()));

                            final MKnotenpunkt[] secondRunwayNode = new MKnotenpunkt[1];

                            runway.getStartTile().getNodeByCoordinatesString(secondGridCoord.toStringCoordinates()).ifPresentOrElse((node) -> {
                                secondRunwayNode[0] = node;
                            }, () -> secondRunwayNode[0] = mKnotenpunktHashMap.get(secondGridCoord.toStringCoordinates()));

                            firstRunwayNode[0].addConnectedNode(secondRunwayNode[0]);
                            secondRunwayNode[0].addConnectedNode(firstRunwayNode[0]);
                        }
                        }
                    }
                }
            }
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
            if (mKnotenpunktHashMapSecondNode.size() > 0) {
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
        return relevantTilesFree || (isConnection && (!feld.isSchraeg() || buildingToBeBuilt.getDz() > 0));

    }

    private void createKnotenpunkteByBuildingPoints() {
        buildingToBeBuilt.getPoints().forEach((key, relCoords) -> {
            if (!isConnection || !currentBuilding.getPoints().containsKey(key)) {
                MKnotenpunkt punkt = createKnotenpunkt(relCoords, key);
                if (relCoords.isSecondTile()) {
                    mKnotenpunktHashMapSecondNode.put(punkt.getVisibleCoordinate().toStringCoordinates(), punkt);
                } else {
                    mKnotenpunktHashMap.put(punkt.getVisibleCoordinate().toStringCoordinates(), punkt);
                }
            }
        });
    }

    private MKnotenpunkt createKnotenpunkt(MCoordinate relCoords, String name) {
        String nodeId = "" + buildingToBeBuiltType + relevantGraph.getIncreasedId();
        MCoordinate feldVisibleCoordinates = feld.getVisibleCoordinates();

        if(feld.isSchraeg()){
            boolean westTief = true;
            if(feld.getWest().getZ() > feld.getLevel()){
                westTief = false;
            }
            MCoordinate firstCoord = null;
            MCoordinate secondCoord = null;
            double half = westTief ? 0.5 : -0.5;
            if (relCoords.getX() % 1 == 0) {

                for (MCoordinate c : feld.getPunkteNeu()
                ) {
                    if (c.getX() == relCoords.getX() && c.getY() == (relCoords.getY() - 0.5)) {
                        firstCoord = c;
                    } else if (c.getX() == relCoords.getX() && c.getY() == (relCoords.getY() + 0.5)) {
                        secondCoord = c;
                    }
                }
            } else if (relCoords.getY() % 1 == 0) {
                for (MCoordinate c : feld.getPunkteNeu()
                ) {
                    if (c.getX() == (relCoords.getX() - 0.5) && c.getY() == (relCoords.getY())) {
                        firstCoord = c;
                    } else if (c.getX() == relCoords.getX() + 0.5 && c.getY() == relCoords.getY()) {
                        secondCoord = c;
                    }
                }
            }
            if (firstCoord != null && secondCoord != null && firstCoord.getZ() == secondCoord.getZ()) {
                relCoords.setZ(firstCoord.getZ());
            } else {
                relCoords.setZ(half);
            }
        }


        MCoordinate nodeVisibleCoord = new MCoordinate(feldVisibleCoordinates.getX() + relCoords.getX(), feldVisibleCoordinates.getY() - relCoords.getY(), feld.getWest().getZ()+ relCoords.getZ());

        MKnotenpunkt knotenpunkt;

        if (relevantGraph.containsKey(nodeVisibleCoord.toStringCoordinates())) {
            knotenpunkt = relevantGraph.get(nodeVisibleCoord.toStringCoordinates());
            knotenpunkt.addGroupId(groupId);
        } else {
            knotenpunkt = new MKnotenpunkt(nodeId, groupId, nodeVisibleCoord, buildingToBeBuiltType, name, feld.getId(), relCoords.getRoadDirection(), relCoords.isEdge());
            relevantGraph.put(nodeVisibleCoord.toStringCoordinates(), knotenpunkt);
        }

        return knotenpunkt;

    }

    private void checkIsConnection() {
        isConnection = !feld.isFree() && !feld.getState().equals(EBuildType.water) && buildingToBeBuilt.getCombinesStrings().size() > 0 &&
            buildingToBeBuilt.getCombinesStrings().containsKey(feld.getConnectedBuilding().getBuildingName());
    }

    private void checkIsTwoTileTransport() {
        isTwoTileTransport = (newBuildingDepth > 1 || newBuildingWidth > 1);
    }


}
