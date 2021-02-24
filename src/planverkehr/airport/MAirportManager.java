package planverkehr.airport;

import javafx.scene.control.Alert;
import planverkehr.Buildings;
import planverkehr.MGame;

import java.util.ArrayList;
import java.util.List;

//verwaltet alle Airports; hier sind alle Airports gespeichert
public class MAirportManager {
    MGame model;
    List<MAirport> airports;

    public MAirportManager(MGame model) {
        this.model = model;
        airports = new ArrayList<>();
    }

    //wenn wir ein Airport-Gebäude setzen, dann soll dieses sich mit einem anderen Airport Building verknüpfen
    //oder einen vollständigen Airport erstellen falls es das letzte gebrauchte Gebäude ist
    //muss in Manager, weil wenn wir am Anfang keinen Airport haben, dann gibt es da keine airport zum verwenden
    //wenn am anfang kein airport existiert, dann kann man das nicht benutzen
    public boolean createOrConnectToAirport(Buildings newBuilding) {
        List<Buildings> neighbourBuildings = model.getNeighbourBuildings(newBuilding);
        List<MAirport> neighbourAirports = getNeighbourAirports(neighbourBuildings);
        String buildingName = newBuilding.getBuildingName();

        //1. Fall: es gibt noch keinen Airport, also machen wir einen neuen
        if (neighbourAirports.isEmpty()) {
            MAirport newAirport = new MAirport(model);
            addBuildingToAirport(newBuilding, newAirport, buildingName);
            airports.add(newAirport); //adden das in die ManagerListe die alle Airports hat
            return true;
        }
        //2.Fall: es gibt einen Nachbar Airport
        else if (neighbourAirports.size() == 1) {
            MAirport airportToConnectTo = neighbourAirports.get(0);
            if(checkForSpaceInAirport(airportToConnectTo, buildingName)){
                addBuildingToAirport(newBuilding, airportToConnectTo, buildingName);
                return true;
            }
        //3. Fall: wenn es mehr als 1 Airport in der NeighboursListe gibt,
            // dann soll man das Gebäude nicht setzen können, weil man nicht zwischen 2 Airports builden soll
        } else return false;

        return false;
    }

    //holen uns Airports der Nachbargebäude
    public List<MAirport> getNeighbourAirports(List<Buildings> neighbourBuildings) {
        List<MAirport> neighbourAirports = new ArrayList<>();

        for (Buildings currentBuilding : neighbourBuildings) {
            if (currentBuilding.getAssociatedAirport() != null) {
                MAirport currentAirport = currentBuilding.getAssociatedAirport();
                if (!neighbourAirports.contains(currentAirport)) neighbourAirports.add(currentAirport);
            }
        }
        return neighbourAirports;
    }


    //namen sind auf JSON abgestimmt, d.h. falls man diese im JSON anders schreibt, ist es cursed but oh well ya gotta live with it
    public void addBuildingToAirport(Buildings building, MAirport airport, String buildingName) {
        switch (buildingName) {
            case "tower" -> airport.setTower(building);
            case "big tower" -> airport.setBigTower(building);
            case "terminal" -> airport.setTerminal(building);
            case "runway" -> airport.setRunway(building);
            case "taxiway" -> airport.setTaxiway(building);
        }
        building.setAssociatedAirport(airport);
    }

    //Gebäude löschen aus Airport
    public void removeBuildingFromAirport(Buildings building, MAirport airport, String buildingName) {
        switch (buildingName) {
            case "tower" -> airport.removeTower();
            case "big tower" -> airport.removeBigTower();
            case "terminal" -> airport.removeTerminal();
            case "runway" -> airport.removeRunway();
            case "taxiway" -> airport.removeTaxiway();
        }
        building.setAssociatedAirport(null);
    }

    //check if Building is already there in the airport
    public boolean checkForSpaceInAirport(MAirport airport, String buildingName) {
        switch (buildingName) {
            case "tower":
                if (airport.getTower() == null) return true;
                break;
            case "big tower":
                if (airport.getBigTower() == null) return true;
                break;
            case "terminal":
                if (airport.getTerminal() == null) return true;
                break;
            case "runway":
                if (airport.getRunway() == null) return true;
                break;
            case "taxiway":
                if (airport.getTaxiway() == null) return true;
                break;
        }
        return false;
    }

    public void removeAirportFromList(MAirport airport){
        airports.remove(airport);
    }

    public void showAirportAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Bau-Error");
        alert.setHeaderText("Bau-Error");
        alert.setContentText("Der Flughafen ist bereits vollständig oder das Gebäude hält den Abstand zwischen zwei FLughäfen nicht ein.");

        alert.showAndWait();
    }
}
