package planverkehr.airport;

import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
/*

public class VAirplane {
    Rectangle rectangle;
    final double faktor;
    final MAirplane plane;


    public VAirplane(Group group, MAirplane plane) {
        this.faktor = Config.scaleFactor;
        this.plane = plane;
        initView(group);
    }



    public void movePlane(Knotenpunkt end) {
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.seconds(0.5));
        double newXValue = -(rectangle.getX() - ((end.x * faktor) - 0.5 * rectangle.getWidth()));
        double newYValue = -(rectangle.getY() - ((end.y * -faktor) - 0.5 * rectangle.getWidth()));

        translateTransition.setToX(newXValue);
        translateTransition.setToY(newYValue);
        translateTransition.setNode(rectangle);
        translateTransition.play();

        rotateView(end);
    }

    public void setAirplaneImage() {

        Image img = new Image("airplane1.png");
        rectangle.setFill(new ImagePattern(img));

    }


    public static double calcRotationAngleInDegrees(Point2D centerPt, Point2D targetPt) {
        // berechnet den Winkel theta aus den targetPt und CenterPt Werten
        // ausgabe in radians-Werten
        // ausgabe in Uhrzeigersinn richtung
        double theta = Math.atan2((-targetPt.getY()) - (-centerPt.getY()), targetPt.getX() - centerPt.getX());


        // rotiert theta im uhrzeigersinn um 90 grad, so dass der Punkt 0 im Norden liegt
        theta += Math.PI / 2.0;


        // konvertiert von radians in Gradzahlen
        double angle = Math.toDegrees(theta) - 90;

        // konvertiert in einen positiven Zahlenbereich

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }



    public void rotateView(Knotenpunkt targetNode) {
        Point2D targetPoint = new Point2D(targetNode.x, targetNode.y);
        Point2D currentPoint = new Point2D(plane.currentNode.x, plane.currentNode.y);

        double angle = calcRotationAngleInDegrees(currentPoint, targetPoint);
        rectangle.setRotate(angle);
    }

    public void initView(Group group) {
        rectangle = new Rectangle();

        rectangle.setWidth(30);
        rectangle.setHeight(30);
        rectangle.setX((plane.currentNode.x * faktor) - 0.5 * rectangle.getWidth());
        rectangle.setY(-(plane.currentNode.y * faktor) - 0.5 * rectangle.getHeight());


        group.getChildren().add(rectangle);

        setAirplaneImage();


    }


    public void remove(Group group) {
        group.getChildren().remove(rectangle);
    }
}

*/