package beans;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import localization.LocalizationManager;
import model.VehicleType;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.RowEditEvent;

import dao.VehicleTypeDAO;


@Named("vehicleTypeBean")
@ViewScoped
public class VehicleTypeBean implements Serializable {

    private static final long serialVersionUID = -7598254029056142014L;
    private List<VehicleType> vehicleTypes;
    @Inject
    private VehicleTypeDAO vehicleTypeDAO;
    private String newVehicleTypeName;
    private boolean addNewVehicleType;


    public VehicleTypeBean() {
        addNewVehicleType = false;
    }

    @PostConstruct
    public void init() {
        vehicleTypes = vehicleTypeDAO.getAll();
    }


    //getters
    public String getNewVehicleTypeName() {
        return newVehicleTypeName;
    }

    public List<VehicleType> getVehicleTypes() {
        return vehicleTypes;
    }

    public boolean isAddNewVehicleType() {
        return addNewVehicleType;
    }


    //setters
    public void setNewVehicleTypeName(String newVehicleTypeName) {
        this.newVehicleTypeName = newVehicleTypeName;
    }

    public void setAddNewVehicleType(boolean addNewVehicleType) {
        this.addNewVehicleType = addNewVehicleType;
    }


    //methods
    public void showMessage(String name) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(
                LocalizationManager.getLocalizedText("vehicleTypeSavedMessage1", name),
                LocalizationManager.getLocalizedText("vehicleTypeSavedMessage2")));
    }

    public void onEditEnd(RowEditEvent event) {
        VehicleType vehicleType = (VehicleType) event.getObject();
        if (vehicleType.getId() == null) {
            vehicleTypeDAO.save(vehicleType);
        } else {
            vehicleTypeDAO.update(vehicleType);
        }
        showMessage(vehicleType.getName());
    }

    public void addVehicleType() {
        addNewVehicleType = true;
    }

    public void saveAddedNewVehicleType() {
        VehicleType vehicleType = new VehicleType();
        vehicleType.setName(newVehicleTypeName);
        vehicleTypeDAO.save(vehicleType);

        vehicleTypes.add(vehicleType);

        showMessage(newVehicleTypeName);
        newVehicleTypeName = "";
        addNewVehicleType = false;
    }

    public void cancelAddingNewVehicleType() {
        addNewVehicleType = false;
    }

    public void deleteVehicleType(VehicleType vehicleType) {
        vehicleTypeDAO.delete(vehicleType);
        vehicleTypes = vehicleTypeDAO.getAll();

        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                LocalizationManager.getLocalizedText("vehicleTypeDeletedMessage1", vehicleType.getName()),
                LocalizationManager.getLocalizedText("vehicleTypeDeletedMessage2")));
    }

}
