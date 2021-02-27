package planverkehr.airport;

import javafx.scene.control.Alert;
import planverkehr.Buildings;
import planverkehr.MGame;

import java.util.ArrayList;
import java.util.List;

/** Diese Klasse verwaltet alle Flughäfen und speichert diese in 2 Listen: eine allgemeine Airport Liste und eine mit nur vollständigen Airports. */

public class MAirportManager {
    MGame model;
    List<MAirport> airports, fullyBuiltAirports;

    public MAirportManager(MGame model) {
        this.model = model;
        airports = new ArrayList<>(); //hier sind ALLE Airports
        fullyBuiltAirports = new ArrayList<>(); //hier sind NUR fully built airports
    }

    /** Diese Methode verknüpft ein gebautes Airport Gebäude mit dem angrenzenden Flughafen oder erstellt einen neuen, sollte es keinen geben. */
    public boolean createOrConnectToAirport(Buildings newBuilding) {
        List<Buildings> neighbourBuildings = model.getNeighbourBuildings(newBuilding);
        List<MAirport> neighbourAirports = getNeighbourAirports(neighbourBuildings);
        String buildingName = newBuilding.getBuildingName();

        //1. Fall: es gibt noch keinen Airport, also machen wir einen neuen
        if (neighbourAirports.isEmpty()) {
            MAirport newAirport = new MAirport();
            addBuildingToAirport(newBuilding, newAirport, buildingName);
            airports.add(newAirport); //adden das in die ManagerListe die alle Airports hat
            return true;
        }
        //2. Fall: es gibt einen Nachbar Airport
        else if (neighbourAirports.size() == 1) {
            MAirport airportToConnectTo = neighbourAirports.get(0);
            if(checkForSpaceInAirport(airportToConnectTo, buildingName)){
                addBuildingToAirport(newBuilding, airportToConnectTo, buildingName);
                if(airportToConnectTo.isFullyBuilt()) fullyBuiltAirports.add(airportToConnectTo);
                return true;
            }
        //3. Fall: wenn es mehr als 1 Airport in der NeighboursListe gibt,
        //dann soll man das Gebäude nicht setzen können, weil man nicht zwischen 2 Airports builden soll
        } else return false;

        return false;
    }

    /** Gibt eine Liste aller an einem Gebäude angrenzenden Airports zurück. Nutzt dazu die Nachbargebäude des Buildings.*/
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


    /** Je nach Name des Gebäudes wird dieses an der entsprechenden Stelle im übergebenen Airport eingefügt.
     Im hinzugefügten Gebäude wird zusätzlich die Referenz auf dessen Airport hinzugefügt. */
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

    /** Je nach Name des Gebäudes wird dieses an der entsprechenden Stelle im übergebenen Airport gelöscht.
     Im hinzugefügten Gebäude wird zusätzlich die Referenz auf dessen Airport zurückgesetzt. */
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

    /** Prüft über den Namen, ob das übergebene Gebäude noch nicht im Airport referenziert ist und damit Platz hat. */
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


    public void removeFromAirportList(MAirport airport){
        airports.remove(airport);
    }

    public void removeFromFullyBuiltList(MAirport airport){
        fullyBuiltAirports.remove(airport);
    }

    /** Wird aufgerufen, wenn an das Gebäude mehr als ein Airport grenzt, d.h. wenn der Platz zwischen zwei Flughäfen ungenügend ist. */
    public void showAirportAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Bau-Error");
        alert.setHeaderText("Bau-Error");
        alert.setContentText("Der Flughafen ist bereits vollständig oder das Gebäude hält den Abstand zwischen zwei FLughäfen nicht ein.");

        alert.showAndWait();
    }

    public List<MAirport> getFullyBuiltAirports() {
        return fullyBuiltAirports;
    }
}
