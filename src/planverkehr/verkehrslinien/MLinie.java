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
    boolean circlePath;


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
        boolean searching = true;
        if (!allHaltestellenCovered() || isCircle() != circlePath) {
            MWegKnotenpunkt last = listOfHaltestellenKnotenpunkten.peekLast();
            MWegKnotenpunkt first = listOfHaltestellenKnotenpunkten.peek();
            listeAllerLinienKnotenpunkte.clear();


            for (MWegKnotenpunkt k :
                listOfHaltestellenKnotenpunkten
            ) {
                if (!searching) {
                    break;
                } else if (k.equals(last)) {
                    //special treatment last
                    searching = findPath(k.getVorgaenger(), k);

                    if (isCircle()) {
                        searching = findPath(k.getKnotenpunkt(), first);
                        circlePath = true;
                    } else {
                        listeAllerLinienKnotenpunkte.add(new MWegKnotenpunkt(listeAllerLinienKnotenpunkte.size(), k.getKnotenpunkt(), listeAllerLinienKnotenpunkte.getLast().getKnotenpunkt()));

                        revertList(listeAllerLinienKnotenpunkte, k);
                        revertList(listOfHaltestellenKnotenpunkten, k);


                        circlePath = false;

                    }
                    System.out.println("found last");

                } else if (!k.equals(first)) {
                    searching = findPath(k.getVorgaenger(), k);
                }
            }
        }
        return searching;

    }

    private void revertList(Deque<MWegKnotenpunkt> knotenpunktListe, MWegKnotenpunkt lastWegKnotenpunkt) {
        ArrayList<MWegKnotenpunkt> tempAlle = new ArrayList<>(knotenpunktListe);

        int size = tempAlle.size();
        for (int i = size - 2; i > 0; i--) {
            MKnotenpunkt vorgaenger = tempAlle.get(i + 1).getKnotenpunkt();
            MWegKnotenpunkt tempWegknoten = new MWegKnotenpunkt(size + i, tempAlle.get(i).getKnotenpunkt(), vorgaenger);
            knotenpunktListe.add(tempWegknoten);
        }

        knotenpunktListe.removeLast();
        knotenpunktListe.getFirst().setVorgaenger(knotenpunktListe.getLast().getKnotenpunkt());

    }

    private boolean allHaltestellenCovered() {
        if (listeAllerLinienKnotenpunkte.size() > 0) {
            for (MWegKnotenpunkt wp : listOfHaltestellenKnotenpunkten) {
                if (!listeAllerLinienKnotenpunkte.contains(wp)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
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

                listeAllerLinienKnotenpunkte.addAll(path);

                return true;
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

    public int getID() {
        return ID;
    }

    public void addWegknotenpunktToBack(MWegKnotenpunkt w) {
        listeAllerLinienKnotenpunkte.addLast(w);
    }


    public boolean contains(MKnotenpunkt linienKnotenpunkt) {

        for (MWegKnotenpunkt mWegKnotenpunkt : listOfHaltestellenKnotenpunkten) {
            if (mWegKnotenpunkt.getKnotenpunkt().equals(linienKnotenpunkt)) {
                return true;
            }
        }
        return false;
    }
}
