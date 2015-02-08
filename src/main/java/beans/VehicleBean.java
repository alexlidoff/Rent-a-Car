package beans;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import localization.LocalizationManager;
import managers.VehicleManager;
import model.Vehicle;
import model.VehicleClass;
import model.VehicleType;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import service.TransBeanCommunication;
import dao.VehicleTypeDAO;


@Named(value = "vehicleBean")
@ViewScoped
public class VehicleBean implements Serializable {
    
    private static final long serialVersionUID = -3765613690894750485L;
    @Inject
    private Vehicle vehicle;
    @Inject
    private VehicleManager vehicleManager;
    @Inject
    private VehicleTypeDAO vehicleTypeDAO; 
    @Inject
    private ImageUploadBean imageUploadBean;
     
    
    public VehicleBean() {
    }
    
    @PostConstruct
    private void init() {
	//getting vehicle from flash
	Vehicle vehicleToOpen = 
		(Vehicle) TransBeanCommunication.getValue("vehicleToOpen");
	if (vehicleToOpen != null) {
	    vehicle = vehicleToOpen;
	}
	imageUploadBean.setVehicle(vehicle);
    }
    
    //getters
    public Vehicle getVehicle() {
	return vehicle;
    }


    //setters
    public void setVehicle(Vehicle vehicle) {
	this.vehicle = vehicle;
    }

    
    //methods
    public String rentVehicle(Vehicle vehicle) {
	//setting chosen vehicle to flash to get it in ApplicationBean
	TransBeanCommunication.putValue("chosenVehicle", vehicle);
	return "openApplicationForm";
    }
    
    public VehicleClass[] getVehicleClasses() {
	return VehicleClass.values();
    }
    
    public List<VehicleType> getVehicleTypes() {
	return vehicleTypeDAO.getAll(); 
    }
    
    public void handleFileUpload(FileUploadEvent event) {
	UploadedFile file = event.getFile();
	vehicle.setPhoto(file.getContents());
	imageUploadBean.setVehicle(vehicle);
    }
    
    public String saveVehicle() {
	vehicleManager.saveVehicle(vehicle);
	
	FacesContext context = FacesContext.getCurrentInstance();
	context.getExternalContext().getFlash().setKeepMessages(true);
	context.addMessage(null, new FacesMessage(
		LocalizationManager.getLocalizedText("vehicleSavedMessage1", vehicle.getName()),
		LocalizationManager.getLocalizedText("vehicleSavedMessage2")));
	
	return "openVehiclesStaff";
    }
    
}
