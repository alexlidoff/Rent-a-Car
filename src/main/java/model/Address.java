package model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class Address implements Serializable {

    private static final long serialVersionUID = -4784363021524422657L;
    @Column(name = "zipCode", nullable = true, length = 10)
    private String zipCode;
    @Column(name = "city", nullable = true, length = 50)
    private String city;
    @Column(name = "street", nullable = true, length = 100)
    private String street;
    @Column(name = "address", nullable = true, length = 100)
    private String address;


    //getters
    public String getZipCode() {
        return zipCode;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getAddress() {
        return address;
    }

    //setters
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
