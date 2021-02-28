package planverkehr.graph;

import java.util.HashMap;


public class Graph extends HashMap<String, MKnotenpunkt> {
    int id = 0;
    int blockId = 0;

    public int getIncreasedId(){
       return id++;
    }

    public int getBlockId() {
        return blockId;
    }

    public void increaseBlockId(){
        blockId++;
    }
}
