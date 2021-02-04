package planverkehr;

import planverkehr.graph.MKnotenpunkt;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MVehicles {

    private String name;
    private String kind;
    private String graphic;
    private HashMap<String, Integer> cargo;
    private HashMap<String, Integer> cargoCurrent;
    private double speed;
    int id;
    MCoordinate currentPosition;
    MKnotenpunkt currentKnotenpunkt;


    public MVehicles(String name, String kind, String graphic, HashMap<String, Integer> cargo, double speed) {
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

    private void zeroStorage() {
        Set<String> keys = cargo.keySet();
        cargoCurrent = new HashMap<>();
        for (String key : keys) {
            cargoCurrent.put(key, 0);
        }
    }

    private boolean spaceForResources(HashMap<String, Integer> transfer) {
        for (Map.Entry<String, Integer> entry : transfer.entrySet()) {
            String resource = entry.getKey();
            int quantity = entry.getValue();
            if (cargo.containsKey(resource)) {
                int currentQuantity = cargoCurrent.get(resource);
                int originalSpace = cargo.get(resource);
                int availableSpace = originalSpace - currentQuantity;
                if (quantity <= availableSpace) {
                    System.out.println("Enough space");
                    return true;
                } else {
                    System.out.println("Not enough space");
                    return false;
                }
            } else {
                System.out.println("Vehicle can not store this resource!");
                return false;
            }
        }
        return false;
    }


    private void addGoodsToCurrentCargo(String resource, int quantity) {
        cargoCurrent.put(resource, cargoCurrent.containsKey(resource) ? cargoCurrent.get(resource) + quantity : quantity);
    }

    private void deleteGoodFromCurrentCargo(String resource, int quantity) {
        cargoCurrent.put(resource, cargoCurrent.containsKey(resource) ? cargoCurrent.get(resource) - quantity : quantity);
    }

    private void takeGoodsFromFactory(HashMap<String, Integer> transfer) {
        transfer.forEach((key, value) -> {
            if (spaceForResources(transfer)) {
                addGoodsToCurrentCargo(key, value);
            } else {
                System.out.println("Not enough space for the resources!");
            }
        });
    }

    private HashMap<String, Integer> giveGoodsToFactory(HashMap<String, Integer> transfer) {
        transfer.forEach((key, value) -> {
            deleteGoodFromCurrentCargo(key, value);
        });
        return transfer;
    }
}