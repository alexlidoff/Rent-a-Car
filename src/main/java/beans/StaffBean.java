package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import localization.LocalizationManager;
import managers.StaffManager;
import model.Role;
import model.Staff;
import model.UserRole;

import org.omnifaces.cdi.ViewScoped;

import service.TransBeanCommunication;


@Named("staffBean")
@ViewScoped
public class StaffBean implements Serializable {

    private static final long serialVersionUID = 7526517143672766385L;
    @Inject
    private Staff employee;
    @Inject
    private StaffManager staffManager;
    
    private String password1;
    private String password2;
    private boolean mustEnterPasswords;
    private List<UserRole> employeeRoles;
    private boolean addNewRole;
    private Role newRole;
    
    
    public StaffBean() {
	mustEnterPasswords = true;
    }
    
    @PostConstruct
    private void init() {
	//getting employee from flash
	Staff employeeToOpen = 
		(Staff) TransBeanCommunication.getValue("employeeToOpen");
	if (employeeToOpen != null) {
	    employee = employeeToOpen;
	    mustEnterPasswords = false;
	    //employeeRoles links to employee.getUser().getUserRoles()
	    employeeRoles = employee.getUser().getUserRoles();
	} else {
	    employeeRoles = new ArrayList<>();
	}
	
    }
    
    //getters
    public Staff getEmployee() {
	return employee;
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
    public List<UserRole> getEmployeeRoles() {
	return employeeRoles;
    }
    public boolean isAddNewRole() {
	return addNewRole;
    }
    public Role getNewRole() {
	return newRole;
    }


    //setters
    public void setEmployee(Staff employee) {
	this.employee = employee;
    }
    public void setPassword1(String password1) {
   	this.password1 = password1;
    }
    public void setPassword2(String password2) {
   	this.password2 = password2;
    }
    public void setEmployeeRoles(List<UserRole> employeeRoles) {
	this.employeeRoles = employeeRoles;
    }
    public void setNewRole(Role newRole) {
	this.newRole = newRole;
    }
    public void setAddNewRole(boolean addNewRole) {
	this.addNewRole = addNewRole;
    }
    
    
    //methods
    public String getPasswordPanelHeader() {
	if (mustEnterPasswords) {
	    return LocalizationManager.getLocalizedText("staffFormPasswordPanelHeader1");
	} else {
	    return LocalizationManager.getLocalizedText("staffFormPasswordPanelHeader2");
	}
    }
    
    public void showDoubleRoleMessage(UserRole userRole) {
	FacesContext context = FacesContext.getCurrentInstance();
	context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
		LocalizationManager.getLocalizedText("staffFormRolesListRoleDouble",
			userRole.getRole()),""));
    }
    
    public Role[] getAllRoles() {
	return Role.values();
    }
    
    public void onEditEnd() {
	Iterator<UserRole> iter = employeeRoles.iterator();
	while (iter.hasNext()) {
	    UserRole testRole = iter.next();
	    int i = 0;
	    for (UserRole ur : employeeRoles) {
		if (ur.getRole() == testRole.getRole()) {
		    i++;
		}
	    }	
	    if (i > 1) {
		iter.remove();
		showDoubleRoleMessage(testRole);
	    }
	}
    }
    
    public void saveAddedNewRole() {
	boolean err = false;
	for (UserRole ur : employeeRoles) {
	    if (ur.getRole() == newRole) {
		showDoubleRoleMessage(ur);
		err = true;
		break;
	    }
	}
	if (!err) {
	    UserRole newUserRole = new UserRole();
	    newUserRole.setRole(newRole);
	    employeeRoles.add(newUserRole);
	}
	newRole = null;
	addNewRole = false;
    }
    
    public void deleteEmployeeRole(UserRole employeeRole) {
	employeeRoles.remove(employeeRole);
    }
    
    public void validateEmail(FacesContext context, UIComponent component, 
	    Object value) {
	
	String email = ((String) value).toLowerCase();
	String messageText = null;
	boolean valueNotValid = false;
	
	//length
	if (email.length() < 5 || email.length() > 100) {
	    valueNotValid = true;
	    messageText = LocalizationManager.
		    getLocalizedText("emailNotCorrectMessage");
	}
	
	//regex
	if (!email.matches("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)+$")) {
	    valueNotValid = true;
	    messageText = LocalizationManager.
		    getLocalizedText("emailNotCorrectMessage");
	}
	
	//we should check email for not being in use
	Staff employeeWithSameEmail = staffManager.getStaffByEmail(email);
	if (employeeWithSameEmail != null && !employeeWithSameEmail.equals(employee)) {
		valueNotValid = true;
		messageText = LocalizationManager.
			getLocalizedText("emailIsInUseMessage", email);
	}
	
	if (valueNotValid) {
	    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
		    messageText, messageText);
	    throw new ValidatorException(message);
	}

    }
    
    public boolean checkPasswordsEquality() {
	if (password1.equals(password2)) {
	    return true;
	} else {
	    FacesContext context = FacesContext.getCurrentInstance();
	    context.addMessage("staffForm:passwordsNotEqual", new FacesMessage(FacesMessage.SEVERITY_WARN,
		LocalizationManager.getLocalizedText("passwordsNotEqualMessage"), ""));
	    return false;
	}	
    }
    
    public String saveEmployee() {	
	if (!checkPasswordsEquality()) {
	    return null;
	}
	
	//all is correct, saving employee
	
	//password handling
	String newPassword;
	if (mustEnterPasswords) {
	    //if must enter password - password field is always filled 
	    newPassword = password1;
	} else {
	    //lets check if user re-entered employee password
	    if (password1.isEmpty()) {
		//didn't re-enter, password will not be changed
		newPassword = null; 
	    } else {
		//re-entered, passing new password
		newPassword = password1;
	    }
	}
	
	staffManager.saveEmployee(employee, newPassword, employeeRoles);
	
	//profile saved message
	FacesContext context = FacesContext.getCurrentInstance(); 
	context.getExternalContext().getFlash().setKeepMessages(true);
        context.addMessage(null, new FacesMessage(
        	LocalizationManager.getLocalizedText("staffSavedMessage1",
        		employee.getFirstName(), employee.getLastName()),
        	LocalizationManager.getLocalizedText("staffSavedMessage2")));
        
        return "openStaff";
    }
     
}
