package beans;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;

import localization.LocalizationManager;
import managers.ClientManager;
import managers.StaffManager;
import managers.UserManager;
import model.Client;
import model.Role;
import model.Staff;
import model.User;


@Named(value = "userBean")
@SessionScoped
public class UserBean implements Serializable {

    private static final long serialVersionUID = 5022669426687458041L;   
    private boolean userLogged;
    private User user;
    private String login;
    private String password;
    private Client userClient;
    private Staff userStaff;
    private String userName;
    private boolean showLoginFields;
    private boolean showAccessDeniedMessage;
    @Inject
    private UserManager userManager;
    @Inject
    private ClientManager clientManager;
    @Inject
    private StaffManager staffManager;
    
    
    public UserBean() {
	userLogged = false;
	showLoginFields = false;
	showAccessDeniedMessage = false;
    }
        
    
    //getters
    public boolean isUserLogged() {
        return userLogged;
    }
    public User getUser() {
	return user;
    }
    public String getLogin() {
	return login;
    }
    public String getPassword() {
	return password;
    }
    public Client getUserClient() {
        return userClient;
    }
    public Staff getUserStaff() {
        return userStaff;
    }
    public String getUserName() {
	return userName;
    }
    public boolean getShowLoginFields() {
        return showLoginFields;
    }
    public boolean getShowAccessDeniedMessage() {
        return showAccessDeniedMessage;
    }

    //setters
    public void setLogin(String login) {
	this.login = login;
    }
    public void setUser(User user) {
	this.user = user;
    }
    public void setPassword(String password) {
	this.password = password;
    }
    public void setUserClient(Client client) {
	this.userClient = client;
    }
    public void setUserStaff(Staff staff) {
	this.userStaff = staff;
    }

    
    //methods 
    public void loginLinkClicked() {
	showLoginFields = true;
    }
    
    public void cancelLogin() {
	showLoginFields = false;
	showAccessDeniedMessage = false;
    }
    
    public void logUser() {
	//preparing fields
	password = null;
	//to show login (email) exactly like this email has been first registered
	login = user.getLogin();
	userLogged = true;
	showLoginFields = false;
	showAccessDeniedMessage = false;
	userClient = clientManager.getClientByUser(user);
	userStaff = staffManager.getStaffByUser(user);
	if (userClient != null) {
	    userName = userClient.getFirstName() + " " + userClient.getLastName();
	} else if (userStaff != null) {
	    userName = userStaff.getFirstName() + " " + userStaff.getLastName();
	}
	
	//log user in standard JAAS security - if not logged
	if (getRequest().getUserPrincipal() == null) {
	    try {
		getRequest().login(user.getLogin(), user.getPassword());
	    } catch (ServletException e) {
		e.printStackTrace();
	    }
	}
		
	//after logging it's needed to completely reload the whole current page
	//TURNED OFF - decided to call navigation rule "userLogged" 
//	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//	try {
//	    ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
//	} catch (IOException e) {
//	    e.printStackTrace();
//	}
	
	FacesContext context = FacesContext.getCurrentInstance();
	NavigationHandler navigationHandler = context.getApplication().
		getNavigationHandler();
	navigationHandler.handleNavigation(context, null, "userLogged");
	
    }
    
    public void login() {
	//fields are not initialized
	if (login == null || password == null) {
	    return;
	}
	//fields are empty
	if (login.isEmpty() || password.isEmpty()) {
	    return;
	}
	
	//encrypting
	password = DigestUtils.sha1Hex(password);
	
	//trying to get user by login (email)
	user = userManager.getUser(login, password);
	
	//no user - access denied
	if (user == null) {
	    FacesContext context = FacesContext.getCurrentInstance();
	    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
		    LocalizationManager.getLocalizedText("accessDeniedMessage"),
		    LocalizationManager.getLocalizedText("IncorrectloginOrPassword")));
	    return;
	}
	
	//log user - prepare field, make other actions, etc.
	logUser();
	
    }
    
    public String logout() {
	FacesContext context = FacesContext.getCurrentInstance();
	context.getExternalContext().invalidateSession();
	
	if (getRequest().getUserPrincipal() != null) {
	    try {
		getRequest().logout();
	    } catch (ServletException e) {
		e.printStackTrace();
	    }
	}
	
	return "logout";
    }
    
    public HttpServletRequest getRequest() {
	Object request = FacesContext.getCurrentInstance().
		getExternalContext().getRequest();
	return (HttpServletRequest) request;
    }
    
    public boolean isUserClient() {
	if (!userLogged) {
	    return false;
	}
	if (userClient != null) {
	    return true;
	} else {
	    return false;
	}
    }
    
    public boolean isUserStaff() {
	if (!userLogged) {
	    return false;
	}
	if (userStaff != null) {
	    return true;
	} else {
	    return false;
	}
    }
    
    public boolean isUserAdmin() {
	return getRequest().isUserInRole(Role.admin.toString());
    }
    
}
