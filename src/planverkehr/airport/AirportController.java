package planverkehr.airport;

/*
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;




public class AirportController {
    final MAirport mAirport;
    final VAirport view;
    int tickNumber = 0;


    private static final Duration TICK_FREQUENCY = Duration.seconds(0.5);
    final PlaneGenerator planeGenerator;


    public AirportController(MAirport mAirport, VAirport view) {
        this.mAirport = mAirport;
        this.view = view;

        planeGenerator = new PlaneGenerator(this);

//        //Action Handler für neues Flugzeug --> führt Methode createPlane aus
//        this.view.getNewPlaneButton().setOnAction(actionEvent -> {
//
//            MAirplane plane = new MAirplane(new String[]{"einflug", "gateway", "ausflug"}, tickNumber, tickNumber + ((int) (Math.random() * 10)));
//            mAirport.addAirplaneToAvailableAirplaneQueue(plane);
//
//        });

        //startet Timeline = 1 Tick p sec
        initTimeline();
    }



    final EventHandler<ActionEvent> handler = event -> runTick();


    private void initTimeline() {
        KeyFrame keyframe = new KeyFrame(TICK_FREQUENCY, handler);
        Timeline tl = new Timeline();
        tl.getKeyFrames().addAll(keyframe);
        tl.setCycleCount(Timeline.INDEFINITE);

        tl.play();
    }


    private void movePlane(MAirplane plane) {
        //der nächste Knotenpunkt wird bestimmt
        plane.currentNode.blockedForTickList.pollFirst();
        Knotenpunkt nextNode = plane.removeLastNodeFromPathList().knotenpunkt;
        nextNode.blockedForTickList.pollFirst();

        //Bewegt das Flugzeug zum nächsten Punkt
        //plane.movePlane(nextNode);
        view.movePlane(nextNode, plane);

        //Setzt den nächsten Knotenpunkt als den aktuellen Knotenpunkt des Flugzeugs
        plane.setCurrentNode(nextNode);
    }


    // In runTick werden alle Aktionen, die innerhalb eines Ticks durchgeführt werden müssen aufgerufen.
    private void runTick() {

        //Erzeugt Flugzeuge
        // planeGenerator.spawnDefaultAirplanes(tickNumber);
        planeGenerator.generateAirplanes(tickNumber);
        planeGenerator.addPlanesToVisiblePlaneList(tickNumber);


        //Die Aktionen werden für jedes Flugzeug einzeln bestimmt, deshalb wird als erstes durch die AirplanesList iteriert.
        // Da sich die AirplaneList verändern kann (bpw. ein Eintrag gelöscht wird), wird ein Iterator verwendet
        ArrayList<MAirplane> airplaneList = mAirport.getVisibleAirplaneList();
        for (Iterator<MAirplane> it = airplaneList.iterator(); it.hasNext(); ) {
            //Das nächste Flugzeug wird ausgewählt
            MAirplane plane = it.next();


            //Wenn Flugzeug noch nicht zu sehen ist, zeige es auf der Karte an
            if (!plane.isVisible) {
                view.showPlane(plane);
                plane.setVisible(true);
            }

            //Wenn das Flugzeug keine Wegknotenpunkte mehr in seiner Liste hat, ist es beim aktuellen Ziel angekommen.
            // Je nach Ziel werden verschiedene Aktionen durchgeführt
            //1. Ausflug: der Flugzeug wird von der Oberfläche entfernt
            //2. wait: Es such ob es zu seinem nächsten Ziel kommen kann, wenn nicht wartet es weiter
            //3. alle anderen: Ziel aus Zielliste entfernen und Knotenpunkt nichtmehr blockieren, neues Ziel suchen und Flugzeug bewegen

            if (plane.pathStack.isEmpty()) {

                switch (plane.currentNode.targetType) {
                    //Handelt es sich um den Ausflugsknoten, wird das Flugzeug entfernt
                    case "ausflug":
                        plane.removeWayPoint();
                        plane.currentNode.setFree();
                        view.removePlane(plane);
                        mAirport.numOfPlanesOnAirport--;
                        it.remove();
                        break;
                    //Handelt es sich um den Warteknoten wird geprüft ob das eigentlich nächste Ziel erreichbar ist
                    case "wait":
                        String wayPointString = plane.wayPointList.get(1);
                        TargetpointList waypoints = mAirport.getWaypointList().clone();
                        waypoints.filterByName(wayPointString);
                        waypoints.filterByBlocked();

                        if (waypoints.isEmpty()) {
                            waitPlane(plane);
                        } else {
                            plane.removeWayPoint();
                            plane.currentNode.setFree();

                            SearchObject so = new SearchObject(plane.currentNode, waypoints, tickNumber, mAirport.getWaitList());

                            Path path = new Path(so);
                            plane.setPathStack(path, tickNumber);
                            movePlane(plane);
                        }
                        break;
                    case "noTargetType":
                        break;
                    default:
                        if (!plane.currentNode.isTempTarget) {
                            plane.removeWayPoint();
                            plane.currentNode.setFree();
                        }

                        TargetpointList wayPointList = mAirport.getWaypointList().clone();
                        String target = wayPointList.get(0).targetType;
                        wayPointList.getReadyForSearch(mAirport.getWaitList(), plane);

                        SearchObject so = new SearchObject(plane.currentNode, wayPointList, tickNumber, mAirport.getWaitList());

                        if (so.getArrayListZuBesuchenderWegpunkte().size() == 0) {
                            System.out.println("something went wrooong");
                        }

                        Path path = new Path(so);

                        if (plane.currentNode.isTempTarget && !path.lastElement().knotenpunkt.targetType.equals(target)) {
                            waitPlane(plane);
                        } else {
                            if(so.isTempTarget()){
                                plane.wayPointList.add(0, so.getCurrentNodeKnotenpunkt().targetType);
                            } else {
                                plane.currentNode.isTempTarget = false;
                            }
                            plane.setPathStack(path, tickNumber);
                            movePlane(plane);
                        }
                }

                //bis hier

            } else {
                //Hier könnte die Animation für die Aussteigenden passagiere stehen
                // plane.currentNode.targetType.equals("gateway") gibt bei true wenn wir am Gateway stehen

                movePlane(plane);
            }
        }

        tickNumber++;
    }

    private void waitPlane(MAirplane plane) {
        view.movePlane(plane.currentNode, plane);
    }
}
*/