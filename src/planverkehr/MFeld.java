package planverkehr;

import java.util.ArrayList;

public class MFeld {
    boolean isSelected = false;
    int x, y, xNew, yNew;
    double xIso, yIso;
    EFieldState state;
    ArrayList felder = new ArrayList();
    ArrayList punkt = new ArrayList();

    public MFeld(int x, int y, double xIso, double yIso){
        this.x = x;
        this.y = y;
        this.xIso = xIso;
        this.yIso = yIso;
        state = EFieldState.free;

        xNew = x;
        yNew = y;

        punkt.add(x);
        punkt.add(y);
        felder.add(punkt);
    }

    public void changeIsSelected(boolean isSelected){
        this.isSelected = isSelected;
    }

    public boolean getIsSelected(){
        return isSelected;
    }

    public double getXIso() {
        return xIso;
    }

    public double getYIso() {
        return yIso;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public EFieldState getState() {
        return state;
    }

    public void setState(EFieldState state) {
        this.state = state;
    }
}
