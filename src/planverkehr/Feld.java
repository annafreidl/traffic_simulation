package planverkehr;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Feld {

    int xnew;
    int ynew;
    ArrayList felder = new ArrayList();
    ArrayList punkt = new ArrayList();

    public Feld(int x, int y) {

        xnew = x;
        ynew = y;

        punkt.add(x);
        punkt.add(y);
        felder.add(punkt);



    }
}
