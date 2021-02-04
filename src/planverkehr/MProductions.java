package planverkehr;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.*;

//check, ob zu konsumierende gueter im storage verfuegbar sind
//resourcesAvailable();
//Nein -> weiterpruefen bis das der Fall ist, oder Duration abwarten

//check, ob zu produzierende gueter platz im storage haben
//spaceForResources();
//Nein -> weiterpruefen bis das der Fall ist, oder Duration abwarten

//gueter producen und consumen
//produceAndConsume();

//konsumierte gueter aus dem storage entfernen
//deleteConsumedResourcesFromStorage();
//produzierte gueter dem storage hinzufuegen
//addProducedResourcesToStorage();

//Duration abwarten
//waitDuration();

//repeat

//Problem: Gueter koennen jederzeit z.B. von vehicles aus dem Storage entfernt werden
//daher muessen die gueter und der platz im storage davor immer reserviert werden
//neue erkenntniss: storage lagert nur die zu konsumierenden gueter, aber nicht die zu produzierenden...
//wohin damit? -> produceStorage


//known Bugs:
//bei zwei konsumierbaren resourcen wird nur überprüft, ob die erste vorhanden ist -> konsum obwohl nur nur 1 der 2 resourcen
//vorhanden ist
//falls ein Productions Objekt mehrere unabhängige Produktionen z.B. construction yard hat, gibt es auch einzelne
//bzw. doppelte und identische lager dafür

public class MProductions {
    int duration;
    List<HashMap<String, Integer>> produce;
    List<HashMap<String, Integer>> consume;
    HashMap<String, Integer> storage; //current resource and quantity
    HashMap<String, Integer> storageRAW; //original resource and quantity
    HashMap<String, Integer> produceStorage; //storage for produced goods
    //sobald das gebäude gesetzt ist, soll die produktion beginnen
    private boolean buildingSet;

