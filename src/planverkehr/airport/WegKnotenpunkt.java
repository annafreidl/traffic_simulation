package planverkehr.airport;

import java.util.ArrayList;


public class WegKnotenpunkt {
    final int betretenUm;
    final Knotenpunkt knotenpunkt;
    final Knotenpunkt vorgaenger;

    public WegKnotenpunkt(int betretenUm, Knotenpunkt knotenpunkt, Knotenpunkt vorgaenger){
        this.betretenUm = betretenUm;
        this.knotenpunkt = knotenpunkt;
        this.vorgaenger = vorgaenger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WegKnotenpunkt)) return false;
        WegKnotenpunkt that = (WegKnotenpunkt) o;
        return knotenpunkt.equals(that.knotenpunkt) && betretenUm == that.betretenUm;
    }

    public ArrayList<Knotenpunkt> getNeighbours(){
        return knotenpunkt.toKnotenpunkt;
    }

}
