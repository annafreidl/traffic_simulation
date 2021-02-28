package planverkehr.verkehrslinien;


public class linienConfigController {
    linienConfigWindow view;
    linienConfigModel model;
    MLinie activeLinie;
    final linienConfigObject[] returnObject = new linienConfigObject[1];

    public linienConfigController(linienConfigWindow view, linienConfigModel model, MLinie activeLinie) {
        this.view = view;
        this.model = model;
        this.activeLinie = activeLinie;



        view.getFahrzeugInput().setOnAction(actionEvent -> {
            System.out.println(actionEvent);

            String newVehicleFromInput = view.getFahrzeugInput().getValue().toString();
            model.setSelectedVehicleByString(newVehicleFromInput);

            view.getCargoInfoLabel().setText(model.getCargoString());
            view.getSpeedNumberLabel().setText(model.selectedVehicle.getSpeed() + "");

            });

        view.getBackButton().setOnAction((e) -> {
            returnObject[0] = new linienConfigObject(view.getLinienNameInputField().getText(), model.selectedVehicle, false, true);
            view.getWindow().close();
        });

        view.getSaveButton().setOnAction((e) -> {
            returnObject[0] = new linienConfigObject(view.getLinienNameInputField().getText(), model.selectedVehicle, true, view.getCircleCB().isSelected());
            view.getWindow().close();
        });


    }

    public linienConfigObject showConfigWindow() {
        view.display("Verkehrslinien konfiguration");

        if(activeLinie.getVehicle() != null){
            view.getLinienNameInputField().setText(activeLinie.getName());
            view.getCircleCB().setSelected(activeLinie.isCircle());
            model.setSelectedVehicleByString(activeLinie.getVehicle().getName());
        }

        return returnObject[0];
    }
}
