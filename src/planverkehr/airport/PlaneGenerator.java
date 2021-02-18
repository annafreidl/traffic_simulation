package planverkehr.airport;

import planverkehr.Buildings;
import planverkehr.Controller;
import planverkehr.EBuildType;
import planverkehr.MGame;

import java.util.*;

//TODO Die Airplanes brauchen die Fluglinie -> Fluglinie kann nur dann existieren, wenn 2 Flughäfen existieren; D.H: wir brauchen den FLughafen aka ModelAirport



public class PlaneGenerator {

    List<Generator> generatorAirplanes;
    Queue<MAirplane> planesQueue;
    MAirport mAirport;
    MGame mGame;
    Controller controller;
    EBuildType buildType;
    Buildings buildings;

    int initTIme;
    int planesOnAirport;
    int maxPlanes;
    Buildings airportBuilding;

    //OLD
/*    public PlaneGenerator(AirportController airportController)  {

        mAirport = airportController.mAirport;
        config = mAirport.config;
        planesQueue = config.getDefaultPlanesQueue();
        generatorAirplanes = config.getRandomPlanesToBeGenerated();
        maxPlanes = config.getMaxPlanes();
    }*/

    public PlaneGenerator() {

        initTIme = 0;
        planesOnAirport = 0;
        maxPlanes = 0;


        //  planesQueue = config.getDefaultPlanesQueue();
        //  generatorAirplanes = config.getRandomPlanesToBeGenerated();
        //  maxPlanes = config.getMaxPlanes();
    }


    public void spawnAirplanes() {

        maxPlanes = controller.getCurrentBuilding().getMaxPlanes(); //access a bit wonky rn, change later
        //int initTime = whenever we place Building

        if (planesOnAirport <= maxPlanes){

          //  MAirplane plane = new MAirplane(mAirport.getWaypointList(), 0, 1 + ((int) (Math.random() * 10)));

          //  mAirport.addPlaneToView(plane); //add plane to airport

           //IF NO SPACE
            //mAirport.addAirplanetoWaitQueue(plane); //add planes to Warteschleife wenn kein spacce




            planesOnAirport++; //every plane we spawn is counted here
        }


    }



    //TODO -------------------------------------------------------------

    /**
     * Spawnt die default Airplanes.
     Die Methode nimmt dabei das letzte Objekt der DefaultPlanes Queue, welche nach initTime geoordnet ist (kleinste zum Schluss).
     Ist die initTime des aktuell betrachteten Plane Objektes kleiner als die TickNumber UND im Airport gibt es Platz fuer neue Planes,
     wird das Flugzeug erstellt und auf die "Warteliste" der available Airplanes gesetzt. Diese Airplanes betreten erst dann den Flughafen, wenn
     der Platz freigeben wird (dabei werden sie im Airport Model spaeter auf die visiblePlanesList gesetzt und so in der view angezeigt).
     Danach entfernen wir das aktuelle Flugzeugobjekt aus der Queue, da wir sonst immmer das gleiche Flugzeug spawnen.
     */

    //ALLE AIRPLANES SIND DEFAULT AIRPLANES, NO MORE CHANCES, s. unten

/*    public void spawnDefaultAirplanes(int tickNumber) {
        System.out.println(tickNumber);

        if (!planesQueue.isEmpty()){
            MAirplane smallestInitTimePlane = planesQueue.peek(); //Plane to spawn

            if ((smallestInitTimePlane.initTime <= tickNumber) && (mAirport.hasSpaceForNewPlane())) {
                mAirport.addAirplaneToAvailableAirplaneQueue(smallestInitTimePlane);
                planesQueue.remove(smallestInitTimePlane);
            }
        }
    }//end method*/

    /**
     * Generieren der Generator Airplanes.
     *
     Die Methode iteriert ueber die GeneratorAirplane Liste, wobei fuer jedes Airplane dessen Chance gespeichert wird.
     In der If Bedingung wird anschließend geprueft, ob diese Chance getroffen wird. Wenn ja, wird das Flugzeug erstellt
     und auf die "Warteliste" der available Airplanes gesetzt. Diese Airplanes betreten erst dann den Flughafen, wenn
     der Platz freigeben wird (dabei werden sie im Airport Model spaeter auf die visiblePlanesList gesetzt und so in der view angezeigt).
     */

    //ELEMENT CHANCE GIBT ES NICHT MEHR, WERDEN VOM PLANVERKEHR GESPaWNED

   /* public void generateAirplanes(int tickNumber) {

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
    */


    //HAS TO BE ADJUSTED TO NEW CONDITIONS

 /*   public void addPlanesToVisiblePlaneList(int tickNumber){
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
    }*/

}//end Class


