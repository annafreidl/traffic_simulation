package planverkehr.airport;


import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;

import java.util.HashMap;

/*
public class VAirport extends ScrollPane{
    final HashMap<Integer, VAirplane> airplaneViewList;
    final Stage window;
    // final Button newTick;
    final Group group;
    final Pane pane;
    final Scene scene;
    final int faktor = Config.scaleFactor;
    public double mouseX, mouseY;

    public VAirport(MAirport mAirport, Stage window) {
        this.window = window;
        group = new Group();
        airplaneViewList = new HashMap<>();
        pane = new Pane(group);

        // Hintergrundfarbe der Pane
        pane.setStyle("-fx-background-color: lightblue");

        // Größe der Pane
        pane.setPrefWidth(Config.windowSize);
        pane.setPrefHeight(Config.windowSize);

        // Verbindungen zwischen den Knotenpunkten werden gezeichnet
        for (Knotenpunkt k1 : mAirport.getNodeList()) {
            for (Integer s : mAirport.getNachbarpunkte(k1)) {
                Knotenpunkt k2 = mAirport.nodeList.get(s);
                Line k = new Line(k1.x * faktor, k1.y * -faktor, k2.x * faktor, k2.y * -faktor);
                k.setStrokeWidth(8);
                k.setStrokeLineCap(StrokeLineCap.ROUND);
                k.setVisible(false);
                Line l = new Line(k1.x * faktor, k1.y * -faktor, k2.x * faktor, k2.y * -faktor);
                l.setStrokeWidth(1);

                // wenn beide Punkte dieselbe kind haben...
                if (k1.kind.equals(k2.kind) ||
                    // ... oder die Punkte vom kind concrete/runway sind und nicht an kind air grenzen,
                    // setze Farbe der Linie dazwischen wie folgt
                    ((k2.kind.equals("concrete") || k2.kind.equals("runway") || k2.kind.equals("hangar")) && !(k1.kind.equals("air"))))
                {
                    switch (k1.kind) {
                        case "air" -> // blaue gestrichelte Linie
                            l.setStroke(Color.BLUE);
                        case "concrete", "hangar" -> {
                            // graue dicke Linie mit runden Enden
                            k.setStroke(Color.GREY);
                            k.setVisible(true);
                            // weisse gestrichelte Linie
                            l.setStroke(Color.WHITE);
                            l.setStyle("-fx-stroke-dash-array:2.0, 21.0;");
                        }
                        case "runway" -> {
                            // graue dicke Linie mit runden Enden
                            k.setStroke(Color.GREY);
                            k.setVisible(true);
                            // gelbe gestrichelte Linie
                            l.setStroke(Color.YELLOW);
                            l.setStyle("-fx-stroke-dash-array:2.0, 21.0;");
                        }
                    }
                }
                // ansonsten ist die Linienfarbe schwarz
                else {
                    l.setStroke(Color.BLACK);
                }
                group.getChildren().addAll(k,l);
            }
        }

        // Bilder und Kreise werden je nach targettype/kind erzeugt
        for (Knotenpunkt k : mAirport.getNodeList()) {

            Rectangle rectangle = new Rectangle();
            rectangle.setWidth(20);
            rectangle.setHeight(10);
            rectangle.setX((k.x * faktor) - 0.5 * rectangle.getWidth());
            rectangle.setY((k.y * -faktor) - 0.5 * rectangle.getHeight());
            rectangle.setVisible(false);

            // Fülle das erstellte Rechteck je nach targettype mit img und setzte visible auf true
            switch (k.targetType){
                case "gateway" -> {
                    Image img = new Image("gate.png");
                    rectangle.setFill(new ImagePattern(img));
                    rectangle.setVisible(true);
                }
                case "hangar" -> {
                    Image img = new Image("Hangar.png");
                    rectangle.setFill(new ImagePattern(img));
                    rectangle.setVisible(true);
                }
                case "tanken" -> {
                    Image img = new Image("Tanken.png");
                    rectangle.setFill(new ImagePattern(img));
                    rectangle.setVisible(true);
                }
            }
            group.getChildren().add(rectangle);

            // wenn kind="air" erzeuge blaue Kreise
            if(k.kind.equals("air")){
                Circle c = new Circle(k.x * faktor, k.y * -faktor, 3);
                c.setFill(Color.BLUE);
                group.getChildren().add(c);
            }
        }

        group.setTranslateX(pane.getPrefWidth() / 2);
        group.setTranslateY(pane.getPrefHeight() / 2);

        scene = new Scene(pane, Config.windowSize, Config.windowSize);

        // Zoomen durch Scrollen
        scene.setOnScroll(event -> {

            if (event.getDeltaY() == 0)
                return;

            // Bestehende Skalierung lesen
            double scaleX = group.getScaleX();
            double scaleY = group.getScaleY();

            // Bestehende Skalierung kopieren
            double oldScaleX = scaleX;
            double oldScaleY = scaleY;

            // Neue Skalierung berechnen
            // getDelta ist abhängig von den Scrolleinstellungen vom Betriebssystem
            scaleX *= Math.pow(1.01, event.getDeltaY());
            scaleY *= Math.pow(1.01, event.getDeltaY());

            double fx = (scaleX / oldScaleX) - 1;
            double fy = (scaleY / oldScaleY) - 1;

            // getBoundsInParent() gibt die Grenzen zurück, nachdem sie mit Translate angepasst wurden
            double dx = (event.getSceneX() - (group.getBoundsInParent().getWidth() / 2 + group.getBoundsInParent().getMinX()));
            double dy = (event.getSceneY() - (group.getBoundsInParent().getHeight() / 2 + group.getBoundsInParent().getMinY()));

            // Neue Skalierung setzen
            group.setScaleX(scaleX);
            group.setScaleY(scaleY);
            group.setTranslateX(group.getTranslateX() - fx * dx);
            group.setTranslateY(group.getTranslateY() - fy * dy);

            event.consume();
        });

        // Events für drag&drop
        // Position speichern an der die Maustaste gedrückt wurde
        scene.setOnMousePressed(event -> {
            event.consume();
            mouseX = event.getX();
            mouseY = event.getY();
            VAirport.this.toFront();
        });

        // wenn Maustaste losgelassen wird, group verschieben
        scene.setOnMouseDragged(event -> {
            event.consume();
            double mouseNewX = event.getX();
            double mouseNewY = event.getY();
            double deltaX = mouseNewX - mouseX;
            double deltaY = mouseNewY - mouseY;
            group.setTranslateX(group.getTranslateX() + deltaX);
            group.setTranslateY(group.getTranslateY() + deltaY);

            mouseX = mouseNewX;
            mouseY = mouseNewY;
        });

        scene.getStylesheets().add("/airport/layout.css");

        window.setTitle("Flughafen Simulator");
        window.setScene(scene);
        window.show();
    }

    // bewegt das Flugzeug von aktuellem Punkt nach "end" in 0.5 Sekunden
    public void movePlane(Knotenpunkt node, MAirplane plane) {
        VAirplane planeView = airplaneViewList.get(plane.id);
        if(planeView != null) {
            planeView.movePlane(node);
        }
    }

    // entfernt Flugzeug
    public void removePlane(MAirplane plane) {
        VAirplane planeView = airplaneViewList.get(plane.id);
        planeView.remove(group);

        airplaneViewList.remove(plane.id);
        group.getChildren().remove(plane);

        window.setScene(scene);
    }

    public void showPlane(MAirplane plane) {
        VAirplane planeView = new VAirplane(group, plane);
        airplaneViewList.put(plane.id, planeView);
        window.setScene(scene);

    }
}
*/