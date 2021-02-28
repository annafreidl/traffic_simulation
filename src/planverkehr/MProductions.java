package planverkehr;

import java.util.*;

//known Bugs:
//bei zwei konsumierbaren resourcen wird nur überprüft, ob die erste vorhanden ist -> konsum obwohl nur nur 1 der 2 resourcen
//vorhanden ist und dadurch können auch negative werte erreicht werden
//bei leerem lager tritt dieser fehler nicht auf

public class MProductions {
    private int duration;
    private List<HashMap<String, Integer>> produce;
    private List<HashMap<String, Integer>> consume;

    HashMap<String, Integer> storage; //current resource and quantity
    HashMap<String, Integer> storageRAW; //original resource and quantity
    HashMap<String, Integer> produceStorage; //storage for produced goods
    MFactory factory;
    boolean foundation = false;
    boolean nave = false;
    boolean isProducing = false;
    double productionStart;


    public MProductions(int duration, List<HashMap<String, Integer>> produce, List<HashMap<String, Integer>> consume, HashMap<String, Integer> storageRAW) {
        this.duration = duration;
        this.produce = produce;
        this.consume = consume;
        this.storageRAW = storageRAW;
        produceStorage = new HashMap<>();
        zeroStorage();
    }


    public int getDuration() {
        return duration;
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


    public HashMap<String, Integer> getStorageRAW() {
        return storageRAW;
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

    public boolean producedResourcesAvailable(String resource, int quantity) {
        int availableResource = produceStorage.get(resource);
        if (quantity <= availableResource) {

            return true;
        } else {

            return false;
        }
    }

    /**
     * prüft ob resourcen vorhanden und bereit zum konsumieren sind
     * known bug: siehe oben
     *
     * @return
     */
    public boolean resourcesAvailableToConsume() {
        if (getConsume().size() > 0) {
            for (int i = 0; i < getConsume().size(); i++) {
                //hier liegt irgendwo der fehler, da bei gefülltem lager
                // immer nur die erste resource abgeprueft wird
                //bei leerem lager passt es komischerweise
                for (Map.Entry<String, Integer> entry : getConsume().get(i).entrySet()) {

                    if(storage.size() > 0) {
                        int availableResource = storage.get(entry.getKey());
                        int quantity = entry.getValue();
                        if (quantity <= availableResource) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * verbrauch der in consume angegeben gueter
     */


    public void consumeCommodities() {

        if (getConsume().size() > 0) {
            for (int i = 0; i < getConsume().size(); i++) {
                for (Map.Entry<String, Integer> entry : getConsume().get(i).entrySet()) {
                    String key = entry.getKey();
                    int value = entry.getValue();
                    if (resourcesAvailableToConsume()) {
                        int availableResource = storage.get(key);
                       
                        if (!storageRAW.containsKey(key)) {
                            System.out.println("Resource unknown to this Building");
                            programmBeenden();
                        }
                        
                        deleteConsumedResourcesFromStorage(key, value);

                    }
                }
            }
        }
    }

    public void startProduction(double tickNumber) {
    
        if (getProduce().size() > 0) {
            for (int i = 0; i < getProduce().size(); i++) {
                for (Map.Entry<String, Integer> entry : getProduce().get(i).entrySet()) {
                    String key = entry.getKey();
                    int value = entry.getValue();
                    if (resourcesAvailableToConsume() || getConsume().size() == 0 && key != null) {
                        isProducing = true;
                        productionStart = tickNumber;
                    }
                }
            }
        }
    }


    public void produceCommodities() {
        if (factory.getBuildType().equals(EBuildType.cathedral)){
            System.out.println("cathedral produces");
        }
        if (getProduce().size() > 0) {
            for (int i = 0; i < getProduce().size(); i++) {
                for (Map.Entry<String, Integer> entry : getProduce().get(i).entrySet()) {
                    String key = entry.getKey();
                    int value = entry.getValue();
                    if (resourcesAvailableToConsume() || getConsume().size() == 0 && key != null) {
                        if (!produceStorage.containsKey(key)) {
                            produceStorage.put(key, 0);
                            isProducing = false;
                            productionStart = -1;

                            consumeCommodities();
                        }
                       
                        addProducedResourcesToProduceStorage(key, value);
                       
                    }
                }
            }
        }
    }

    /**
     * gemeinsamer produce und consume sowie pruefung fuer own-scenario
     */
    public void consumeAndProduce() {
        produceCommodities();

//        if (!foundation) {
//            cathedralFoundationReady();
//        }
//        if (!nave) {
//            cathedralNaveReady();
//        }
    }

    /**
     * prueft ob die cathedral_foundation bereit zum ausbau ist und stoppt dann die weitere produktion von foundations,
     * da dieser schritt einmalig sein soll
     *
     * @return
     */
    private boolean cathedralFoundationReady() {
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

    /**
     * prueft ob die cathedral_nave bereit zum ausbau ist und stoppt dann die gesamte produktion der kathedrale,
     * da dieser schritt einmalig sein soll
     *
     * @return
     */
    private boolean cathedralNaveReady() {
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
        if (factory.getBuildType().equals(EBuildType.cathedral)) {
            if (resource.equals("cathedral_foundation") && factory.getCathedralState().equals(ECathedralState.ground)) {
                factory.setCathedralState(ECathedralState.foundation);

            } else if (resource.equals("cathedral_nave") && factory.getCathedralState().equals(ECathedralState.foundation)) {
                factory.setCathedralState(ECathedralState.nave);
            }
        }
        produceStorage.put(resource, produceStorage.containsKey(resource) ? produceStorage.get(resource) + quantity : quantity);
    }

    private void deleteConsumedResourcesFromStorage(String resource, int quantity) {
        storage.put(resource, storage.containsKey(resource) ? storage.get(resource) - quantity : quantity);
    }

    private void addTransportedResourcesToStorage(String resource, int quantity) {
        storage.put(resource, storage.containsKey(resource) ? storage.get(resource) + quantity : quantity);
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

    public boolean isProducing() {
        return isProducing;
    }

    public double getProductionStart() {
        return productionStart;
    }
}
