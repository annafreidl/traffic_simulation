package planverkehr;

import java.util.*;

//known Bugs:
//bei zwei konsumierbaren resourcen wird nur überprüft, ob die erste vorhanden ist -> konsum obwohl nur nur 1 der 2 resourcen
//vorhanden ist und dadurch können auch negative werte erreicht werden
//falls ein Productions Objekt mehrere unabhängige Produktionen z.B. construction yard hat, gibt es auch einzelne
//bzw. doppelte und identische lager dafür

public class MProductions {
    int duration;
    List<HashMap<String, Integer>> produce;
    List<HashMap<String, Integer>> consume;

    HashMap<String, Integer> storage; //current resource and quantity
    HashMap<String, Integer> storageRAW; //original resource and quantity
    HashMap<String, Integer> produceStorage; //storage for produced goods
    MFactory factory;
    boolean foundation = false;
    boolean nave = false;


    public MProductions(int duration, List<HashMap<String, Integer>> produce, List<HashMap<String, Integer>> consume, HashMap<String, Integer> storageRAW) {
        this.duration = duration;
        this.produce = produce;
        this.consume = consume;
        this.storageRAW = storageRAW;
        produceStorage = new HashMap<>();
        zeroStorage();
        initStorage();
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

    //nur zum testen, füllt das lager mit resourcen für jeweils 25 produktionsschritte
    private void initStorage() {
        for (int i = 0; i < getConsume().size(); i++) {
            for (Map.Entry<String, Integer> entry : getConsume().get(i).entrySet()) {
                storage.put(entry.getKey(), entry.getValue() * 25);
            }
        }
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
                //hier liegt irgendwo der fehler, da bei gefülltem lager
                // immer nur die erste resource abgeprueft wird
                //bei leerem lager passt es komischerweise
                for (Map.Entry<String, Integer> entry : getConsume().get(i).entrySet()) {
                    //System.out.println("EntrySet: " + getConsume().get(i).entrySet());
                    //System.out.println("Entry: " + entry);
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


    public void consumeCommodities() {
        if (getConsume().size() > 0) {
            for (int i = 0; i < getConsume().size(); i++) {
                for (Map.Entry<String, Integer> entry : getConsume().get(i).entrySet()) {
                    String key = entry.getKey();
                    int value = entry.getValue();
                    if (resourcesAvailableToConsume()) {
                        int availableResource = storage.get(key);
                        System.out.println("Duration: " + getDuration());
                        System.out.println("getConsume: " + getConsume());
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
                    }
                }
            }
        }
    }


    public void produceCommodities() {
        if (getProduce().size() > 0) {
            for (int i = 0; i < getProduce().size(); i++) {
                for (Map.Entry<String, Integer> entry : getProduce().get(i).entrySet()) {
                    String key = entry.getKey();
                    int value = entry.getValue();
                        if (resourcesAvailableToConsume() || getConsume().size() == 0) {
                            if (!produceStorage.containsKey(key)) {
                                produceStorage.put(key, 0);
                            }
                            System.out.println("Duration: " + getDuration());
                            System.out.println("getProduce: " + getProduce());
                            System.out.println("Producing Resources: " + key + " " + value);
                            System.out.println("Current Storage : " + storage);
                            System.out.println("Before add: " + produceStorage);
                            addProducedResourcesToProduceStorage(key, value);
                            System.out.println("After add: " + produceStorage);
                            System.out.println("\n");
                        }
                    }
                }
            }
        }

    public void consumeAndProduce() {
        System.out.println("EBuildType: " + factory.getEbuildType());
        produceCommodities();
        consumeCommodities();
        if (!foundation){ cathedralFoundationReady();}
        if (!nave){cathedralNaveReady();}
    }

    public boolean cathedralFoundationReady() {
        if (factory.getBuildingName().equals("cathedral")) {
            for (Map.Entry<String, Integer> entry : produceStorage.entrySet()) {
                if (entry.getKey().equals("cathedral_foundation") && entry.getValue() >= 1) {
                    List<HashMap<String, Integer>> newConsume = new ArrayList<>(getConsume());
                    List<HashMap<String, Integer>> newProduce = new ArrayList<>(getProduce());
                    newConsume.remove(0);
                    newProduce.remove(0);
                    foundation = true;
                    factory.setCathedralBuildTypeToFoundation();
                    setConsume(newConsume);
                    setProduce(newProduce);
                    produceStorage.clear();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cathedralNaveReady() {
        if (factory.getBuildingName().equals("cathedral")) {
            for (Map.Entry<String, Integer> entry : produceStorage.entrySet()) {
                if (entry.getKey().equals("cathedral_nave") && entry.getValue() >= 1) {
                    List<HashMap<String, Integer>> newConsume = new ArrayList<>(getConsume());
                    List<HashMap<String, Integer>> newProduce = new ArrayList<>(getProduce());
                    newConsume.remove(0);
                    newProduce.remove(0);
                    nave = true;
                    factory.setCathedralBuildTypeToNave();
                    setConsume(newConsume);
                    setProduce(newProduce);
                    produceStorage.clear();
                    return true;
                }
            }
        }
        return false;
    }

    private void addProducedResourcesToProduceStorage(String resource, int quantity) {
        MCommodity commodity = new MCommodity(resource, ECommodityTypes.valueOf(resource.replace(" ", "_")), 1, factory.knotenpunkt);
        factory.addToStorage(commodity, quantity);
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

    private boolean spaceForResources(String resource, Integer quantity) {

        if (storage.containsKey(resource)) {
            int currentQuantity = storage.get(resource);
            int originalSpace = storageRAW.get(resource);
            int availableSpace = originalSpace - currentQuantity;
            if (quantity <= availableSpace) {
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

    public void takeGoodsFromVehicle(String resource, Integer quantity) {
        if (spaceForResources(resource, quantity)) {
            addTransportedResourcesToStorage(resource, quantity);
        }
    }

    public void setFactory(MFactory factory) {
        this.factory = factory;
    }


    public HashMap<String, Integer> getProduceStorage() {
        return produceStorage;
    }

    public Buildings getFactory() {
        return factory;
    }

    public boolean consumesResource(String resource) {
        for (HashMap<String, Integer> consumationGood : consume) {
            if (consumationGood.containsKey(resource)) {
                return true;
            }
        }
        return false;
    }
}
