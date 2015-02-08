package managers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import model.Client;
import model.Role;
import model.User;
import model.UserRole;

import org.apache.commons.codec.digest.DigestUtils;
import org.primefaces.model.SortOrder;

import dao.ClientDAO;


public class ClientManager implements Serializable {

    private static final long serialVersionUID = -3655650651307826080L;
    @Inject
    private ClientDAO clientDAO;


    public List<Client> getAllClients() {
        return clientDAO.getAll();
    }

    public Client getClientByITIN(String ITIN) {
        return clientDAO.getClientByITIN(ITIN);
    }

    public Client getClientByUser(User user) {
        return clientDAO.getClientByUser(user);
    }

    public List<Client> getByConditions(int first, int pageSize, String sortField,
                                        SortOrder sortOrder, Map<String, String> filters) {
        return clientDAO.getByConditions(first, pageSize, sortField,
                sortOrder, filters);
    }

    public int count(String sortField, SortOrder sortOrder,
                     Map<String, String> filters) {
        return clientDAO.getByConditions(-1, -1, null, null, filters).size();
    }

    public void saveClient(Client client, String password) {

        //FIRST STEP - USER
        //getting user
        User user = client.getUser();
        //if no user found - creating new
        if (user == null) {
            user = new User();
        }
        //setting required user fields
        user.setLogin(client.getEmail());
        if (password != null) {
            user.setPassword(DigestUtils.sha1Hex(password));
        }
        //setting user to client
        client.setUser(user);


        //SECOND STEP - USER ROLE
        //getting user roles collection
        List<UserRole> userRoles = user.getUserRoles();
        //if no user roles found - creating new collection
        if (userRoles == null) {
            userRoles = new ArrayList<UserRole>();
        }
        //if user roles collection is empty - creating new role and add
        if (userRoles.size() == 0) {
            UserRole clientRole = new UserRole();
            clientRole.setRole(Role.client);
            clientRole.setUser(user);
            userRoles.add(clientRole);
        }
        //setting this user roles collection to user
        user.setUserRoles(userRoles);


        //THIRD STEP - SAVING
        //saving new or updating existing client
        //the user will be saved or updated automatically: (Client maps User with cascade=CascadeType.ALL)
        //the user roles collection will be saved or updated automatically: (User maps UserRole with cascade=CascadeType.ALL)
        if (client.getId() == null) {
            clientDAO.save(client);
        } else {
            clientDAO.update(client);
        }

    }


}
