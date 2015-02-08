package model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import dao.GetIdAble;


@Entity
@Table(name = "users")
@NamedQueries({
    @NamedQuery(name = "User.getAll", query = "SELECT u FROM User AS u"),
    @NamedQuery(name = "User.getByLogin",
    	query = "SELECT u FROM User AS u WHERE u.login = :login")
})
public class User implements Serializable, GetIdAble<Integer> {

    private static final long serialVersionUID = -1285002323981489634L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "login", nullable = false, length = 100)
    private String login;
    @Column(name = "password", nullable = false)
    private String password;
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="user", fetch=FetchType.EAGER)
    private List<UserRole> userRoles;
    
    
    public User() {
    }


    //getters
    public Integer getId() {
        return id;
    }
    public String getLogin() {
        return login;
    }
    public String getPassword() {
        return password;
    }
    public List<UserRole> getUserRoles() {
        return userRoles;
    }


    //setters
    public void setId(Integer id) {
        this.id = id;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
    
}
