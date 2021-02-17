package planverkehr.verkehrslinien;

import javafx.scene.paint.Color;
import planverkehr.EBuildType;
import planverkehr.MVehicles;
import planverkehr.graph.*;
import planverkehr.transportation.ESpecial;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Random;

public class MLinie {
    String name;
    int ID, haltestellenID;
    Deque<MWegKnotenpunkt> listOfHaltestellenKnotenpunkten;
    Deque<MWegKnotenpunkt> listeAllerLinienKnotenpunkte;
    MVehicles vehicle;
    EBuildType type;
    Color color;
    Random rand = new Random();
    boolean circle;


    public MLinie(int linienID) {
        this.ID = linienID;
        listOfHaltestellenKnotenpunkten = new ArrayDeque<MWegKnotenpunkt>();
        listeAllerLinienKnotenpunkte = new ArrayDeque<MWegKnotenpunkt>();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        color = Color.color(r, g, b);
    }

    public void addWegknotenpunkt(MKnotenpunkt k) {
        boolean isFirstNode = listOfHaltestellenKnotenpunkten.isEmpty();
        MKnotenpunkt vorgaenger = isFirstNode ? k : listOfHaltestellenKnotenpunkten.getLast().getKnotenpunkt();
        if (isFirstNode) {
            type = k.getSurfaceType();
            listOfHaltestellenKnotenpunkten.add(new MWegKnotenpunkt(haltestellenID, k, vorgaenger));
            haltestellenID++;
        } else if (type.equals(k.getSurfaceType()) || k.getTargetType().equals(ESpecial.FACTORY)) {
            listOfHaltestellenKnotenpunkten.add(new MWegKnotenpunkt(haltestellenID, k, vorgaenger));
            haltestellenID++;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVehicle(MVehicles vehicle) {
        this.vehicle = vehicle;
    }

    public String getName() {
        return name;
    }

    public EBuildType getType() {
        return type;
    }

    public MVehicles getVehicle() {
        return vehicle;
    }

    public Deque<MWegKnotenpunkt> getListOfHaltestellenKnotenpunkten() {
        return listOfHaltestellenKnotenpunkten;
    }

    public Color getColor() {
        return color;
    }


    public boolean connect() {
        MWegKnotenpunkt last = listOfHaltestellenKnotenpunkten.peekLast();
        MWegKnotenpunkt first = listOfHaltestellenKnotenpunkten.peek();
        boolean searching = true;


        for (MWegKnotenpunkt k :
            listOfHaltestellenKnotenpunkten
        ) {
            if (!searching) {
                break;
            }
            else if (k.equals(last)) {
                //special treatment last
                searching = findPath(k.getVorgaenger(), k);

                if (isCircle()) {
                    searching = findPath(k.getKnotenpunkt(), first);
                } else {
                    ArrayList<MWegKnotenpunkt> temp = new ArrayList<>(listeAllerLinienKnotenpunkte);
                    listeAllerLinienKnotenpunkte.add(new MWegKnotenpunkt(listeAllerLinienKnotenpunkte.size(), k.getKnotenpunkt(), listeAllerLinienKnotenpunkte.getLast().getKnotenpunkt()));
                   int size = temp.size();
                    for (int i = size - 1; i > 0; i--) {
                        MKnotenpunkt vorgaenger = i == size - 1 ? k.getKnotenpunkt() : temp.get(i + 1).getKnotenpunkt();
                        MWegKnotenpunkt tempWegknoten = new MWegKnotenpunkt(size + i, temp.get(i).getKnotenpunkt(), vorgaenger);
                        listeAllerLinienKnotenpunkte.add(tempWegknoten);
                    }
                }
                System.out.println("found last");

            } else if (!k.equals(first)){
                searching = findPath(k.getVorgaenger(), k);
            }
        }
        return searching;
    }

    public boolean findPath(MKnotenpunkt currentPoint, MWegKnotenpunkt targetNode) {
        MTargetpointList wayPointList = new MTargetpointList();
        wayPointList.add(targetNode.getKnotenpunkt());

        SearchObject so = new SearchObject(currentPoint, wayPointList, 0);

        if (so.getArrayListZuBesuchenderWegpunkte().size() == 0) {
            System.out.println("something went wrooong");
            return false;
        } else {

            Path path = new Path(so, true);

            if (path.isEmpty()) {
                return false;
            } else {


                if (listeAllerLinienKnotenpunkte.size() > 1) {
                    //                  MWegKnotenpunkt wp = listeAllerLinienKnotenpunkte.pollLast();
//                   System.out.println(wp);
                    // path.firstElement().setVorgaenger(listeAllerLinienKnotenpunkte.getLast().getKnotenpunkt());
                }

                listeAllerLinienKnotenpunkte.addAll(path);

                return true;


                //  movePlane(plane);
            }
        }
    }

    public void setCircle(boolean circle) {
        this.circle = circle;
    }

    public boolean isCircle() {
        return circle;
    }

    public Deque<MWegKnotenpunkt> getListeAllerLinienKnotenpunkte() {
        return listeAllerLinienKnotenpunkte;
    }

    public void addWegknotenpunktToBack(MWegKnotenpunkt w) {
        listeAllerLinienKnotenpunkte.addLast(w);
    }

    public void edit() {
    }
}
