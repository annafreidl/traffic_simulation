package planverkehr.graph;


import java.util.ArrayList;


public class MTargetpointList extends ArrayList<MKnotenpunkt> {

    @Override
    public MTargetpointList clone() {
        //   MTargetpointList clone = (MTargetpointList) super.clone();
        MTargetpointList clone = new MTargetpointList();
        clone.addAll(this);
        return clone;
    }

}
