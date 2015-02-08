package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import dao.GetIdAble;


@Entity
@Table(name = "roles")
@NamedQueries({
    @NamedQuery(name = "UserRole.getByUser",
    	query = "SELECT ur FROM UserRole AS ur WHERE ur.user = :user")
})
public class UserRole implements GetIdAble<Integer> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "role", nullable = false, columnDefinition = "VARCHAR")
    @Enumerated(EnumType.STRING)
    private Role role;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    
    public UserRole() {
    }


    //getters
    public Integer getId() {
        return id;
    }
    public Role getRole() {
        return role;
    }
    public User getUser() {
        return user;
    }


    //setters
    public void setId(Integer id) {
        this.id = id;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public void setUser(User user) {
        this.user = user;
    }
    
}
