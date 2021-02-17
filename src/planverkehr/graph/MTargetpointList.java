package planverkehr.graph;

import planverkehr.MVehicles;
import planverkehr.transportation.ESpecial;

import java.util.ArrayList;


public class MTargetpointList extends ArrayList<MKnotenpunkt> {

    @Override
    public MTargetpointList clone() {
        //   MTargetpointList clone = (MTargetpointList) super.clone();
        MTargetpointList clone = new MTargetpointList();
        this.forEach(clone::add);
        return clone;
    }

    public void filterByBlocked(ArrayList<String> groupId) {
        //nur prüfen, wenn Knotenpunkt zu einem Zeitpunkt blockiert ist
        this.removeIf(mKnotenpunkt -> {
            ArrayList<String> clonedGroupIds = (ArrayList<String>) mKnotenpunkt.getListOfGroupId().clone();
            clonedGroupIds.retainAll(groupId);

              return  mKnotenpunkt.isBlocked || !clonedGroupIds.isEmpty();
            }
        );


    }

    public void filterByName(ESpecial MTargetType) {
        this.removeIf(knotenpunkt ->
            !knotenpunkt.targetType.equals(MTargetType)
        );
    }

    public boolean isWaitList() {
        return this.get(0).name.equals("wait");
    }

    public void getReadyForSearch(MVehicles vehicles, int tickNumber) {
        //        filterByName(vehicles.getNextWayPoint());
        filterByBlocked(vehicles.getCurrentKnotenpunkt().getListOfGroupId());

        //Wenn Zielknoten nicht erreichbar ist, dann wenn möglich auf eigenen Knoten stehen bleiben,
        if (this.isEmpty()) {
            vehicles.setWaiting(true);
            vehicles.getCurrentKnotenpunkt().addEntryToBlockedForTickList(tickNumber, true);
        }
    }

}
