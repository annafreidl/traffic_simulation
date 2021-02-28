package planverkehr;

import planverkehr.graph.*;
import planverkehr.transportation.ESpecial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MVehicles {

    private final String name;
    private final String kind;
    private final String graphic;
    private final HashMap<String, Integer> cargo;
    private HashMap<String, Integer> cargoCurrentString;
    private final HashMap<MCommodity, Integer> cargoCurrentCommodity;
    private final double speed, calculatedSpeed;
    int id;
    MCoordinate currentPosition;
    MKnotenpunkt currentKnotenpunkt;
    final ArrayList<ESpecial> wayPointList;
    Path pathStack;
    boolean isWaiting = false;
    private boolean isAtGoal = true;
    boolean isVisible = false;
    EVehicleTypes kindEnum;
    boolean drivesLeft;
    double lastMoved = 0;

    public MVehicles(String name, String kind, String graphic, HashMap<String, Integer> cargo, double speed) {
        this.name = name;
        this.kind = kind;
        this.graphic = graphic;
        this.cargo = cargo;
        this.speed = speed;
        this.calculatedSpeed = 1 / (speed * 3);
        wayPointList = new ArrayList<>();
        pathStack = new Path();

        cargoCurrentString = new HashMap<>();
        cargoCurrentCommodity = new HashMap<>();

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

    public HashMap<String, Integer> getCargo() {
        return cargo;
    }

    public double getSpeed() {
        return speed;
    }

    public void setId(int id) {
        this.id = id;
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
            "currentCargo=" + cargoCurrentString +
            '}';
    }


    private boolean spaceForResources(String resource, int quantity) {
        if (cargo.containsKey(resource)) {
            int currentQuantity;
            if (cargoCurrentString.size() > 0) {
                currentQuantity = cargoCurrentString.get(resource);
            } else {
                currentQuantity = 0;
            }
            return getStorageSpace(resource, quantity, currentQuantity, cargo);
        } else {
            System.out.println("Vehicle can not store this resource!");
            return false;
        }
    }

    static boolean getStorageSpace(String resource, int quantity, int currentQuantity, HashMap<String, Integer> cargo) {
        int originalSpace = cargo.get(resource);
        int availableSpace = originalSpace - currentQuantity;
        if (quantity <= availableSpace) {
            System.out.println("Enough space");
            return true;
        } else {
            System.out.println("Not enough space");
            return false;
        }
    }


    private void addGoodsToCurrentCargo(MCommodity resource, int quantity) {
        cargoCurrentString.put(resource.getName(), cargoCurrentString.containsKey(resource.getName()) ? cargoCurrentString.get(resource.getName()) + quantity : quantity);
        cargoCurrentCommodity.put(resource, cargoCurrentCommodity.containsKey(resource) ? cargoCurrentCommodity.get(resource) + quantity : quantity);
    }

    private void deleteGoodFromCurrentCargo(MCommodity resource, int quantity) {
        cargoCurrentString.put(resource.getName(), cargoCurrentString.containsKey(resource.getName()) ? cargoCurrentString.get(resource.getName()) - quantity : quantity);
        cargoCurrentCommodity.put(resource, cargoCurrentCommodity.containsKey(resource) ? cargoCurrentCommodity.get(resource) - quantity : quantity);

    }

    public boolean takeGoodsFromFactory(MCommodity commodity, int quantity) {
        if (spaceForResources(commodity.getName(), quantity)) {
            addGoodsToCurrentCargo(commodity, quantity);
            return true;
        } else {
            return false;
        }
    }


    public void removeGoodFromVerhicle(MCommodity resource, int quantity) {

        deleteGoodFromCurrentCargo(resource, quantity);


    }

    public MKnotenpunkt getCurrentKnotenpunkt() {
        return currentKnotenpunkt;
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

    public HashMap<MCommodity, Integer> getCargoCurrentCommodity() {
        return cargoCurrentCommodity;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public double getLastMoved() {
        return lastMoved;
    }

    public double getCalculatedSpeed() {
        return calculatedSpeed;
    }

    public void setLastMoved(double lastMoved) {
        this.lastMoved = lastMoved;
    }
}
