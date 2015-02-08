package beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import localization.LocalizationManager;
import managers.ApplicationManager;
import model.Application;
import model.ApplicationStatus;
import model.Client;
import model.Staff;
import model.Vehicle;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.SelectEvent;

import service.TransBeanCommunication;


@Named("appStaffBean")
@ViewScoped
public class ApplicationStaffBean implements Serializable {

    private static final long serialVersionUID = -2682089406233993860L;
    @Inject
    private ApplicationManager applicationManager;
    @Inject
    private Application application;
    @Inject
    private ClientsListStaffBean clientsListStaffBean;
    @Inject
    private VehiclesListBean vehiclesListBean;
    @Inject
    private StaffListBean staffListBean;
    @Inject
    private UserBean userBean;

    private Vehicle vehicle;
    private Client client;
    private String managerIndex;
    private Date startDate;
    private Date endDate;
    private int days;
    private BigDecimal amount;
    private boolean saveButtonDisabled;


    public ApplicationStaffBean() {
    }

    @PostConstruct
    private void init() {
        //getting application from flash
        Application applicationToOpen =
                (Application) TransBeanCommunication.getValue("applicationToOpen");
        if (applicationToOpen != null) {
            application = applicationToOpen;
            client = application.getClient();
            vehicle = application.getVehicle();
            startDate = application.getStartDate();
            endDate = application.getEndDate();
            calculateDays();
        }

        //setting clientsListStaffBean to 'choose' mode
        clientsListStaffBean.setChooseMode(true);
        //setting this bean into clientsListStaffBean
        clientsListStaffBean.setApplicationStaffBean(this);

        //setting vehiclesListBean to 'choose' mode
        vehiclesListBean.setChooseMode(true);
        //setting this bean into vehiclesListBean
        vehiclesListBean.setApplicationStaffBean(this);

        //setting staffListBean to 'choose' mode
        staffListBean.setChooseMode(true);
        //setting this bean into staffListBean
        staffListBean.setApplicationStaffBean(this);

        //disable save button for usual managers if application is closed
        saveButtonDisabled = (application.getApplicationStatus() ==
                ApplicationStatus.Closed) && !userBean.isUserAdmin();
    }


    //getters
    public Application getApplication() {
        return application;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Client getClient() {
        return client;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getDays() {
        return days;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getManagerIndex() {
        return managerIndex;
    }

    public boolean isSaveButtonDisabled() {
        return saveButtonDisabled;
    }


    //setters
    public void setApplication(Application application) {
        this.application = application;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setManagerIndex(String managerIndex) {
        this.managerIndex = managerIndex;
    }

    public void setSaveButtonDisabled(boolean saveButtonDisabled) {
        this.saveButtonDisabled = saveButtonDisabled;
    }


    //methods
    public String getClientProps() {
        if (client == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(LocalizationManager.getLocalizedText("clientProfileDrivingLicense"));
        sb.append(": ");
        sb.append(client.getDrivingLicense());
        sb.append("; ");
        sb.append(LocalizationManager.getLocalizedText("clientProfilePhone"));
        sb.append(": ");
        sb.append(client.getPhone());
        return sb.toString();
    }

    public String getVehicleProps() {
        if (vehicle == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(LocalizationManager.getLocalizedText("vehicleFormVIN"));
        sb.append(": ");
        sb.append(vehicle.getVIN());
        sb.append("; ");
        sb.append(LocalizationManager.getLocalizedText("vehicleEngine"));
        sb.append(": ");
        sb.append(vehicle.getEngine());
        return sb.toString();
    }

    public ApplicationStatus[] getApplicationStatuses() {
        return ApplicationStatus.values();
    }

    public boolean checkDates() {
        boolean err = false;
        String errorMessage = LocalizationManager.
                getLocalizedText("invalidPeriodMessage");

        //checking period
        if (startDate.after(endDate) || startDate.equals(endDate)) {
            err = true;
        }

        if (err) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    errorMessage, ""));
        }

        return !err;
    }

    private void calculateAmount() {
        if (vehicle == null) {
            amount = BigDecimal.ZERO;
            return;
        }
        amount = vehicle.getPrice().multiply(BigDecimal.valueOf(days));
    }

    private void calculateDays() {
        //dates are not specified
        if (startDate == null || endDate == null) {
            days = 0;
            amount = BigDecimal.ZERO;
            return;
        }

        //invalid period
        if (!checkDates()) {
            days = 0;
            amount = BigDecimal.ZERO;
            return;
        }

        //everything is correct
        Calendar cal1 = new GregorianCalendar();
        cal1.setTime(startDate);
        Calendar cal2 = new GregorianCalendar();
        cal2.setTime(endDate);
        long ms = cal2.getTimeInMillis() - cal1.getTimeInMillis();
        days = (int) (ms / 86400000);
        calculateAmount();
    }

    public void dateChanged(SelectEvent event) {
        calculateDays();
    }

    public void statusChanged() {
        //setting application manager only once, is status has been changed from New
        if (application.getApplicationStatus() != ApplicationStatus.New
                && application.getApplicationManager() == null) {
            application.setApplicationManager(userBean.getUserStaff());
        }
    }

    public void setManager(Staff employee) {
        switch (managerIndex) {
            case "1":
                application.setApplicationManager(employee);
                break;
            case "2":
                application.setReleasedByManager(employee);
                break;
            case "3":
                application.setAcceptedByManager(employee);
                break;
            default:
                application.setReleasedByManager(employee);
                break;
        }
    }

    public void releaseVehicle() {
        application.setVehicleReleased(true);
        application.setVehicleReleasingDate(new Date());
        application.setReleasedByManager(userBean.getUserStaff());
    }

    public void acceptVehicle() {
        application.setVehicleAccepted(true);
        application.setVehicleAcceptingDate(new Date());
        application.setAcceptedByManager(userBean.getUserStaff());
    }

    public String saveApplication() {

        if (!checkDates() || vehicle == null || client == null) {
            return null;
        }

        application.setStartDate(startDate);
        application.setEndDate(endDate);
        application.setAmount(amount);
        application.setClient(client);
        application.setVehicle(vehicle);
        applicationManager.saveApplication(application);

        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        context.addMessage(null, new FacesMessage(
                LocalizationManager.getLocalizedText("applicationSavedStaffMessage1", application.getId()),
                LocalizationManager.getLocalizedText("applicationSavedStaffMessage2")));

        return "openApplicationsStaff";
    }

}
