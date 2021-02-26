package planverkehr.airport;

import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import planverkehr.JSONParser;
import planverkehr.VGame;
import planverkehr.graph.MKnotenpunkt;


public class VPlane {
    Rectangle rectangle;
    final MAirplane plane;
    final double faktor;
    JSONParser parser = new JSONParser();


    public VPlane(VGame GameView, MAirplane plane) {
        this.plane = plane;
        this.faktor = Config.scaleFactor;
        initView(GameView);

    }

    public void initView(VGame GameView) {
        Rectangle rectangle = new Rectangle();

        rectangle.setWidth(10);
        rectangle.setHeight(10);

        rectangle.setX((plane.currentNode.x * faktor) - 0.5 * rectangle.getWidth());
        rectangle.setY(-(plane.currentNode.y * faktor) - 0.5 * rectangle.getWidth());


    }

    public void setAirplaneImage() {
        Image airplaneIMG = new Image("aeroplane.png");
    }

    public void setBalloonImage() {
        Image balloonIMG = new Image("hot-air-balloon.png");
    }

    public void fillScenarioWise() {
        if (planverkehr.Config.gameMode.equals("planverkehr")) {
            Image airplaneIMG = new Image("aeroplane.png");
            rectangle.setFill(new ImagePattern(airplaneIMG));
        } else {
            Image balloonIMG = new Image("hot-air-balloon.png");
            rectangle.setFill(new ImagePattern(balloonIMG));
        }

    }
    //todo: morgen fragen wie man an entsprechende Parameter rankommt.
    public void movePlane(Knotenpunkt end) {
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.seconds(1));
        double newXValue = -(rectangle.getX() - ((end.x * faktor) - 0.5 * rectangle.getWidth()));;
        double newYValue = -(rectangle.getY() - ((end.y * -faktor) - 0.5 * rectangle.getWidth()));;

        translateTransition.setToX(newXValue);
        translateTransition.setToY(newYValue);
        translateTransition.setNode(rectangle);
        translateTransition.play();

        rotateView(end);
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


}
