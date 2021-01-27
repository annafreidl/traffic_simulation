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
        return (this.getX() % 1 == 0 || this.getY() % 1 == 0);
    }

    public boolean isSecondTile() {
        boolean isSecondTile = false;
        if (this.getX() > 1) {
            isSecondTile = true;
        } else if (this.getY() > 1) {
            isSecondTile = true;
        }
        return isSecondTile;
    }

    public MCoordinate toIsoWithoutOffset() {
        double xIso = (this.x + this.y) * Config.tWidth / 2;
        double yIso = (this.x - this.y) * Config.tWidth / 4;

        return new MCoordinate(xIso, yIso);
    }

    public MCoordinate toGrid(){
        x -= Config.XOffset;
        y -= Config.YOffset;

        double xGrid = ((x / Config.tWidthHalft) + (y / Config.tHeightHalft)) / 2;
        double yGrid = (((y / Config.tHeightHalft) - (x / Config.tWidthHalft)) / 2 + 1);

        return new MCoordinate(xGrid, yGrid);
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

    public String toStringCoordinates(){
        return x + "-" + y;
    }

    public boolean istGleich(MCoordinate b){
        boolean p;
            if(x == b.x && y == b.y) {p = true; }
            else p = false;
            return p;
    }

    @Override
    public String toString() {
        return "MCoordinates{" +
            "x=" + x +
            ", y=" + y +
            '}';
    }


    public EDirections getRoadDirection() {

        EDirections directions;

        if (this.isEdge()) {
            MCoordinate coord = new MCoordinate(0, 0);
            if (x > 1) {
                coord.setX(x - 1);
            } else {
                coord.setX(x);
            }
            if (y > 1) {
                coord.setY(y - 1);
            } else {
                coord.setY(y);
            }

            if (coord.getX() == 0 && coord.getY() == 0.5) {
                directions = EDirections.nw;
            } else if (coord.getX() == 1 && coord.getY() == 0.5) {
                directions = EDirections.se;
            } else if (coord.getX() == 0.5 && coord.getY() == 0) {
                directions = EDirections.sw;
            } else if (coord.getX() == 0.5 && coord.getY() == 1) {
                directions = EDirections.ne;
            } else {
                directions = EDirections.empty;
            }
        } else {

            directions = EDirections.empty;
        }
        return directions;
    }
}
