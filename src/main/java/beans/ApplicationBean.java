package beans;

import java.io.IOException;
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

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.SelectEvent;

import service.TransBeanCommunication;
import localization.LocalizationManager;
import managers.ApplicationManager;
import model.Application;
import model.Vehicle;


@Named(value = "applicationBean")
@ViewScoped
public class ApplicationBean implements Serializable {
    
    private static final long serialVersionUID = 5346196381674601884L;
    @Inject
    private Application application;
    @Inject
    private ApplicationManager applicationManager;
    @Inject
    private UserBean userBean;
   
    private Vehicle vehicle;
    private Date startDate;
    private Date endDate;
    private int days;
    private BigDecimal amount;
    private boolean userAgreement;
    
        
    public ApplicationBean() {
    }
        
    @PostConstruct
    private void init() {
	
	//getting vehicle form flash
	vehicle = (Vehicle) TransBeanCommunication.getValue("chosenVehicle");
	//setting vehicle in flash to null
	TransBeanCommunication.putValue("chosenVehicle", null);
	
	//if no vehicle or user not logged - it means someone tries to open the page manually
	//may be the page was saved as a bookmark. Redirecting to the home page
	if (vehicle == null || !userBean.isUserLogged()) {
	    try {
		FacesContext.getCurrentInstance().getExternalContext().
			redirect("/Rent-a-Car");
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	
    }
    
    
    //getters
    public Vehicle getVehicle() {
	return vehicle;
    }
    public Application getApplication() {
	return application;
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
    public boolean isUserAgreement() {
	return userAgreement;
    }


    //setters
    public void setApplication(Application application) {
	this.application = application;
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
    public void setUserAgreement(boolean userAgreement) {
	this.userAgreement = userAgreement;
    }
    
    
    //methods
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
    
    public String getMinDate() {
	Calendar cal = new GregorianCalendar();
	return cal.get(Calendar.DAY_OF_MONTH) + "." + 
		(cal.get(Calendar.MONTH)+1) + "." + cal.get(Calendar.YEAR);
    }
    
    public boolean checkDates() {
	boolean err = false;
	String errorMessage = LocalizationManager.
		getLocalizedText("invalidPeriodMessage");
	
	//checking period
	Calendar cal = new GregorianCalendar();
	cal.set(Calendar.HOUR_OF_DAY, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);
	cal.set(Calendar.MILLISECOND, 0);
	Date today = new Date(cal.getTimeInMillis());
	if (startDate.before(today)) {
	    err = true;
	}
	if (startDate.after(endDate) || startDate.equals(endDate)) {
	    err = true;
	}
	
	//checking if there are any applications in this period with this vehicle
	if (!err && applicationManager.
		existApplicationsInPeriod(vehicle, startDate, endDate)) {
	    err = true;
	    errorMessage = LocalizationManager.
		    getLocalizedText("vehicleNotAvailableMessage", vehicle.getName());
	}
	
	if (err) {
	    FacesContext context = FacesContext.getCurrentInstance();
	    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
		errorMessage, ""));
	}
	
	return !err;
    }
    
    public String saveApplication() {
	if (!checkDates()) {
	    return null;
	}
	calculateDays();
	application.setStartDate(startDate);
	application.setEndDate(endDate);
	application.setAmount(amount);
	application.setClient(userBean.getUserClient());
	application.setVehicle(vehicle);
	applicationManager.saveApplication(application);
	
	FacesContext context = FacesContext.getCurrentInstance();
	context.getExternalContext().getFlash().setKeepMessages(true);
        context.addMessage(null, new FacesMessage(
        	LocalizationManager.getLocalizedText("applicationSavedMessage1"),
        	LocalizationManager.getLocalizedText("applicationSavedMessage2")));
	
	return "openApplicationsListClient";
    }
        
}
