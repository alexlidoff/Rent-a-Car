package managers;

import java.io.Serializable;

import javax.inject.Inject;

import model.User;
import dao.UserDAO;

public class UserManager implements Serializable {

    private static final long serialVersionUID = 6038892501392970157L;
    @Inject
    private UserDAO userDAO;


    public UserManager() {
    }


    public User getUser(String login, String password) {
        User user = userDAO.getUserByLogin(login);
        if (user == null) {
            return null;
        }
        if (password == null) {
            //this is internal call - user is logged through standard JAAS login form
            return user;
        }
        if (user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

}