    public MProductions(int duration, List<HashMap<String, Integer>> produce, List<HashMap<String, Integer>> consume, HashMap<String, Integer> storageRAW) {
        this.duration = duration;
        this.produce = produce;
        this.consume = consume;
        this.storageRAW = storageRAW;
        produceStorage = new HashMap<>();
        zeroStorage();
        //initStorage(); //nur zum testen, füllt das lager mit jeweils 100 resourcen
        //consumeAndProduce(); //zum starten der produktion auskommentieren
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<HashMap<String, Integer>> getProduce() {
        return produce;
    }

    public void setProduce(List<HashMap<String, Integer>> produce) {
        this.produce = produce;
    }

    public List<HashMap<String, Integer>> getConsume() {
        return consume;
    }

    public void setConsume(List<HashMap<String, Integer>> consume) {
        this.consume = consume;
    }

    public HashMap<String, Integer> getStorage() {
        return storage;
    }

    public void setStorage(HashMap<String, Integer> storage) {
        this.storage = storage;
    }

    public HashMap<String, Integer> getStorageRAW() {
        return storageRAW;
    }

    public void setStorageRAW(HashMap<String, Integer> storageRAW) {
        this.storageRAW = storageRAW;
    }

    private void zeroStorage() {
        Set<String> keys = storageRAW.keySet();
        storage = new HashMap<>();
        for (String key : keys) {
            storage.put(key, 0);
        }
    }

    private void programmBeenden() {
        System.out.println("Programm wird beendet!");
        System.exit(-1);
    }

    private void initStorage() {
        storage.put("sand", 100);
        storage.put("glass", 100);
        storage.put("silicon", 100);
        storage.put("solar panels", 100);
        storage.put("methyl chloride", 100);
        storage.put("silicone", 100);
    }

    public boolean resourcesAvailable(String resource, int quantity) {
        int availableResource = storage.get(resource);
        if (quantity <= availableResource) {
            System.out.println("true");
            return true;
        } else {
            System.out.println("false");
            return false;
        }
    }

    private boolean spaceForResources(HashMap<String, Integer> transfer) {
        for (Map.Entry<String, Integer> entry : transfer.entrySet()) {
            if (storage.containsKey(entry.getKey())) {
                int currentQuantity = storage.get(entry.getKey());
                int originalSpace = storageRAW.get(entry.getKey());
                int availableSpace = originalSpace - currentQuantity;
                if (entry.getValue() <= availableSpace) {
                    System.out.println("Enough space");
                    return true;
                } else {
                    System.out.println("Not enough space");
                    return false;
                }
            } else {
                System.out.println("Factory can not store this resource!");
                return false;
            }
        }
        return false;
    }

    public boolean producedResourcesAvailable(String resource, int quantity) {
        int availableResource = produceStorage.get(resource);
        if (quantity <= availableResource) {
            System.out.println("true");
            return true;
        } else {
            System.out.println("false");
            return false;
        }
    }

    public boolean resourcesAvailableToConsume() {
        if (getConsume().size() > 0) {
            for (int i = 0; i < getConsume().size(); i++) {
                //hier liegt irgendwo der fehler, da immer nur die erste resource abgeprueft wird
                for (Map.Entry<String, Integer> entry : getConsume().get(i).entrySet()) {
                    System.out.println("EntrySet: " + getConsume().get(i).entrySet());
                    System.out.println("Entry: " + entry);
                    int availableResource = storage.get(entry.getKey());
                    int quantity = entry.getValue();
                    if (quantity <= availableResource) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public void consume() {
        if (getConsume().size() > 0) {
            System.out.println("getConsume: " + getConsume());
            for (int i = 0; i < getConsume().size(); i++) {
                for (Map.Entry<String, Integer> entry : getConsume().get(i).entrySet()) {
                    String key = entry.getKey();
                    int value = entry.getValue();
                    if (resourcesAvailableToConsume()) {
                        int availableResource = storage.get(key);
                        System.out.println("AvailableResource: " + key + " " + availableResource);
                        System.out.println("Quantity required : " + value);
                        if (!storageRAW.containsKey(key)) {
                            System.out.println("Resource unknown to this Building");
                            programmBeenden();
                        }
                        System.out.println("Before consume: " + storage);
                        deleteConsumedResourcesFromStorage(key, value);
                        System.out.println("After consume: " + storage);
                        System.out.println("ProduceStorage: " + produceStorage);
                        System.out.println("\n");
                    } else {
                        System.out.println("No resources available to consume");
                    }
                }
            }
        }
    }


    public void produce() {
        if (getProduce().size() > 0) {
            System.out.println("getProduce: " + getProduce());
            for (int i = 0; i < getProduce().size(); i++) {
                for (Map.Entry<String, Integer> entry : getProduce().get(i).entrySet()) {
                    String key = entry.getKey();
                    int value = entry.getValue();
                    if (resourcesAvailableToConsume() || getConsume().size() == 0) {
                        if (!produceStorage.containsKey(key)) {
                            produceStorage.put(key, 0);
                        }
                        System.out.println("Producing Resources: " + key + " " + value);
                        System.out.println("Current Storage : " + storage);
                        System.out.println("Before add: " + produceStorage);
                        addProducedResourcesToProduceStorage(key, value);
                        System.out.println("After add: " + produceStorage);
                        System.out.println("\n");
                    } else {
                        System.out.println("No resources available to produce");
                    }
                }
            }
        }
    }


    public void consumeAndProduce() {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(Config.tickFrequency.multiply(getDuration()).toSeconds()), e -> {
                System.out.println(Config.tickFrequency.multiply(getDuration()).toSeconds());
                produce();
                consume();
            }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        //timeline.setCycleCount(30);
        timeline.play();
    }

    private void addProducedResourcesToProduceStorage(String resource, int quantity) {
        produceStorage.put(resource, produceStorage.containsKey(resource) ? produceStorage.get(resource) + quantity : quantity);
    }

    private void deleteConsumedResourcesFromStorage(String resource, int quantity) {
        storage.put(resource, storage.containsKey(resource) ? storage.get(resource) - quantity : quantity);
    }

    private void deleteProducedResourcesFromProduceStorage(String resource, int quantity) {
        produceStorage.put(resource, produceStorage.get(resource) - quantity);
    }

    private void addTransportedResourcesToStorage(String resource, int quantity) {
        storage.put(resource, storage.containsKey(resource) ? storage.get(resource) + quantity : quantity);
    }

    public HashMap<String, Integer> giveGoodsToVehicle(MProductions MProductions, String kind, int quantity) {
        HashMap<String, Integer> taken = new HashMap<>();
        if (produceStorage.containsKey(kind)) {
            HashMap<String, Integer> before = MProductions.produceStorage;
            System.out.println("Before: " + before);
            if (producedResourcesAvailable(kind, quantity)) {
                deleteProducedResourcesFromProduceStorage(kind, quantity);
                HashMap<String, Integer> after = MProductions.produceStorage;
                System.out.println("After: " + after);

                for (String key : before.keySet()) {
                    if (after.keySet().contains(key) && key.equals(kind)) {
                        taken.put(key, quantity);
                    }
                }
            } else {
                System.out.println("Demanded quantity can not be supplied!");
            }
        } else {
            System.out.println("Resource is not available from this Building!");
        }
        System.out.println("Taken: " + taken);
        return taken;
    }

    public void takeGoodsFromVehicle(HashMap<String, Integer> transfer) {
        transfer.forEach((key, value) -> {
            if (spaceForResources(transfer)) {
                addTransportedResourcesToStorage(key, value);
            }
        });
    }
}
