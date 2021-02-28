package planverkehr;


import planverkehr.graph.MKnotenpunkt;
import planverkehr.graph.MTargetpointList;
import planverkehr.graph.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MFactory extends Buildings {
    MTargetpointList listOfFactoriesNodes;
    ArrayList<MFactory> listOfFactories;
    MKnotenpunkt knotenpunkt;
    MHaltestelle haltestelle;
    HashMap<String, Integer> allProductionCommodities;
    HashMap<String, Integer> allConsummationCommodities;
    Random randomFactoryChooser = new Random();

    double lastTick = 0;


    HashMap<MFactory, Path> pathToFactories;

    HashMap<String, Integer> storage; //current resource and quantity
    HashMap<String, Integer> storageRAW; //original resource and quantity
    HashMap<String, Integer> produceStorage; //storage for produced goods


    public MFactory(Buildings building) {
        super(building);

        createOverallProductionAndConsummationMap();

        createStorage();

        if (getBuildType().equals(EBuildType.cathedral)) {
            setCathedralState(ECathedralState.ground);
        }
    }

    private void createPathToFactories() {
        pathToFactories = new HashMap<>();

        for (MFactory factory : listOfFactories) {
            pathToFactories.put(factory, new Path());
        }
    }

    private void createStorage() {
        storage = new HashMap<>(); //current resource and quantity
        storageRAW = new HashMap<>(); //original resource and quantity
        produceStorage = new HashMap<>(); //storage for produced goods

        for (MProductions production : this.getProductions()) {
            storage.putAll(production.getStorage());
            storageRAW.putAll(production.getStorageRAW());
            produceStorage.putAll(production.getProduceStorage());
        }
    }

    //called when game is started
    public void startProductionAndConsumption() {
        List<MProductions> productionsList = this.getProductions();
        if (productionsList != null) {
            for (MProductions production : productionsList) {
                production.setFactory(this);

            }
        }
    }

    public void produceAndConsume(double tickNumber) {
        List<MProductions> productionsList = this.getProductions();
        if (productionsList != null && tickNumber - lastTick > 1 ) {
            lastTick = tickNumber;
            for (MProductions production : productionsList) {
                if (production.isProducing() && tickNumber > production.getDuration() + production.getProductionStart()) {
                    production.consumeAndProduce();
                } else if (!production.isProducing()) {
                    production.startProduction(tickNumber);
                }
            }
        }
    }

    public void setKnotenpunkt(MKnotenpunkt knotenpunkt) {
        this.knotenpunkt = knotenpunkt;
    }

    public MKnotenpunkt getKnotenpunkt() {
        return knotenpunkt;
    }

    public void setHaltestelle(MHaltestelle haltestelle) {
        this.haltestelle = haltestelle;
    }


    public void setListOfFactoriesNodes(ArrayList<MFactory> listOfAllFactories) {
        MTargetpointList listOfFactoryNodes = new MTargetpointList();
        listOfFactories = new ArrayList<>();
        for (MFactory factory : listOfAllFactories) {
            if (!factory.equals(this) && factory.consumes(this.getAllProductionCommodities())) {
                listOfFactoryNodes.add(factory.getKnotenpunkt());
                listOfFactories.add(factory);

            }
        }
        this.listOfFactoriesNodes = listOfFactoryNodes;

        createPathToFactories();
    }

    private boolean consumes(HashMap<String, Integer> production) {
        final boolean[] isConsuming = new boolean[1];
        isConsuming[0] = false;
        allConsummationCommodities.forEach((commodity, quantity) -> production.forEach((commodity2, quantity2) -> {
            if (commodity.equals(commodity2)) {
                isConsuming[0] = true;
            }
        }));
        return isConsuming[0];
    }

    private void createOverallProductionAndConsummationMap() {
        allConsummationCommodities = new HashMap<>();
        allProductionCommodities = new HashMap<>();
        for (MProductions production : this.getProductions()) {
            for (HashMap<String, Integer> productionMap : production.getProduce()
            ) {
                productionMap.forEach((commodity, quantity) -> allProductionCommodities.put(commodity, allProductionCommodities.containsKey(commodity) ? allProductionCommodities.get(commodity) + quantity : quantity));
            }

            for (HashMap<String, Integer> consummationMap : production.getConsume()
            ) {
                consummationMap.forEach((commodity, quantity) -> allConsummationCommodities.put(commodity, allConsummationCommodities.containsKey(commodity) ? allConsummationCommodities.get(commodity) + quantity : quantity));
            }
        }
    }


    public HashMap<String, Integer> getAllProductionCommodities() {
        return allProductionCommodities;
    }

    public void addToStorage(MCommodity commodity, int quantity) {
        if (getBuildType().equals(EBuildType.cathedral)) {
            addTransportedResourcesToStorage(commodity.getName(), quantity);
        } else {
            ArrayList<MFactory> targetFactoriesList = new ArrayList<>();
            MTargetpointList targetpointListfactories = new MTargetpointList();
            for (MFactory factory : listOfFactories) {
                if (factory.allConsummationCommodities.containsKey(commodity.getName())) {
                    targetFactoriesList.add(factory);
                }
            }


            ArrayList<Path> pathList = new ArrayList<>();

            for (MFactory mFactory : targetFactoriesList) {
                if (pathToFactories.get(mFactory).size() > 0) {
                    pathList.add(pathToFactories.get(mFactory));
                } else {
                    targetpointListfactories.add(mFactory.getKnotenpunkt());
                }
            }

            if (targetpointListfactories.size() > 0) {
                if (commodity.searchPath(targetpointListfactories)) {
                    //  storage.put(commodity, quantity);
                    pathToFactories.put(commodity.haltestellenStops.lastElement().getKnotenpunkt().getHaltestelle().getFactory(), commodity.haltestellenStops);
                    haltestelle.commodityStorage.put(commodity, quantity);
                    haltestelle.stringStorage.put(commodity.getName(), quantity);
                } else if (pathList.size() > 1) {
                    int i = randomFactoryChooser.nextInt(0) + pathList.size();
                    commodity.setPath(pathList.get(i));
                    haltestelle.commodityStorage.put(commodity, quantity);
                    haltestelle.stringStorage.put(commodity.getName(), quantity);
                } else if (pathList.size() == 1) {
                    commodity.setPath(pathList.get(0));
                    haltestelle.commodityStorage.put(commodity, quantity);
                    haltestelle.stringStorage.put(commodity.getName(), quantity);
                }

            }
        }
    }

    private boolean spaceForResources(String resource, int quantity) {

        return checkStorageByKey(resource, quantity, storage, storageRAW);
    }

    static boolean checkStorageByKey(String resource, int quantity, HashMap<String, Integer> storage, HashMap<String, Integer> storageRAW) {
        if (storage.containsKey(resource)) {
            int currentQuantity = storage.get(resource);
            int originalSpace = storageRAW.get(resource);
            int availableSpace = originalSpace - currentQuantity;
            if (quantity <= availableSpace) {

                return true;
            } else {
       
                return false;
            }
        } else {

            return false;
        }
    }

    private void addTransportedResourcesToStorage(String resource, int quantity) {
        storage.put(resource, storage.containsKey(resource) ? storage.get(resource) + quantity : quantity);
        for (MProductions productions : getProductions()
        ) {
            if (productions.consumesResource(resource)) {
                productions.takeGoodsFromVehicle(resource, quantity);
                break;
            }
        }
    }


    public void addGoods(MCommodity resource, Integer quantity) {
        if (spaceForResources(resource.getName(), quantity)) {
            addTransportedResourcesToStorage(resource.getName(), quantity);
        }
    }
}
