package planverkehr.verkehrslinien;

import planverkehr.MVehicles;

import java.util.Objects;

public class linienConfigObject {
    String name;
    MVehicles vehicles;
    boolean save, circle;

    public linienConfigObject(String name, MVehicles vehicles, boolean save, boolean circle){
        this.name = Objects.requireNonNullElse(name, "DefaultName");
        this.vehicles = vehicles;
        this.save = save;
        this.circle = circle;
    }

    public boolean shouldSave() {
        return save;
    }

    public String getName() {
        return name;
    }

    public MVehicles getVehicle() {
        return vehicles;
    }

    public boolean isCircle() {
        return circle;
    }
}
