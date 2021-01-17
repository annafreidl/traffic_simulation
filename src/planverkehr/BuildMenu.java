package planverkehr;

import javafx.scene.control.Menu;

public class BuildMenu extends Menu {
    EBuildType buildType;

    public BuildMenu(EBuildType buildType, String name){
        this.buildType = buildType;
        this.setText(name);
    }

    public EBuildType getBuildType() {
        return buildType;
    }
}
