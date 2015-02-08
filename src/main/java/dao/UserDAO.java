package dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import model.User;


@Stateless
public class UserDAO extends GenericDAO<User, Integer> {

    public UserDAO() {
        super(User.class);
    }

    public List<User> getAll() {
        TypedQuery<User> query = em.createNamedQuery("User.getAll",
                User.class);
        return query.getResultList();
    }

    public User getUserByLogin(String login) {
        TypedQuery<User> query = em.createNamedQuery("User.getByLogin",
                User.class);
        try {
            return query.setParameter("login", login).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
