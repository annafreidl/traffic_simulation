package planverkehr.verkehrslinien;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import planverkehr.EBuildType;
import planverkehr.EVehicleTypes;
import planverkehr.MVehicles;

import java.util.ArrayList;

public class linienConfigModel {
    private final ObservableList<String> options;
    private EVehicleTypes vehicleType;
    MVehicles selectedVehicle;
    ArrayList<MVehicles> vehicles;

    public linienConfigModel(EBuildType surfaceType, ArrayList<MVehicles> vehicles) {
        this.vehicles = vehicles;

        switch (surfaceType) {
            case rail -> this.vehicleType = EVehicleTypes.wagon;
            case road -> this.vehicleType = EVehicleTypes.road_vehicle;
            case airport -> this.vehicleType = EVehicleTypes.plane;
        }


        ArrayList<String> filtertList = new ArrayList<>();

        vehicles.forEach(v -> {
            if(v.getKindEnum().equals(vehicleType)){
                if(selectedVehicle == null){
                    selectedVehicle = v;
                }
                filtertList.add(v.getName());
            }
        });

        options = FXCollections.observableList(filtertList);

    }

    public ObservableList<String> getOptions (){
        return options;
    }



   public String getCargoString(){
       final String[] cargo = {""};
       selectedVehicle.getCargo().forEach((commodity, number) -> {
            cargo[0] += number + " ";
            cargo[0] += commodity + ", ";

        });

       String cargoString = cargo[0];
       cargoString = cargoString.substring(0, cargoString.length() - 2);

      return cargoString;
   }

    public void setSelectedVehicleByString(String newVehilce) {
        vehicles.forEach(mVehicles -> {
            if (mVehicles.getName().equals(newVehilce)) {
                selectedVehicle = mVehicles;
            }});
    }


}
