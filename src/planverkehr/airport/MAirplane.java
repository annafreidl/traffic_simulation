package planverkehr.airport;

import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


//Model des Flugzeugs
public class MAirplane implements Comparable<MAirplane> {
    final int initTime;
    Knotenpunkt currentNode;
    final int id;
    final ArrayList<String> wayPointList;
    Path pathStack;
    boolean isVisible;
    final double faktor;





    public MAirplane(String[] waypoints, int initTime, int id) {
        wayPointList = new ArrayList<>();
        Collections.addAll(wayPointList, waypoints);
        isVisible = false;
        pathStack = new Path();
        this.id = id;
        this.initTime = initTime;
        this.faktor=Config.scaleFactor;




    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MAirplane plane = (MAirplane) o;
        return Double.compare(plane.initTime, initTime) == 0 &&
            Objects.equals(id, plane.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(initTime, id);
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Override
    public int compareTo(MAirplane plane) {
        return Integer.compare(this.getInitTime(), plane.getInitTime());
    }

    public void setPathStack(Path pathStack, Integer tick) {
        if(pathStack.isEmpty()){
            WegKnotenpunkt wp = new WegKnotenpunkt(tick, currentNode, currentNode);
            currentNode.addEntryToBlockedForTickList(tick);
            currentNode.addEntryToBlockedForTickList(tick + 1);
            pathStack.push(wp);
        } else {
            this.pathStack = pathStack;
        }
    }

    public void addElementToWaypointList(int i, String s){
        wayPointList.add(i, s);
    }

    public int getInitTime() {
        return initTime;
    }

    public WegKnotenpunkt removeLastNodeFromPathList() {
        return pathStack.pop();
    }

    public void setCurrentNode(Knotenpunkt currentNode) {
        this.currentNode = currentNode;
    }

    public String getNextWayPoint() {
        return wayPointList.get(0);
    }

    public void removeWayPoint() {
        wayPointList.remove(0);
    }



}
