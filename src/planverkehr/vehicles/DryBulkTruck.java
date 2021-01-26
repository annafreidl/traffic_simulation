package planverkehr.vehicles;

import planverkehr.EVehicleTypes;
import planverkehr.MCoordinate;

import java.util.Map;

public class DryBulkTruck implements IVehilce {

    String name = "dry bulk truck";
    EVehicleTypes kind = EVehicleTypes.road_vehicle;
    String graphic = "bulk_truck";
    double speed = 1.2;
    MCoordinate currentPosition;
    int id;

    public DryBulkTruck(int id, MCoordinate currentPosition) {
        this.id = id;
        this.currentPosition = currentPosition;
    }

    @Override
    public void moveVehicle(MCoordinate goal) {


    }
}
