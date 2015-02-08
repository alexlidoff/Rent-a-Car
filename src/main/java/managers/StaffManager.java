package managers;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import model.Staff;
import model.User;
import model.UserRole;

import org.apache.commons.codec.digest.DigestUtils;
import org.primefaces.model.SortOrder;

import dao.StaffDAO;


public class StaffManager implements Serializable {

    private static final long serialVersionUID = -3720923887774591871L;
    @Inject
    private StaffDAO staffDAO;


    public List<Staff> getAllStaff() {
        return staffDAO.getAll();
    }

    public List<Staff> getByConditions(int first, int pageSize, String sortField,
                                       SortOrder sortOrder, Map<String, String> filters) {
        return staffDAO.getByConditions(first, pageSize, sortField,
                sortOrder, filters);
    }

    public Staff getStaffByEmail(String email) {
        return staffDAO.getStaffByEmail(email);
    }

    public Staff getStaffByUser(User user) {
        return staffDAO.getStaffByUser(user);
    }

    public int count(String sortField, SortOrder sortOrder,
                     Map<String, String> filters) {
        return staffDAO.getByConditions(-1, -1, null, null, filters).size();
    }

    public void saveEmployee(Staff employee, String password,
                             List<UserRole> newUserRoles) {

        //FIRST STEP - USER
        //getting user
        User user = employee.getUser();
        //if no user found - creating new
        if (user == null) {
            user = new User();
        }
        //setting required user fields
        user.setLogin(employee.getEmail());
        if (password != null) {
            user.setPassword(DigestUtils.sha1Hex(password));
        }
        employee.setUser(user);

        //SECOND STEP - USER ROLES
        //setting user to roles collection (if employee is new - there is no user in userRole)
        for (UserRole userRole : newUserRoles) {
            userRole.setUser(user);
        }
        //setting this user roles collection to user
        user.setUserRoles(newUserRoles);


        //THIRD STEP - SAVING
        //saving new or updating existing employee
        //the user will be saved or updated automatically: (Staff maps User with cascade=CascadeType.ALL)
        //the user roles collection will be saved or updated automatically: (User maps UserRole with cascade=CascadeType.ALL)
        if (employee.getId() == null) {
            staffDAO.save(employee);
        } else {
            staffDAO.update(employee);
        }

    }

}
