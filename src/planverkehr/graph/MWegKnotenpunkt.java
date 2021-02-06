package planverkehr.graph;

import java.util.ArrayList;

public class MWegKnotenpunkt {
    final int betretenUm;
    final MKnotenpunkt knotenpunkt;
    final MKnotenpunkt vorgaenger;

    public MWegKnotenpunkt(int betretenUm, MKnotenpunkt knotenpunkt, MKnotenpunkt vorgaenger){
        this.betretenUm = betretenUm;
        this.knotenpunkt = knotenpunkt;
        this.vorgaenger = vorgaenger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MWegKnotenpunkt)) return false;
        MWegKnotenpunkt that = (MWegKnotenpunkt) o;
        return knotenpunkt.equals(that.knotenpunkt) && betretenUm == that.betretenUm;
    }

    public ArrayList<MKnotenpunkt> getNeighbours(){
        return knotenpunkt.connectedKnotenpunkteArray;
    }

    public MKnotenpunkt getKnotenpunkt() {
        return knotenpunkt;
    }
}
