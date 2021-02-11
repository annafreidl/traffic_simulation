package planverkehr;

import javafx.scene.input.MouseButton;
import javafx.geometry.Point2D;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import planverkehr.graph.Graph;
import planverkehr.graph.MKnotenpunkt;
import planverkehr.graph.MTargetpointList;
import planverkehr.transportation.MTransportConnection;

import java.util.ArrayList;



enum MouseMode {
    MOVE_UP, MOVE_DOWN
}

public class Controller {
    MGame gameModel;
    VGame gameView;
    Buildings newBuilding;

    public Controller(MGame gameModel, VGame gameView) {
        this.gameView = gameView;
        this.gameModel = gameModel;

        //  initTimeline();

        gameView.getVehicleButton().setOnAction(e -> {
            e.consume();
            gameModel.createVehicle();
            gameView.drawField();
        });


        gameView.getDefaultRoadButton().setOnAction(e -> {
            e.consume();
            Graph relevantGraph = gameModel.roadGraph;
            EBuildType buildingToBeBuiltType = EBuildType.road;
            boolean hasSpaceForBuilding = true;

            String newBuildingId = "busstop-nw-se";

            //BusStop
            Buildings buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            MTargetpointList relevantTargetpointlist = gameModel.listeDerBushaltestellen;

            for (int x = 0; x < 3; x++) {
                for (int y = 2; y < 8; y++) {
                    String tileId = x + "--" + y;
                    MTile feld = gameModel.getTileById(tileId);
                    ArrayList<MTile> relevantTiles = new ArrayList<>();
                    relevantTiles.add(feld);
                    if (x == 1 && (y == 2 || y == 4 || y == 7)) {
                        new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantTiles, relevantGraph, true, relevantTargetpointlist);
                    }
                }
            }

            newBuildingId = "busstop-ne-sw";

            //BusStop
            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            for (int x = 0; x < 3; x++) {
                for (int y = 2; y < 8; y++) {
                    String tileId = x + "--" + y;
                    MTile feld = gameModel.getTileById(tileId);
                    ArrayList<MTile> relevantTiles = new ArrayList<>();
                    relevantTiles.add(feld);
                    if ((x == 0 || x == 2) && (y == 3 || y == 5)) {
                        new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantTiles, relevantGraph, true, relevantTargetpointlist);
                    }
                }
            }


            //Road-ne
            newBuildingId = "road-ne";

            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            for (int x = 0; x < 3; x++) {
                for (int y = 2; y < 7; y++) {
                    String tileId = x + "--" + y;
                    MTile feld = gameModel.getTileById(tileId);
                    ArrayList<MTile> relevantTiles = new ArrayList<>();
                    relevantTiles.add(feld);
                    if (feld.getConnectedBuilding() == null || (!feld.getConnectedBuilding().getSpecial().equals("busstop"))) {
                        new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantTiles, relevantGraph, true, relevantTargetpointlist);
                    }
                }
                x++;
            }

