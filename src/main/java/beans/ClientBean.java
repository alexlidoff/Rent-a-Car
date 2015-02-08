package beans;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import localization.LocalizationManager;
import managers.ClientManager;
import model.Address;
import model.Client;

import org.omnifaces.cdi.ViewScoped;

import service.TransBeanCommunication;


@Named(value = "clientBean")
@ViewScoped
public class ClientBean implements Serializable {

    private static final long serialVersionUID = 9075364547859047933L;
    @Inject
    private ClientManager clientManager;
    @Inject
    private Client client;
    @Inject
    private Address address;
    @Inject
    private UserBean userBean;

    private String password1;
    private String password2;
    private boolean mustEnterPasswords;


    public ClientBean() {
        mustEnterPasswords = true;
    }

    @PostConstruct
    private void init() {
        if (userBean.isUserLogged()) {
            if (userBean.isUserClient()) {
                //the client wants to edit his profile
                client = userBean.getUserClient();
                mustEnterPasswords = false;
            } else if (userBean.isUserStaff()) {
                //staff wants to edit or add new client profile
                //getting client from flash
                Client clientToOpen =
                        (Client) TransBeanCommunication.getValue("clientToOpen");
                if (clientToOpen != null) {
                    client = clientToOpen;
                    mustEnterPasswords = false;
                }
            }
            if (client.getAddress() != null) {
                address = client.getAddress();
            }
        } else {
            mustEnterPasswords = true;
        }
    }


    //getters
    public Client getClient() {
        return client;
    }

    public Address getAddress() {
        return address;
    }

    public String getPassword1() {
        return password1;
    }

    public String getPassword2() {
        return password2;
    }

    public boolean getMustEnterPasswords() {
        return mustEnterPasswords;
    }


    //setters
    public void setClient(Client client) {
        this.client = client;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }


    //methods
    public String getPasswordPanelHeader() {
        if (mustEnterPasswords) {
            return LocalizationManager.getLocalizedText("clientProfilePasswordPanelHeader1");
        } else {
            return LocalizationManager.getLocalizedText("clientProfilePasswordPanelHeader2");
        }
    }

    public void validateITIN(FacesContext context, UIComponent component,
                             Object value) {

        String ITIN = ((String) value).toLowerCase();
        String messageText = null;
        boolean valueNotValid = false;

        //length
        if (ITIN.length() < 5 || ITIN.length() > 20) {
            valueNotValid = true;
            messageText = LocalizationManager.
                    getLocalizedText("ITINNotCorrectMessage");
        }

        //we should check ITIN for not being in use
        Client clientWithSameITIN = clientManager.getClientByITIN(ITIN);
        if (userBean.isUserLogged()) {
            //user is logged
            if (clientWithSameITIN != null && !clientWithSameITIN.equals(client)) {
                valueNotValid = true;
                messageText = LocalizationManager.
                        getLocalizedText("ITINIsInUseMessage", ITIN);
            }
        } else {
            //user not logged
            if (clientWithSameITIN != null) {
                valueNotValid = true;
                messageText = LocalizationManager.
                        getLocalizedText("ITINIsInUseMessage", ITIN);
            }
        }

        if (valueNotValid) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    messageText, messageText);
            throw new ValidatorException(message);
        }

    }

    public String closeClientProfileForm() {
        if (userBean.isUserStaff()) {
            return "openClientsStaff";
        } else {
            return "closeClientProfileForm";
        }
    }

    public boolean checkPasswordsEquality() {
        if (password1.equals(password2)) {
            return true;
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("clientForm:passwordsNotEqual", new FacesMessage(FacesMessage.SEVERITY_WARN,
                    LocalizationManager.getLocalizedText("passwordsNotEqualMessage"), ""));
            return false;
        }
    }

    public void saveClient() {
        if (!checkPasswordsEquality()) {
            return;
        }

        //all is correct, saving client

        //address is the embedded object
        client.setAddress(address);

        //password handling
        String newPassword;
        if (mustEnterPasswords) {
            //if must enter password - password field is always filled
            newPassword = password1;
        } else {
            //lets check if user re-entered his password
            if (password1.isEmpty()) {
                //didn't re-enter, password will not be changed
                newPassword = null;
            } else {
                //re-entered, passing new password
                newPassword = password1;
            }
        }

        clientManager.saveClient(client, newPassword);

        //profile saved message
        FacesContext context = FacesContext.getCurrentInstance();
        if (!userBean.isUserLogged() || userBean.isUserStaff()) {
            //if user not logger or user is staff we should keep message, because user will be redirected
            context.getExternalContext().getFlash().setKeepMessages(true);
        }
        context.addMessage(null, new FacesMessage(
                LocalizationManager.getLocalizedText("profileSavedMessage"),
                ""));

        //auto log in this client if not logged
        if (!userBean.isUserLogged()) {
            userBean.setUser(client.getUser());
            userBean.logUser();
        } else {
            if (userBean.isUserStaff()) {
                //staff saved client profile - redirect him/her to clients list
                NavigationHandler navigationHandler = context.getApplication().
                        getNavigationHandler();
                navigationHandler.handleNavigation(context, null, "openClientsStaff");
            }
        }
    }

}
