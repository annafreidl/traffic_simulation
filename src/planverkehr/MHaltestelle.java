package planverkehr;

import planverkehr.graph.MKnotenpunkt;

import java.util.ArrayList;
import java.util.HashMap;

public class MHaltestelle {
    int id;
    boolean connectsWithFactory;
    ArrayList<Buildings> buildingsList;
    ArrayList<MTile> coveredTilesList;
    HashMap<String, MKnotenpunkt> knotenpunkteList;
    HashMap<MCommodity, Integer> commodityStorage;
    HashMap<String, Integer> stringStorage;
    MFactory factory;


    public MHaltestelle(int id, Buildings building, MTile tile) {
        this.id = id;
        buildingsList = new ArrayList<>();
        buildingsList.add(building);
        commodityStorage = new HashMap<>();
        stringStorage = new HashMap<>();
        coveredTilesList = new ArrayList<>();
        coveredTilesList.add(tile);
        knotenpunkteList = new HashMap<>();

        connectsWithFactory = false;
    }

    public MHaltestelle(int id, MFactory building, MTile tile) {
        this.id = id;
        buildingsList = new ArrayList<>();
        buildingsList.add(building);
        commodityStorage = new HashMap<>();
        stringStorage = new HashMap<>();
        coveredTilesList = new ArrayList<>();
        coveredTilesList.add(tile);
        knotenpunkteList = new HashMap<>();

        connectsWithFactory = true;
        factory = building;
    }


    public void addBuilding(MTile feld, Buildings buildingToBeBuilt) {
        buildingsList.add(buildingToBeBuilt);
        coveredTilesList.add(feld);
    }

    public void removeFeld(MTile mTile) {
        buildingsList.remove(mTile.getConnectedBuilding());
        for (MKnotenpunkt knotenpunkt : mTile.getKnotenpunkteArray()) {
            knotenpunkteList.remove(knotenpunkt.getVisibleCoordinate().toStringCoordinates());
        }
        coveredTilesList.remove(mTile);
    }

    public void addKnotenpunkt(MKnotenpunkt knotenpunkt) {
        knotenpunkteList.put(knotenpunkt.getVisibleCoordinate().toStringCoordinates(), knotenpunkt);
    }

    public HashMap<String, MKnotenpunkt> getKnotenpunkteList() {
        return knotenpunkteList;
    }

    public HashMap<MCommodity, Integer> getCommodities() {
        return commodityStorage;
    }

    public int getId() {
        return id;
    }

    public void removeFromStorage(MCommodity mCommodity, Integer quantity) {
        stringStorage.put(mCommodity.getName(), stringStorage.get(mCommodity.getName()) - quantity);
        commodityStorage.put(mCommodity, commodityStorage.get(mCommodity) - quantity);
    }

    public void takeGoodsFromVehicle(MCommodity resource, Integer quantity) {
        stringStorage.put(resource.getName(), stringStorage.containsKey(resource.getName()) ? stringStorage.get(resource.getName()) + quantity : quantity);
        commodityStorage.put(resource, commodityStorage.containsKey(resource) ? commodityStorage.get(resource) + quantity : quantity);

        if (connectsWithFactory) {
            factory.addGoods(resource, quantity);
        }
    }

    public MFactory getFactory() {
        return factory;
    }

    @Override
    public String toString() {
        return "MHaltestelle{" +
            "id=" + id +
            ", buildingsList=" + buildingsList +
            ", stringStorage=" + stringStorage +
            '}';
    }
}
