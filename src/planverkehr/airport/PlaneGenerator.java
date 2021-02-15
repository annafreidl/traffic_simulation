package planverkehr.airport;

import java.util.*;

/*

public class PlaneGenerator {

    final List<Generator> generatorAirplanes;
    final Queue<MAirplane> planesQueue;
    final MAirport mAirport;
    final AirportConfig config;
    final int maxPlanes;



    public PlaneGenerator(AirportController airportController)  {

        mAirport = airportController.mAirport;

        config = mAirport.config;

        planesQueue = config.getDefaultPlanesQueue();
        generatorAirplanes = config.getRandomPlanesToBeGenerated();

        maxPlanes = config.getMaxPlanes();
    }




    public void spawnDefaultAirplanes(int tickNumber) {
        System.out.println(tickNumber);

        if (!planesQueue.isEmpty()){
            MAirplane smallestInitTimePlane = planesQueue.peek(); //Plane to spawn

            if ((smallestInitTimePlane.initTime <= tickNumber) && (mAirport.hasSpaceForNewPlane())) {
                mAirport.addAirplaneToAvailableAirplaneQueue(smallestInitTimePlane);
                planesQueue.remove(smallestInitTimePlane);
            }
        }
    }//end method



    public void generateAirplanes(int tickNumber) {

        //current Airplane in for loop
        for (Generator currentGenerator : generatorAirplanes) {
            double chance = currentGenerator.chance;

            //Chance Calc; if we hit Chance, we generate plane
            if (new Random().nextDouble() <= chance) {
                System.out.println("Plane generated with " + chance * 100 + "% Chance!");
                int idBase = tickNumber;
                if (tickNumber > 200) {
                    idBase = (idBase - 200) / 2;
                }

                MAirplane plane = new MAirplane(currentGenerator.waypoints, tickNumber, tickNumber + ((int) (Math.random() * 10)));
                mAirport.addAirplaneToAvailableAirplaneQueue(plane);
            }
        }
    }//end method

    public void addPlanesToVisiblePlaneList(int tickNumber){
        //Verfügbare Einflugknoten bestimmen und nur dann Flugzeug anzeigen, wenn Knoten frei sind
        TargetpointList einflugNodes = mAirport.getEinflugList().clone();
        TargetpointList waitNodes = mAirport.getWaitList().clone();
        einflugNodes.filterByBlocked();
        waitNodes.filterByBlocked();

        int freeSpots = einflugNodes.size();
        Queue<MAirplane> availablePlanes = mAirport.getAvailableAirplaneQueue();
        System.out.println(availablePlanes.size());
        //Füge Flugzeug aus Queue auf Flughafen hinzu wenn:
        // 1. Maximale Anzahl an Flugzeugen nicht erreicht ist.
        // 2. Es einen freien Einflugknotenpunkt gibt.
        // 3. Wenn es ein Flugzeug in der Queue gibt
        // 4. Wenn das Flugzeug in der Queue eine iniTime von tickNumber oder kleiner hat
        while (

            mAirport.hasSpaceForNewPlane() &&
                freeSpots > 0 &&
                availablePlanes.size() > 0 &&
                availablePlanes.peek().initTime <= tickNumber) {

            //Holt sich erstes Plane aus der Queue
            MAirplane plane = availablePlanes.peek();


            boolean tryPath = true;

            //Sucht nach erstem Ziel nach Einflug
            String target = plane.wayPointList.get(1);
            TargetpointList wayPointList = mAirport.getWaypointList().clone();
            wayPointList.filterByName(target);



            //Wenn Zielknoten nicht erreichbar ist, dann den waitKnoten als nächstes Ziel auswählen
            if (wayPointList.isEmpty()) {
                wayPointList = mAirport.getWaypointList().clone();
                wayPointList.filterByName("wait");
            }

            boolean changedWaypoint = false;

            if (wayPointList.isEmpty()) {
                tryPath = false;
            } else if (wayPointList.get(0).targetType.equals("wait")) {
                plane.wayPointList.add(1, "wait");
                target = "wait";
                changedWaypoint = true;
            }

            //Alle einflugsknoten als mögliche Startknoten ausprobieren
            for (int i = 0; i < einflugNodes.size() && tryPath; i++) {


                SearchObject searchObject = new SearchObject(einflugNodes.get(i), wayPointList, tickNumber, waitNodes);

                if(searchObject.getArrayListZuBesuchenderWegpunkte().size() == 0){
                    System.out.println("something went wrong");
                }
                //Solange wir noch nach dem Zielpunkt suchen:
                Path path = new Path(searchObject);



                //Wenn ein Pfad gefunden werden konnte:
                if (!path.noPathFound) {
                    //Einflugszielknoten entfernen
                    plane.removeWayPoint();

                    //Berechneten Knoten als Startknoten festlegen
                    plane.setCurrentNode(einflugNodes.get(i));
                    System.out.println("current node set");

                    //Weg zum Flugzeug hinzufügen

                    plane.setPathStack(path, tickNumber);
                    mAirport.addAirplaneToVisiblePlanesList(plane);
                    freeSpots--;

                    tryPath = false;
                }
            }
            if (plane.currentNode == null) {
                freeSpots = 0;
                System.out.println("no way found");
                if (changedWaypoint) {
                    plane.wayPointList.remove(1);
                }

            }


            else availablePlanes.poll();
        }
    }

}//end Class

*/
