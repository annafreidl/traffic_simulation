package planverkehr.verkehrslinien;

import javafx.scene.paint.Color;
import planverkehr.EBuildType;
import planverkehr.MHaltestelle;
import planverkehr.MVehicles;
import planverkehr.graph.*;
import planverkehr.transportation.ESpecial;

import java.util.*;

public class MLinie {
    String name;
    int ID, haltestellenID;
    Deque<MWegKnotenpunkt> listOfHaltestellenKnotenpunkten;
    Deque<MWegKnotenpunkt> listeAllerLinienKnotenpunkte;
    HashMap<Integer, MHaltestelle> listOfHaltestellen;
    MVehicles vehicle;
    EBuildType type;
    Color color;
    Random rand = new Random();
    boolean circle;
    boolean circlePath;


    public MLinie(int linienID) {
        this.ID = linienID;
        listOfHaltestellenKnotenpunkten = new ArrayDeque<>();
        listOfHaltestellen = new HashMap<>();
        listeAllerLinienKnotenpunkte = new ArrayDeque<>();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        color = Color.color(r, g, b);
    }

    public void addHaltestelle(MKnotenpunkt k) {
        boolean isFirstNode = listOfHaltestellenKnotenpunkten.isEmpty();
        MKnotenpunkt vorgaenger = isFirstNode ? k : listOfHaltestellenKnotenpunkten.getLast().getKnotenpunkt();
        int realHaltestellenID = k.getHaltestelle().getId();
        if (isFirstNode) {
            type = k.getSurfaceType();
            listOfHaltestellenKnotenpunkten.add(new MWegKnotenpunkt(haltestellenID, k, vorgaenger));
            listOfHaltestellen.put(realHaltestellenID, k.getHaltestelle());
            haltestellenID++;
        } else if (type.equals(k.getSurfaceType()) || k.getTargetType().equals(ESpecial.FACTORY) && !listOfHaltestellen.containsKey(realHaltestellenID)) {
            listOfHaltestellenKnotenpunkten.add(new MWegKnotenpunkt(haltestellenID, k, vorgaenger));
            listOfHaltestellen.put(realHaltestellenID, k.getHaltestelle());
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
                        if (listeAllerLinienKnotenpunkte.size() < 1) {
                            searching = findPath(k.getVorgaenger(), k);
                        }
                        if (searching) {

                            listeAllerLinienKnotenpunkte.add(new MWegKnotenpunkt(listeAllerLinienKnotenpunkte.size(), k.getKnotenpunkt(), listeAllerLinienKnotenpunkte.getLast().getKnotenpunkt()));

                            revertList(listeAllerLinienKnotenpunkte);
                            revertList(listOfHaltestellenKnotenpunkten);

                            circlePath = false;
                        }

                    }
                    System.out.println("found last");

                } else if (!k.equals(first)) {
                    searching = findPath(k.getVorgaenger(), k);
                }
            }
        }
        return searching;

    }

    private void revertList(Deque<MWegKnotenpunkt> knotenpunktListe) {
        ArrayList<MWegKnotenpunkt> tempAlle = new ArrayList<>(knotenpunktListe);

        int size = tempAlle.size();
        for (int i = size - 2; i > 0; i--) {
            MKnotenpunkt vorgaenger = tempAlle.get(i + 1).getKnotenpunkt();
            MWegKnotenpunkt tempWegknoten = new MWegKnotenpunkt(size + i, tempAlle.get(i).getKnotenpunkt(), vorgaenger);
            knotenpunktListe.add(tempWegknoten);
        }

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
            System.out.println("MLinie[findPath]: something went wrong");
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

    public HashMap<Integer, MHaltestelle> getListOfHaltestellen() {
        return listOfHaltestellen;
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
