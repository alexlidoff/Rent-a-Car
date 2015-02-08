package dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import model.User;
import model.UserRole;


@Stateless
public class UserRoleDAO extends GenericDAO<UserRole, Integer> {

    public UserRoleDAO() {
        super(UserRole.class);
    }

    public List<UserRole> getUserRoleByUser(User user) {
        TypedQuery<UserRole> query = em.createNamedQuery("UserRole.getByUser",
                UserRole.class);
        try {
            return query.setParameter("user", user).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

}