            //Road-sw
            newBuildingId = "road-sw";

            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            for (int x = 0; x < 3; x++) {
                for (int y = 3; y < 8; y++) {
                    String tileId = x + "--" + y;
                    MTile feld = gameModel.getTileById(tileId);
                    ArrayList<MTile> relevantTiles = new ArrayList<>();
                    relevantTiles.add(feld);
                    if (feld.getConnectedBuilding() == null || (!feld.getConnectedBuilding().getSpecial().equals("busstop"))) {
                        new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantTiles, relevantGraph, true, relevantTargetpointlist);
                    }
                }
                x++;
            }

            //Road-nw
            newBuildingId = "road-nw";

            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);

            for (int x = 1; x < 3; x++) {
                for (int y = 2; y < 8; y++) {
                    String tileId = x + "--" + y;
                    MTile feld = gameModel.getTileById(tileId);
                    ArrayList<MTile> relevantTiles = new ArrayList<>();
                    relevantTiles.add(feld);
                    if ((y == 2 || y == 4 || y == 7) && (feld.getConnectedBuilding() == null || !feld.getConnectedBuilding().getSpecial().equals("busstop"))) {
                        new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantTiles, relevantGraph, true, relevantTargetpointlist);
                    }
                }
            }

            //Road-se
            newBuildingId = "road-se";

            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);

            for (int x = 0; x < 2; x++) {
                for (int y = 2; y < 8; y++) {
                    String tileId = x + "--" + y;
                    MTile feld = gameModel.getTileById(tileId);
                    ArrayList<MTile> relevantTiles = new ArrayList<>();
                    relevantTiles.add(feld);
                    if ((y == 2 || y == 4 || y == 7) && (feld.getConnectedBuilding() == null || !feld.getConnectedBuilding().getSpecial().equals("busstop"))) {
                        new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantTiles, relevantGraph, true, relevantTargetpointlist);
                    }
                }
            }


            gameView.drawField();

        });

        gameView.getDefaultRailButton().setOnAction(e -> {
            e.consume();
            Graph relevantGraph = gameModel.railGraph;
            EBuildType buildingToBeBuiltType = EBuildType.rail;
            boolean hasSpaceForBuilding = true;
            MTargetpointList relevantTargetpointlist = gameModel.listeDerBahnhöfe;

            //Geraden

            String newBuildingId = "rail-ne-sw";
            Buildings buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            for (int x = 1; x < 8; x++) {
                for (int y = 0; y < 10; y++) {
                    if (((x == 1 || x == 7) && (y == 0 || y == 1 || y == 4))) {
                        drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);
                    }
                }
            }
            newBuildingId = "rail-nw-se";
            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            for (int x = 1; x < 8; x++) {
                for (int y = 0; y < 10; y++) {
                    if (((x == 4) && (y == 1 || y == 7 || y == 9))) {
                        drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);
                    }
                }
            }

            //Switches
            newBuildingId = "railswitch-ne-s";
            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            int x = 1;
            int y = 2;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);

            newBuildingId = "railswitch-sw-e";
            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            x = 1;
            y = 5;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);

            newBuildingId = "railswitch-sw-n";
            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            x = 7;
            y = 5;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);

            newBuildingId = "railswitch-ne-w";
            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            x = 7;
            y = 2;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);

            //Kurven
            newBuildingId = "railcurve-se-n";
            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            x = 2;
            y = 1;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);

            newBuildingId = "railcurve-sw-n";
            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            x = 7;
            y = 7;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);

            newBuildingId = "railcurve-sw-e";
            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            x = 1;
            y = 7;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);


            newBuildingId = "railcurve-nw-e";
            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            x = 5;
            y = 1;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);

            newBuildingId = "railcurve-nw-s";
            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            x = 5;
            y = 7;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);

            x = 5;
            y = 9;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);

            newBuildingId = "railcurve-se-w";
            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            x = 2;
            y = 7;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);

            x = 2;
            y = 9;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);

