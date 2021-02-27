package planverkehr;

import javafx.util.Duration;

public class Config {
    static JSONParser parser = new JSONParser();
    public static final int scaleFactor = 20;
    public static final int windowSize = 700;
    public static final int worldWidth = parser.getMapFromJSON().getWidth()/5;
    public static final int worldHeight = parser.getMapFromJSON().getDepth()/5;
    public static final int tWidth = 40 * 2; //80
    public static final int tWidthHalft = tWidth / 2; //40
    public static final int tHeight = tWidth / 2; //20
    public static final int tHeightHalft = tWidth / 4; //10
    public static final int increase = tHeightHalft;
    //todo: XOffset dynamisch berechnen, bisher eine Schätzung
    public static final double XOffset = (double)(worldWidth*(tWidth+1))/2+(int)(Math.sqrt(worldHeight-worldWidth)+5)*tWidth;
        //(double)(worldWidth*(tWidth+1))/2;
    public static final double YOffset = (double)tWidth/4;
    public static final String jsonFile = "own-scenario.json";
    public static final String gameMode = parser.getMapFromJSON().getGameMode();
    public static final Duration tickFrequency = Duration.seconds(0.1);


}
