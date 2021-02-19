package planverkehr.airport;

import planverkehr.Buildings;
import planverkehr.MGame;
import planverkehr.MTile;

import java.util.ArrayList;
import java.util.List;

//verwaltet alle Airports; hier sind alle Airports gespeichert
public class MAirportManager {
    MGame model;
    List<MAirport> airports;

    public MAirportManager(MGame model){
        this.model = model;
        airports = new ArrayList<>();
    }

    //wenn wir ein Airport-Gebäude setzen, dann soll dieses sich mit einem anderen Airport Building verknüpfen
    //oder einen vollständigen Airport erstellen falls es das letzte gebrauchte Gebäude ist
    //muss in Manager, weil wenn wir am Anfang keinen Airport haben, dann gibt es da keine airport zum verwenden
    //wenn am anfang kein airport existiert, dann kann man das nicht benutzen
    public void createOrConnectToAirport(Buildings newBuilding){

        List<Buildings> neighbourBuildings = model.getNeighbourBuildings(newBuilding);

        System.out.println("Neighbour Buildings: " + neighbourBuildings);

    }


}