//Eckverbindungen
            newBuildingId = "rail-nw-sw";
            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            x = 2;
            y = 2;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);

            newBuildingId = "rail-se-sw";
            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            x = 6;
            y = 2;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);

            newBuildingId = "rail-ne-se";
            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            x = 6;
            y = 6;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);

            x = 6;
            y = 8;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);

            newBuildingId = "rail-ne-nw";
            buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
            x = 2;
            y = 6;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);

            x = 2;
            y = 8;
            drawDefaultNode(x, y, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantGraph, relevantTargetpointlist);

            gameView.drawField();

        });

        gameView.getTickButton().setOnAction(e -> {
            e.consume();
            gameModel.moveVehicles();
            gameView.drawField();
        });


        //show coordinates
        gameView.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, e -> gameView.showCoordinates(e.getX(), e.getY()));

        //Build
        gameView.getMenuBar().getMenus().forEach(m -> {
            m.getItems().forEach(i -> {
                i.setOnAction(a -> {
                    a.consume();
                    String selectedTileId = gameModel.getSelectedTileId();

                    //Wenn ein Feld ausgewählt ist, dann füge den ausgewählten Gebäudetyp zum Feld hinzu
                    if (!selectedTileId.equals("null")) {
                        MTile feld = gameModel.getTileById(selectedTileId);
                        EBuildType buildingToBeBuiltType = EBuildType.valueOf(m.getId());
                        String newBuildingId = i.getId();
                        Buildings buildingToBeBuilt = gameModel.getBuildingById(newBuildingId);
                        boolean hasSpaceForBuilding = gameModel.hasSpaceForBuilding(buildingToBeBuilt.getWidth(), buildingToBeBuilt.getDepth(), buildingToBeBuilt.getDz());
                        ArrayList<MTile> relevantTiles = gameModel.getTilesToBeGrouped(buildingToBeBuilt.getWidth(), buildingToBeBuilt.getDepth(), buildingToBeBuilt.getDz() );


                        if (buildingToBeBuiltType.equals(EBuildType.rail) || buildingToBeBuiltType.equals(EBuildType.road)) {

                            Graph relevantGraph;
                            MTargetpointList relevantTargetpointlist;

                            switch (buildingToBeBuiltType) {
                                case rail -> {
                                    relevantGraph = gameModel.railGraph;
                                    relevantTargetpointlist = gameModel.listeDerBahnhöfe;
                                }
                                case road -> {
                                    relevantGraph = gameModel.roadGraph;
                                    relevantTargetpointlist = gameModel.listeDerBushaltestellen;
                                }
                                default -> {
                                    relevantGraph = gameModel.gameGraph;
                                    relevantTargetpointlist = gameModel.listeDerBushaltestellen;
                                    System.out.println("Achtung - falsche Targetpointlist ");
                                }
                            }
                            new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantTiles, relevantGraph, false, relevantTargetpointlist);


                        } else if (buildingToBeBuiltType.equals(EBuildType.factory) || buildingToBeBuiltType.equals(EBuildType.airport) || buildingToBeBuiltType.equals(EBuildType.nature)) {
                            feld.setState(buildingToBeBuiltType);
                            feld.setBuilding(i.getText()); //damit wissen wir welches Menu Ding genau wir angeklickt haben, e.g. chemical plant

                            newBuilding = new Buildings(buildingToBeBuilt); //new Building thats copied
                            feld.setBuildingOnTile(newBuilding);
                            feld.addConnectedBuilding(newBuilding);
                           // createKnotenpunktBuilding()
                            newBuilding.startProductionandCosumption();
                            createBuildingNodeByCenter(buildingToBeBuiltType);
                        }
                        gameView.drawField();
                        // gameModel.railGraph.print();
                    }
                });
            });
        });

        //select tiles
        gameView.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            double canvasX = e.getX();
            double canvasY = e.getY();



            // Umwandlung von Canvas-Panel-Koordinate
            // auf Canvas-Bild-Koordinate
            // (Beachten von Scaling, Translation)
            Point2D canvasPoint = this.gameView.canvas.sceneToLocal(canvasX, canvasY);
            canvasX = canvasPoint.getX();
            canvasY = canvasPoint.getY();

            // wenn linker MausButton gedrückt wird, lese MouseMode aus
            if (e.getButton() == MouseButton.SECONDARY) {

                String feldID = gameModel.getFeldIdByCanvasCoords(canvasX, canvasY);

            MTile feld = gameModel.getTileById(feldID);


                    // rufe Methode setHigh() je nach MouseMode auf
                    switch(gameView.getMouseMode()) {
                        case MOVE_UP: {
                            gameView.setHigh(feld, true);
                            break;
                        }
                        case MOVE_DOWN: {
                            gameView.setHigh(feld, false);
                            break;
                        }
                        default:
                            throw new IllegalStateException("Unexpected value: " + gameView.getMouseMode());
                    }
                    // zeichne Spielplan neu
                gameView.drawField();
                }

            if (e.getButton() == MouseButton.PRIMARY) {
                //select tiles
                boolean isTile = gameModel.selectTileByCoordinates(canvasX, canvasY);
                if (isTile) {
                    gameView.drawField();
                }
            }
        });

        //todo: remove stations from Targettypelist
        gameView.getRemoveButton().setOnAction(e -> {
            //   gameModel.removeKnotenpunkte();
            gameModel.resetTile();
            gameView.clearField();
            gameView.drawField();


        });

    }

    private void drawDefaultNode(int x, int y, EBuildType buildingToBeBuiltType, Buildings buildingToBeBuilt, String newBuildingId, boolean hasSpaceForBuilding, Graph relevantGraph, MTargetpointList relevantTargetpointlist) {
        String tileId = x + "--" + y;
        MTile feld = gameModel.getTileById(tileId);
        if (feld == null) {
            tileId = x + "-" + y;
            feld = gameModel.getTileById(tileId);
        }
        gameModel.selectedTileId = tileId;
        feld.isSelected = true;
        ArrayList<MTile> relevantTiles = gameModel.getTilesToBeGrouped(buildingToBeBuilt.getWidth(), buildingToBeBuilt.getDepth(), buildingToBeBuilt.getDz());

        Boolean hasSpace = gameModel.hasSpaceForBuilding(buildingToBeBuilt.getWidth(), buildingToBeBuilt.getDepth(), buildingToBeBuilt.getDz());

        new MTransportConnection(feld, buildingToBeBuiltType, buildingToBeBuilt, newBuildingId, hasSpaceForBuilding, relevantTiles, relevantGraph, true, relevantTargetpointlist);

    }

    private void initTimeline() {
        KeyFrame keyframe = new KeyFrame(Config.tickFrequency, handler);
        gameView.getTl().getKeyFrames().addAll(keyframe);
        gameView.getTl().setCycleCount(Timeline.INDEFINITE);


        gameView.getTl().play();
    }

    final EventHandler<ActionEvent> handler = event -> {
        gameModel.moveVehicles();
        gameView.drawField();
    };

    private void createBuildingNodeByCenter(EBuildType buildingToBeBuiltType) {

        String startPoint = gameModel.getSelectedTileId(); //get Startpoint
        MTile field = gameModel.getTileById(startPoint);
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
        if (newBuilding.getSpecial().equals("runway") || (newBuilding.getSpecial().equals("terminal") || (newBuilding.getSpecial().equals("taxiway")))){
            newBuilding.getPoints().forEach((name, point) -> {

            double finalPointX = startPointX + point.getX(); //points aus JSON auf Startpoint rechnen
            double finalPointY = startPointY + point.getY();

            MCoordinate finalCoords;
            finalCoords = new MCoordinate(finalPointX,finalPointY, level);

             MKnotenpunkt buildingPoint = createBuildingNode(finalCoords, name, buildingToBeBuiltType);
                System.out.println(buildingPoint);
                System.out.println("Hi");
            });

            //IF NOT THOSE 3, SET POINT TO SOUTHWEST
        } else { MKnotenpunkt buildingPoint = createBuildingNode(coords, key, buildingToBeBuiltType); }
    }

    private MKnotenpunkt createBuildingNode(MCoordinate coords, String name, EBuildType buildingToBeBuiltType) {

       // EBuildType buildingToBeBuiltType = EBuildType.factory;
       //EBuildType buildingType = EBuildType.factory; //we know we gonna need factory
        //GET BUILDTYPE DYNAMIC FROM BUILDING WE BUILD
        Graph buildingGraph = new Graph();

        //node ID setzt sich zusammen aus string, building type and Nummer (reihenfolge) (ID +1)
        String nodeId = "" +  buildingToBeBuiltType + buildingGraph.getIncreasedId();
        //System.out.println("nodeID:" + nodeId); //ID DOESNT GET INCREASED FOR SOME REASON

        String selectedTileId = gameModel.getSelectedTileId(); //getting the tile we selected

        //Wenn selected tile NICHT null ist, dann mach den Kram
        if (!selectedTileId.equals("null")) {
            MTile buildingField = gameModel.getTileById(selectedTileId);
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

}
