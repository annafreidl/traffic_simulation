package planverkehr.transportation;

public class MRailBlock {
    boolean isBlocked = false;
    int id;

    public MRailBlock(int id){
        this.id = id;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public boolean isBlocked() {
        return isBlocked;
    }
}
