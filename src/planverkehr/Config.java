package planverkehr;

import javafx.util.Duration;

public class Config {
    public static final int scaleFactor = 20;
    public static final int windowSize = 700;
    public static final int worldWidth = 10;
    public static final int worldHeight = 10;
    public static final int tWidth = 80;
    public static final double Xoffset = (worldWidth*(tWidth+1))/2; //darf nicht in die Config
    public static final double Yoffset = tWidth/4; //darf nicht in die Config
    public static final String jsonFile = "planverkehr.json";
    public static final Duration tickFrequency = Duration.seconds(1);
}
