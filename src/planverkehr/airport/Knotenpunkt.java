package planverkehr.airport;

import java.util.ArrayList;
import java.util.TreeSet;



public class Knotenpunkt {
    final double x;
    final double y;
    final String name;
    final String kind;
    final String[] to;
    final ArrayList<Knotenpunkt> toKnotenpunkt;
    final ArrayList<Knotenpunkt> conflictKnotenpunkt;
    final String[] conflict;
    final String targetType;
    final TreeSet<Integer> blockedForTickList;
    final int waitTime;
    final boolean hasConflict;
    boolean isBlocked;
    boolean isTempTarget = false;

    public Knotenpunkt(double x, double y, String name, String kind, String[] to, String[] conflict, String targetType, int waitTime) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.kind = kind;
        this.to = to;
        this.conflict = conflict;
        hasConflict = conflict.length > 0;
        toKnotenpunkt = new ArrayList<>();
        conflictKnotenpunkt = new ArrayList<>();
        blockedForTickList = new TreeSet<>();
        this.targetType = targetType;
        this.waitTime = waitTime;
    }

    public void addEntryToBlockedForTickList(Integer tick) {
        blockedForTickList.add(tick);

    }

    public String getName() {
        return name;
    }

    public void addToKnotenpunkt(Knotenpunkt k) {
        toKnotenpunkt.add(k);
    }

    public String[] getConflict() {
        return conflict;
    }

    public String[] getTo() {
        return to;
    }

    public void addConflictNode(Knotenpunkt conflictNode) {
        conflictKnotenpunkt.add(conflictNode);
    }

    public boolean isFreeFor(int timeBetretenUm) {
        Integer lowerTime = blockedForTickList.higher(timeBetretenUm);
        Integer upperTime = blockedForTickList.higher(timeBetretenUm + this.waitTime + 1);

        if (isBlocked) {
            return false;
        } else if (!hasConflict && (blockedForTickList.isEmpty() || (lowerTime == null)) ) {
            return true;
        } else if ( !hasConflict && (upperTime == null)) {
            return false;
        } else if (!hasConflict && blockedForTickList.subSet(lowerTime, upperTime).isEmpty()) {
            return true;
        } else if (hasConflict) {

            for (Knotenpunkt c : conflictKnotenpunkt) {
                Integer lowerTimeTemp = c.blockedForTickList.higher(timeBetretenUm);
                Integer upperTimeTemp = c.blockedForTickList.higher(timeBetretenUm + c.waitTime + 2);
                if (c.isBlocked) {
                    return false;
                } else if (c.blockedForTickList.isEmpty() || (lowerTimeTemp == null)) {
                    return true;
                } else if ( upperTimeTemp == null) {
                    return false;
                } else if (c.blockedForTickList.subSet(lowerTimeTemp, upperTimeTemp).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setBlocked() {
        isBlocked = true;
    }

    public void setFree() {
        isBlocked = false;
    }

}
