package planverkehr;
//Klassen Name sehr schlecht gewählt, wird noch geändert
public class MMap {

    private final String gameMode;
    private final int width;
    private final int depth;


    public MMap(String gameMode, int width, int depth){
        this.gameMode = gameMode;
        this.width = width;
        this.depth = depth;
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
