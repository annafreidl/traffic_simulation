package planverkehr.airport;

import java.util.Stack;

/*
public class Path extends Stack<WegKnotenpunkt> {
    boolean isSearching, changedWayPoint, noPathFound;
    SearchObject so;

    public Path() {

    }

    public Path(
        SearchObject so) {
        isSearching = true;
        changedWayPoint = false;
        int i = 0;

        this.so = so;
        Knotenpunkt planePosition = so.getCurrentNodeKnotenpunkt();

        if (so.getWayPointList().get(0).targetType.equals("ausflug")) {
            System.out.println("ausflug");
        }

        while (isSearching) {
            i++;
            if (so.getArrayListZuBesuchenderWegpunkte().size() == 0) {
                isSearching = false;
                so.addWaynodeToZuBesuchendeWegpunkte(so.getCurrentWayNode());
                System.out.println("keinen Weg gefunden");
            } else {

                //Ersten, zu besuchenden Wegpunkt aus der List der zubesuchendenWegpunkte zu der Liste der
                // Besuchten Elemente hinzufügen und dem aktuellen Wegknotenpunkt zuweisen
                so.setCurrentWayNode(so.getFirstElementZuBesuchenderWegpunkte());
                so.addCurrentWaynodeToBesuchterWegpunkte();

                // Füge jeden verfügbaren Nachbarn des aktuell betrachteten Wegpunkts zur Liste der gesichteten Wegpunkte hinzu
                addAvailableNeighbourNodes();

                //Wenn die Liste der zu besuchenden Wegpunkte leer ist, Fehler ausgeben und Suche abbrechen
                if (so.getArrayListZuBesuchenderWegpunkte().size() == 1) {
                    isSearching = false;
                    so.clearBesuchteWegpunkte();
                    System.out.println("keinen Weg gefunden");
                }

                // aktuell betrachteten Wegpunkt aus der Liste der zubesuchendenWegpunkte entfernen
                so.removeFirstFromZuBesuchendeWegpunkte();

                // Prüfen, ob die Liste überläuft
                if (so.arrayListBesuchterWegpunkte.size() > 4000) {

                    if (changedWayPoint) {

                        so.setCurrentWayNode(so.getFirstElementBesuchterWegpunkte());

                        if (so.getCurrentNodeKind().equals("concrete")) {
                            letPlaneWait();

                        } else {
                            System.out.println("++++++++++++DEADLOCK in search for path+++++++++++++");
                            isSearching = false;
                        }


                    } else {

                        tryWaitNodeAsTarget();


                    }
                }
            }
        }

        if (so.getArrayListZuBesuchenderWegpunkte().isEmpty()) {
            noPathFound = true;
        } else {
            noPathFound = false;


            so.setLastElementZuBesuchenderToCurrentNode();


            this.filterPath(planePosition);
        }
    }




    private void tryWaitNodeAsTarget() {

        so.setCurrentWayNode(so.getFirstElementBesuchterWegpunkte());
        so.clearBesuchteWegpunkte();
        so.clearZuBesuchendeWegpunkte();
        so.addCurrentWaynodeToBesuchterWegpunkte();
        so.addWaynodeToZuBesuchendeWegpunkte(so.currentWayNode);
        changedWayPoint = true;
    }

    private void letPlaneWait() {
        so.clearBesuchteWegpunkte();


        WegKnotenpunkt wp = new WegKnotenpunkt(so.getCurrentNodeBetretenUm() + 1, so.getCurrentNodeKnotenpunkt(), so.getCurrentNodeKnotenpunkt());
        WegKnotenpunkt wp2 = new WegKnotenpunkt(so.getCurrentNodeBetretenUm() + 2, so.getCurrentNodeKnotenpunkt(), wp.knotenpunkt);

        so.addWaynodeToBesuchteWegpunkte(wp);
        so.addWaynodeToBesuchteWegpunkte(wp2);
        wp.knotenpunkt.isTempTarget = true;
        isSearching = false;
    }

    private void addAvailableNeighbourNodes() {
        // 1. Für jeden Nachbarknoten von currentWayNode:
        for (Knotenpunkt k : so.getNeighboursOfCurrentWaynode()) {
            //erstelle aus dem Knotenpunkt einen Wegpunkt, wenn er zu dem Tick betretbar ist und existiert
            if (k != null
                && k.isFreeFor(so.getCurrentNodeBetretenUm() + 1)
            ) {
                WegKnotenpunkt tempWegpunkt = new WegKnotenpunkt(so.getCurrentNodeBetretenUm() + 1, k, so.getCurrentNodeKnotenpunkt());

                //  1. Prüfen ob der Knoten zu diesem Zeitpunkt (tick +1) noch nicht betrachtet wurde
                if (!so.isZuBesuchendeWegeContaining(tempWegpunkt)) {

                    // --> JA:
                    // ist es unser Ziel?
                    if (so.isWayPointListContaining(tempWegpunkt.knotenpunkt)) {
                        // --> JA: while abbrechen und zu besuchen hinzufügen
                        so.addWaynodeToZuBesuchendeWegpunkte(tempWegpunkt);
                        so.addWaynodeToBesuchteWegpunkte(tempWegpunkt);

                        isSearching = false;
                        if (!tempWegpunkt.knotenpunkt.targetType.equals("ausflug")) {
                            tempWegpunkt.knotenpunkt.setBlocked();
                        }
                        break;
                    } else {

                        // --> NEIN: zu gesichtet hinzufügen
                        so.addWaynodeToZuBesuchendeWegpunkte(tempWegpunkt);
                    }
                }
            }
        }

    }

    //Filtert alle Punkte aus dem Pfad heraus, die nicht Teil des Weges sind
    public void filterPath(Knotenpunkt planePosition) {
        Stack<WegKnotenpunkt> wpStack = new Stack<>();
        wpStack.push(so.currentWayNode);


        //fügt wartezeit hinzu wenn es sich um den Zielknoten und nicht einen verlängerten Aufenthalt handelt hinzu
        if (!so.isTempTarget()) {
            for (int i = 1; i <= so.getWaitTime(); i++) {
                WegKnotenpunkt tempWP = new WegKnotenpunkt(so.getCurrentNodeBetretenUm() + i, so.getCurrentNodeKnotenpunkt(), so.getVorgaenger());
                wpStack.push(tempWP);
            }
        }

        //Knotenpunk wird dann zur finalen Wegliste hinzgefügt, wenn der Vorgänger sein Vorgänger ist. Gestartet wird beim Zielknoten.
        // Zusätzlich werden die Blockaden für alle passierten knoten hinzugefügt
        for (int i = so.arrayListBesuchterWegpunkte.size() - 1; i > 0; i--) {

            WegKnotenpunkt prevWegpunkt = so.getWegKnotenpunktAtIndex(i - 1);
            Knotenpunkt prev = prevWegpunkt.knotenpunkt;

            if (prevWegpunkt.betretenUm + 1 == so.getCurrentNodeBetretenUm() &&
                (prev == so.getVorgaenger())) {
                so.setCurrentWayNode(prevWegpunkt);
                wpStack.add(prevWegpunkt);

                prev.addEntryToBlockedForTickList(prevWegpunkt.betretenUm);
                prev.addEntryToBlockedForTickList(prevWegpunkt.betretenUm + 1);

            }
        }
        if (planePosition != wpStack.firstElement().knotenpunkt) {
            wpStack.pop();
        }

        this.clear();
        this.addAll(wpStack);
    }


}

 */