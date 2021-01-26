package planverkehr;

import planverkehr.graph.MKnotenpunkt;

import java.util.HashMap;
import java.util.Random;

public class MVehicles {

    private String name;
    private String kind;
    private String graphic;
    private HashMap cargo;
    private double speed;
    int id;
    MCoordinate currentPosition;
    MKnotenpunkt currentKnotenpunkt;


    public MVehicles(String name, String kind, String graphic, HashMap cargo, double speed) {
        this.name = name;
        this.kind = kind;
        this.graphic = graphic;
        this.cargo = cargo;
        this.speed = speed;
    }


    public String getName() {
        return name;
    }

    public String getKind() {
        return kind;
    }

    public String getGraphic() {
        return graphic;
    }

    public HashMap getCargo() {
        return cargo;
    }

    public double getSpeed() {
        return speed;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCurrentPosition(MCoordinate currentPosition) {
        this.currentPosition = currentPosition;
    }

    public MCoordinate getCurrentPosition() {
        return currentPosition;
    }

    public int getId() {
        return id;
    }

    public void setCurrentKnotenpunkt(MKnotenpunkt roadKnotenpunkt) {
        this.currentKnotenpunkt = roadKnotenpunkt;
        currentPosition = roadKnotenpunkt.getGridCoordinate();
    }

    public MKnotenpunkt getNextKnotenpunkt() {
        int numOfConnections = currentKnotenpunkt.getConnectedKnotenpunkteArray().size();
        if(numOfConnections > 0) {
            Random zufall = new Random(); // neues Random Objekt, namens zufall
            int zufallsZahl = zufall.nextInt(currentKnotenpunkt.getConnectedKnotenpunkteArray().size() + 1);
            zufallsZahl = zufallsZahl == numOfConnections ? zufallsZahl - 1 : zufallsZahl;
            return currentKnotenpunkt.getConnectedKnotenpunkteArray().get(zufallsZahl);
        } else {
            System.out.println("keine Connections vorhanden");
            System.out.println(currentKnotenpunkt);
            return currentKnotenpunkt.getConnectedKnotenpunkteArray().get(0);
        }

    }

    @Override
    public String toString() {
        return "MVehicles{" +
            "id=" + id +
            ", currentKnotenpunkt=" + currentKnotenpunkt +
            '}';
    }
}