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

}
