package beans;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import service.TransBeanCommunication;
import localization.LocalizationManager;
import managers.ApplicationManager;
import model.Application;


@Named(value = "paymentBean")
@ViewScoped
public class PaymentBean implements Serializable {

    private static final long serialVersionUID = 6999285611538364154L;
    @Inject
    private ApplicationManager applicationManager;
    private Application application;


    public PaymentBean() {
    }

    @PostConstruct
    private void init() {

        //getting application from flash
        application = (Application) TransBeanCommunication.getValue("applicationToPay");

        //no application - means that someone is calling payment page
        //directly, for example if this page was saved as a bookmark
        if (application == null) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().
                        redirect("/Rent-a-Car");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    //getters
    public Application getApplication() {
        return application;
    }


    //setters
    public void setApplication(Application application) {
        this.application = application;
    }


    //methods
    public String[] getMonths() {
        return new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    }

    public String[] getYears() {
        return new String[]{"2014", "2015", "2016", "2017", "2018", "2019", "2020"};
    }

    public String pay() {
        applicationManager.markAppicationPaid(application);

        //setting application in flash to null
        TransBeanCommunication.putValue("applicationToPay", null);

        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        context.addMessage(null, new FacesMessage(
                LocalizationManager.getLocalizedText("paymentDoneMessage1"),
                LocalizationManager.getLocalizedText("paymentDoneMessage2")));

        return "leavePayment";
    }

}
