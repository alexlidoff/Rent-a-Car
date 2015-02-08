package model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import dao.GetIdAble;


@Entity
@Table(name = "staff")
@NamedQueries({
    @NamedQuery(name = "Staff.getAll", query = "SELECT st FROM Staff AS st"),
    @NamedQuery(name = "Staff.getByEmail",
    	query = "SELECT st FROM Staff AS st WHERE st.email = :email"),
    @NamedQuery(name = "Staff.getByUser",
    	query = "SELECT st FROM Staff AS st WHERE st.user = :user")
})
public class Staff implements GetIdAble<Integer>, Serializable {
    
    private static final long serialVersionUID = 8229843564355444765L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "firstName", nullable = false, length = 50)
    private String firstName;
    @Column(name = "lastName", nullable = false, length = 50)
    private String lastName;
    @Column(name = "position", nullable = true, length = 100)
    private String position;
    @Column(name = "phone", nullable = false, length = 100)
    private String phone;
    @Column(name = "email", nullable = false, length = 100)
    private String email;
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;
    
    
    //getters
    public Integer getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getPosition() {
        return position;
    }
    public String getPhone() {
        return phone;
    }
    public String getEmail() {
        return email;
    }
    public User getUser() {
	return user;
    }
    
    
    //setters
    public void setId(Integer id) {
        this.id = id;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setUser(User user) {
	this.user = user;
    }
    
    
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	return result;
    }
    
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Staff other = (Staff) obj;
	if (id == null) {
	    if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	    return false;
	return true;
    }
        
}
