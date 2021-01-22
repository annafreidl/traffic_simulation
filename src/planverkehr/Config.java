package planverkehr;

import javafx.util.Duration;

public class Config {
    public static final int scaleFactor = 20;
    public static final int windowSize = 700;
    public static final int worldWidth = 10;
    public static final int worldHeight = 10;
    public static final int tWidth = 80;
    public static final int tWidthHalft = tWidth / 2;
    public static final int tHeight = tWidth / 2;
    public static final int tHeightHalft = tWidth / 4;
    public static final double XOffset = (double)(worldWidth*(tWidth+1))/2;
    public static final double YOffset = (double)tWidth/4;
    public static final String jsonFile = "planverkehr.json";
    public static final Duration tickFrequency = Duration.seconds(1);
}
