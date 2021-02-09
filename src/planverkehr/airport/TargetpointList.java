package planverkehr.airport;

import java.util.ArrayList;

public class TargetpointList extends ArrayList<Knotenpunkt> {

    @Override
    public TargetpointList clone() {
        TargetpointList clone = new TargetpointList();
        this.forEach(clone::add);
        return clone;
    }

    public void filterByBlocked() {
        //nur prüfen, wenn Knotenpunkt zu einem Zeitpunkt blockiert ist
        this.removeIf(knotenpunkt ->
            knotenpunkt.isBlocked
        );

    }

    public void filterByName(String targetType) {
        this.removeIf(knotenpunkt ->
            !knotenpunkt.targetType.equals(targetType)
        );
    }

    public boolean isWaitList() {
        return this.get(0).name.equals("wait");
    }

    public void getReadyForSearch(TargetpointList waitPointList, MAirplane plane) {
        filterByName(plane.getNextWayPoint());
        filterByBlocked();

        //Wenn Zielknoten nicht erreichbar ist, dann den waitKnoten als nächstes Ziel prüfen
        if (this.isEmpty()) {
            this.addAll(waitPointList.clone());
            filterByBlocked();
        }

        //Wenn waitKnoten nicht erreichbar, dann wenn möglich auf eigenen Knoten stehen bleiben,
        // wenn erreichbar, dann zur Waypointlist hinzufügen
        if (this.isEmpty()) {
            if (plane.currentNode.kind.equals("concrete")) {
                this.add(plane.currentNode);
                plane.addElementToWaypointList(0, plane.currentNode.targetType);
            } else {
                System.out.println("++++++++Deadlock in getWaypoints++++++++");
            }
        } else if (isWaitList()) {
            plane.addElementToWaypointList(0, "wait");
        }


    }

}
