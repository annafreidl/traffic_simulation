package planverkehr;

import java.util.HashMap;

public class MVehicles {

    private String name;
    private String kind;
    private String graphic;
    private HashMap cargo;
    private double speed;


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

}