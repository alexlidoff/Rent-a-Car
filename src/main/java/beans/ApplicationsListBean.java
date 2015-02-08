package beans;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import service.TransBeanCommunication;
import localization.LocalizationManager;
import managers.ApplicationManager;
import model.Application;
import model.ApplicationStatus;
import model.Client;


@Named(value = "applicationsListBean")
@RequestScoped
public class ApplicationsListBean {
    
    @Inject
    private ApplicationManager applicationManager;
    @Inject
    private UserBean userBean;
    private Client client;
    
    
    public ApplicationsListBean() {
    }
    
    @PostConstruct
    private void init() {	
	client = userBean.getUserClient();
    }
    
        
    //methods
    public List<Application> getClientApplications() {
	return applicationManager.getClientApplications(client);
    }
    
    public String makePayment(Application application) {
	//setting chosen application to flash to get it in the PaymentBean
	TransBeanCommunication.putValue("applicationToPay", application);
	return "openPaymentSystem";
    }

    public String getNotPaidText(Application application) {
	if (application.isRentPaid() 
		|| application.getApplicationStatus() == ApplicationStatus.Canceled
		|| application.getApplicationStatus() == ApplicationStatus.Declined) {
	    return ""; 
	}
	return LocalizationManager.getLocalizedText("notPaidSign");
    }
    
}
