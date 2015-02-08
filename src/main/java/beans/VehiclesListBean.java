package beans;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import localization.LocalizationManager;
import managers.VehicleManager;
import model.Vehicle;
import model.VehicleClass;
import model.VehicleType;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import service.TransBeanCommunication;
import dao.VehicleTypeDAO;


@Named("vehiclesListBean")
@ViewScoped
public class VehiclesListBean implements Serializable {

    private static final long serialVersionUID = 6287070138949004792L;
    @Inject
    private VehicleManager vehicleManager;
    @Inject
    private VehicleTypeDAO vehicleTypeDAO;
    private LazyDataModel<Vehicle> vehicleDataModel;
    private boolean chooseMode;
    private boolean clientMode;
    private ApplicationStaffBean applicationStaffBean;
    private Vehicle vehicleToViewSchedule;
    private Date startDate;
    private Date endDate;


    public VehiclesListBean() {
        setClientMode(false);
    }

    @PostConstruct
    private void init() {

        vehicleDataModel = new LazyDataModel<Vehicle>() {

            private static final long serialVersionUID = 5130942592729706930L;

            @Override
            public List<Vehicle> load(int first, int pageSize,
                                      String sortField, SortOrder sortOrder,
                                      Map<String, String> filters) {

                vehicleDataModel.setRowCount(vehicleManager.count(sortField,
                        sortOrder, filters, startDate, endDate, clientMode));
                return vehicleManager.getByConditions(first, pageSize,
                        sortField, sortOrder, filters, startDate, endDate, clientMode);
            }


            @Override
            public Vehicle getRowData(String rowKey) {
                @SuppressWarnings("unchecked")
                List<Vehicle> vehicles = (List<Vehicle>) getWrappedData();

                for (Vehicle vehicle : vehicles) {
                    if (vehicle.getId().equals(Integer.valueOf(rowKey)))
                        return vehicle;
                }

                return null;
            }


            @Override
            public String getRowKey(Vehicle vehicle) {
                return String.valueOf(vehicle.getId());
            }

        };

        //setting 'disabled' filter = FALSE - to show only not disabled vehicles by default
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().
                getViewRoot().findComponent("vehiclesListForm:vehiclesTable");
        if (dataTable != null) {
            Map<String, String> filters = new HashMap<>();
            filters.put("disabled", "false");
            dataTable.setFilters(filters);
        }

    }


    //getters
    public LazyDataModel<Vehicle> getVehicleDataModel() {
        return vehicleDataModel;
    }

    public boolean isChooseMode() {
        return chooseMode;
    }

    public ApplicationStaffBean getApplicationStaffBean() {
        return applicationStaffBean;
    }

    public boolean isClientMode() {
        return clientMode;
    }

    public Vehicle getVehicleToViewSchedule() {
        return vehicleToViewSchedule;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }


    //setters
    public void setVehicleDataModel(LazyDataModel<Vehicle> vehicleDataModel) {
        this.vehicleDataModel = vehicleDataModel;
    }

    public void setChooseMode(boolean chooseMode) {
        this.chooseMode = chooseMode;
    }

    public void setApplicationStaffBean(ApplicationStaffBean applicationStaffBean) {
        this.applicationStaffBean = applicationStaffBean;
    }

    public void setClientMode(boolean clientMode) {
        this.clientMode = clientMode;
    }

    public void setVehicleToViewSchedule(Vehicle vehicleToViewSchedule) {
        this.vehicleToViewSchedule = vehicleToViewSchedule;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    //methods
    public SelectItem[] getVehicleClasses() {
        VehicleClass[] vehicleClasses = VehicleClass.values();
        SelectItem[] classes = new SelectItem[vehicleClasses.length + 1];
        classes[0] = new SelectItem("", "-");
        for (int i = 0; i < vehicleClasses.length; i++) {
            classes[i + 1] = new SelectItem(vehicleClasses[i],
                    vehicleClasses[i].toString());
        }
        return classes;
    }

    public SelectItem[] getBooleanOptions() {
        SelectItem[] options = new SelectItem[3];
        options[0] = new SelectItem("", "-");
        options[1] = new SelectItem(Boolean.TRUE.toString(),
                LocalizationManager.getLocalizedText("booleanTrue"));
        options[2] = new SelectItem(Boolean.FALSE.toString(),
                LocalizationManager.getLocalizedText("booleanFalse"));
        return options;
    }

    public SelectItem[] getVehicleTypes() {
        List<VehicleType> vehicleTypes = vehicleTypeDAO.getAll();
        SelectItem[] types = new SelectItem[vehicleTypes.size() + 1];
        types[0] = new SelectItem("", "-");
        for (int i = 0; i < vehicleTypes.size(); i++) {
            types[i + 1] = new SelectItem(vehicleTypes.get(i).getId(),
                    vehicleTypes.get(i).getName());
        }
        return types;
    }

    public SelectItem[] getGearboxes() {
        SelectItem[] options = new SelectItem[3];
        options[0] = new SelectItem("", "-");
        options[1] = new SelectItem(1,
                LocalizationManager.getLocalizedText("gearboxManual"));
        options[2] = new SelectItem(2,
                LocalizationManager.getLocalizedText("gearboxAutomatic"));
        return options;
    }

    public SelectItem[] getDrives() {
        SelectItem[] options = new SelectItem[4];
        options[0] = new SelectItem("", "-");
        options[1] = new SelectItem(1,
                LocalizationManager.getLocalizedText("frontWheelDrive"));
        options[2] = new SelectItem(2,
                LocalizationManager.getLocalizedText("rearWheelDrive"));
        options[3] = new SelectItem(3,
                LocalizationManager.getLocalizedText("fullWheelDrive"));
        return options;
    }

    public String openVehicle(Vehicle vehicle) {
        //setting chosen vehicle to flash to get it in VehicleBean
        TransBeanCommunication.putValue("vehicleToOpen", vehicle);
        return "openVehicleForm";
    }

    public void chooseVehicle(Vehicle vehicle) {
        applicationStaffBean.setVehicle(vehicle);
    }

    public void onRowDoubleClick(SelectEvent event) {
        Vehicle vehicle = (Vehicle) event.getObject();
        if (vehicle == null) {
            return;
        }
        if (chooseMode) {
            //choose mode - setting vehicle into applicationStaffBean and closing choose dialog
            chooseVehicle(vehicle);
            RequestContext.getCurrentInstance().execute("PF('vehicleChooseDialog').hide();");
        } else {
            //usual mode
            //setting chosen vehicle to flash to get it in VehicleBean
            TransBeanCommunication.putValue("vehicleToOpen", vehicle);
            //than opening vehicle form with help of navigation rule
            FacesContext context = FacesContext.getCurrentInstance();
            NavigationHandler navigationHandler = context.getApplication().
                    getNavigationHandler();
            navigationHandler.handleNavigation(context, null, "openVehicleForm");
        }
    }

    public void vehicleScheduleClicked(Vehicle vehicle) {
        vehicleToViewSchedule = vehicle;
    }

}
