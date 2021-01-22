package planverkehr;

import planverkehr.transportation.EDirections;

public class MCoordinate {
    double x, y;

    public MCoordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public MCoordinate toIso() {
        double xIso = (this.x - this.y) * Config.tWidth / 2;
        double yIso = (this.x + this.y) * Config.tWidth / 4;

        xIso += Config.XOffset;
        yIso += Config.YOffset;

        return new MCoordinate(xIso, yIso);
    }

    public boolean isEdge() {
        System.out.println("nachkommastellen: " + this.getX() % 1);
        return (this.getX() % 1 == 0 || this.getY() % 1 == 0);
    }

    public boolean isSecondTile(){
        boolean isSecondTile = false;
        if(this.getX() > 1){
            this.setX(this.getX() - 1);
            isSecondTile = true;
        } else if (this.getY() > 1){
            this.setY(this.getY() - 1);
            isSecondTile = true;
        }
        return isSecondTile;
    }

    public MCoordinate toIsoWithoutOffset() {
        double xIso = (this.x + this.y) * Config.tWidth / 2;
        double yIso = (this.x - this.y) * Config.tWidth / 4;

        return new MCoordinate(xIso, yIso);
    }

    public MCoordinate toGird() {
        x -= Config.XOffset;
        y -= Config.YOffset;

        double xGrid = ((x / Config.tWidthHalft) + (y / Config.tHeightHalft)) / 2;
        double yGrid = (((y / Config.tHeightHalft) - (x / Config.tWidthHalft)) / 2 + 1);

        return new MCoordinate(xGrid, yGrid);
    }

    //Berechnet die Koordinaten auf Basis der Building Daten. Muss auf Basis des Centers durchgeführt werden
    // todo: Berechnung noch fehlerhaft
    public MCoordinate getAbsoluteCoordinates(MCoordinate relativeCoordinates) {

        System.out.println((relativeCoordinates.getY() - 0.5));
        System.out.println((relativeCoordinates.getY() - 0.5) * Config.tWidthHalft);

        System.out.println((relativeCoordinates.getX() - 0.5));
        System.out.println((relativeCoordinates.getX() - 0.5) * Config.tHeightHalft);

        double xAbs = this.x + ((relativeCoordinates.getY() - 0.5) * Config.tWidthHalft);
        double yAbs = this.y + ((relativeCoordinates.getX() - 0.5) * Config.tHeightHalft);

        return new MCoordinate(xAbs, yAbs);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "MCoordinates{" +
            "x=" + x +
            ", y=" + y +
            '}';
    }


    public EDirections getRoadDirection(){
        EDirections directions;
        if(this.getX() == 0 && this.getY() == 0.5){
            directions = EDirections.nw;
        } else if (this.getX() == 1 && this.getY() == 0.5){
            directions = EDirections.se;
        } else if (this.getX() == 0.5 && this.getY() == 0){
            directions = EDirections.sw;
        } else {
            directions = EDirections.ne;
        }
        return directions;
    }
}
