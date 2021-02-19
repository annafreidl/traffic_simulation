package planverkehr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MHaltestelle {
    int id;
    ArrayList<Buildings> buildingsList;
    ArrayList<MTile> coveredTilesList;
    HashMap<MCommodity, Integer> storage;

    public MHaltestelle(int id, Buildings building, MTile tile) {
        this.id = id;
        buildingsList = new ArrayList<>();
        buildingsList.add(building);
        storage = new HashMap<>();
        coveredTilesList = new ArrayList<>();
        coveredTilesList.add(tile);


    }

    public void putToStorage(MCommodity commodity, int amount) {
        int newStorage = storage.remove(commodity) + amount;
        storage.put(commodity, newStorage);
    }

    public Optional<HashMap<MCommodity, Integer>> getFromStorage(HashMap<String, Integer> capacities, ArrayList<String> listeDerHaltestellen) {
        HashMap<MCommodity, Integer> map = new HashMap<>();
        return Optional.ofNullable(map);
    }


    public void addBuilding(MTile feld, Buildings buildingToBeBuilt) {
        buildingsList.add(buildingToBeBuilt);
        coveredTilesList.add(feld);
    }

    public void removeFeld(MTile mTile) {
        buildingsList.remove(mTile.getConnectedBuilding());
        coveredTilesList.remove(mTile);
    }
}
