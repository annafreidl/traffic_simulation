package planverkehr;
//Klassen Name sehr schlecht gewählt, wird noch geändert
public class MMap {
    private String mapGen;
    private String gameMode;
    private int width;
    private int depth;


    public MMap(String mapGen, String gameMode, int width, int depth){
        this.mapGen = mapGen;
        this.gameMode = gameMode;
        this.width = width;
        this.depth = depth;
    }

    public String getMapGen() {
        return mapGen;
    }

    public String getGameMode() {
        return gameMode;
    }

    public int getWidth() {
        return width;
    }

    public int getDepth() {
        return depth;
    }
}
