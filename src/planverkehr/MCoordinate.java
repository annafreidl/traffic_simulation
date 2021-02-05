package planverkehr;

import planverkehr.transportation.EDirections;

public class MCoordinate {
    double x, y, z;

    public MCoordinate(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public MCoordinate toCanvasCoord() {
        double xVisible = (this.x - this.y) * Config.tWidth / 2;
        double yVisible = (this.x + this.y) * Config.tWidth / 4;

        xVisible += Config.XOffset;
        yVisible += Config.YOffset;


        yVisible = yVisible - (z * Config.increase);

        return new MCoordinate(xVisible, yVisible, z);
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

    public MCoordinate toCanvasCoordWithoutOffset() {
        double xVisible = (this.x - this.y) * Config.tWidth / 2;
        double yVisible = (this.x + this.y) * Config.tWidth / 4;

        xVisible += Config.XOffset;
        yVisible += Config.YOffset;

        yVisible = yVisible - (z * Config.increase);

        return new MCoordinate(xVisible, yVisible, z);
    }

    public MCoordinate toVisibleCoord(){
       double xNew = x - Config.XOffset;
       double yNew = y - Config.YOffset;

        double xVisible = ((xNew / Config.tWidthHalft) + (yNew / Config.tHeightHalft)) / 2;
        double yVisible = (((yNew / Config.tHeightHalft) - (xNew / Config.tWidthHalft)) / 2) - Config.worldWidth + 1;

        return new MCoordinate((int) xVisible, (int) yVisible, z);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
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
            MCoordinate coord = new MCoordinate(0, 0, 0);
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
                directions = EDirections.NW;
            } else if (coord.getX() == 1 && coord.getY() == 0.5) {
                directions = EDirections.SE;
            } else if (coord.getX() == 0.5 && coord.getY() == 0) {
                directions = EDirections.SW;
            } else if (coord.getX() == 0.5 && coord.getY() == 1) {
                directions = EDirections.NE;
            } else {
                directions = EDirections.EMPTY;
            }
        } else {

            directions = EDirections.EMPTY;
        }
        return directions;
    }
}
