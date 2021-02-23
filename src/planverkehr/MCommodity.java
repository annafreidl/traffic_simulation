package planverkehr;

import planverkehr.airport.TargetpointList;
import planverkehr.graph.MKnotenpunkt;
import planverkehr.graph.MTargetpointList;
import planverkehr.graph.Path;
import planverkehr.graph.SearchObject;

public class MCommodity {
    String name;
    ECommodityTypes commodityType;
    Integer id;
    Path haltestellenStops;
    MKnotenpunkt currentTarget;

    public MCommodity(String name, ECommodityTypes commodityType, int id, MKnotenpunkt currentTarget) {
        this.name = name;
        this.commodityType = commodityType;
        this.id = id;
        this.currentTarget = currentTarget;

    }

    public boolean searchPath(MTargetpointList otherFactories) {
        SearchObject so = new SearchObject(currentTarget, otherFactories, 0);

        Path p = new Path(so, false);

        if (p.isEmpty()) {
            System.out.println("no connection found");
            return false;
        } else {
            p.remove(0);
            haltestellenStops = p;
            return true;
        }
    }

    @Override
    public String toString() {
        return "MCommodity{" +
            "name='" + name + '\'' +
            ", commodityType=" + commodityType +
            ", id=" + id +
            ", nextStop=" + (haltestellenStops.size() > 0 ? haltestellenStops.peek().getKnotenpunkt().getVisibleCoordinate().toString() : "endstation")+
            ", currentTarget=" + currentTarget.getVisibleCoordinate().toString() +
            '}';
    }

    public String getName() {
        return name;
    }

    public void setPath(Path mWegKnotenpunkts) {
        haltestellenStops = mWegKnotenpunkts;
    }
}
