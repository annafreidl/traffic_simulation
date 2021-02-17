package planverkehr;

import planverkehr.graph.*;
import planverkehr.transportation.ESpecial;

import java.util.ArrayList;
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
    final ArrayList<ESpecial> wayPointList;
    Path pathStack;
    boolean isWaiting = false;
    private boolean isAtGoal = true;
    EVehicleTypes kindEnum;
    boolean drivesLeft;

    public MVehicles(String name, String kind, String graphic, HashMap<String, Integer> cargo, double speed) {
        this.name = name;
        this.kind = kind;
        this.graphic = graphic;
        this.cargo = cargo;
        this.speed = speed;
        wayPointList = new ArrayList<>();
        pathStack = new Path();

        switch (kind) {
            case "road vehicle" -> kindEnum = EVehicleTypes.road_vehicle;
            case "engine" -> kindEnum = EVehicleTypes.engine;
            case "wagon" -> kindEnum = EVehicleTypes.wagon;
            case "plane" -> kindEnum = EVehicleTypes.plane;
        }
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
        currentPosition = roadKnotenpunkt.getVisibleCoordinate();
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

    public MKnotenpunkt getCurrentKnotenpunkt() {
        return currentKnotenpunkt;
    }

    public ESpecial getNextWayPoint() {
        ESpecial nextWaypoint;
        //    if(currentKnotenpunkt.getTargetType().equals(ESpecial.FACTORY)){
        nextWaypoint = switch (this.getKind()) {
            case "road vehicle" -> ESpecial.BUSSTOP;
            case "engine" -> ESpecial.RAILSTATION;
            default -> ESpecial.BUSSTOP;
        };
        //     } else {
        nextWaypoint = ESpecial.FACTORY;
        //     }
        return nextWaypoint;

    }

    public void addElementToWaypointList(int i, ESpecial targetType) {
        wayPointList.add(i, targetType);
    }

    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }

    public void setAtGoal(boolean atGoal) {
        isAtGoal = atGoal;
    }

    public EVehicleTypes getKindEnum() {
        return kindEnum;
    }

    public boolean isAtGoal() {
        return isAtGoal;
    }

    public void setDrivesLeft(boolean drivesLeft) {
        this.drivesLeft = drivesLeft;
    }

    public boolean isDrivesLeft() {
        return drivesLeft;
    }
}
