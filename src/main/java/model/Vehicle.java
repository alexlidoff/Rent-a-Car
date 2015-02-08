package model;

import java.io.Serializable;
import java.math.BigDecimal;

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
@Table(name = "vehicles")
@NamedQueries({
    @NamedQuery(name = "Vehicle.getAll",
	query = "SELECT veh FROM Vehicle AS veh"),
    @NamedQuery(name = "Vehicle.getAllNotDisabled",
    	query = "SELECT veh FROM Vehicle AS veh WHERE veh.disabled = false")
})
public class Vehicle implements Serializable, GetIdAble<Integer> {
    
    private static final long serialVersionUID = 3680293448882398045L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @Column(name = "VIN", nullable = false, length = 20)
    private String VIN;
    @Column(name = "vehicleClass", nullable = false, columnDefinition = "VARCHAR")
    @Enumerated(EnumType.STRING)
    private VehicleClass vehicleClass;
    @ManyToOne
    @JoinColumn(name = "vehicleType_id")
    private VehicleType vehicleType;
    @Column(name = "year", nullable = false, length = 4)
    private short year;
    @Column(name = "gearbox", nullable = false, columnDefinition = "BIT")
    private short gearbox;
    @Column(name = "engine", nullable = false, length = 50)
    private String engine;
    @Column(name = "drive", nullable = false, columnDefinition = "BIT")
    private short drive;
    @Column(name = "ac", columnDefinition = "BIT", length = 1)
    private boolean ac;
    @Column(name = "cruiseControl", columnDefinition = "BIT", length = 1)
    private boolean cruiseControl;
    @Column(name = "cabriolet", columnDefinition = "BIT", length = 1)
    private boolean cabriolet;
    @Column(name = "price", nullable = false, columnDefinition = "DECIMAL")
    private BigDecimal price;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "photo", columnDefinition = "MEDIUMBLOB")
    private byte[] photo;
    @Column(name = "disabled", columnDefinition = "BIT", length = 1)
    private boolean disabled;
    
    
    //getters
    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getVIN() {
        return VIN;
    }
    public VehicleClass getVehicleClass() {
        return vehicleClass;
    }
    public VehicleType getVehicleType() {
        return vehicleType;
    }
    public short getYear() {
        return year;
    }
    public short getGearbox() {
        return gearbox;
    }
    public String getEngine() {
        return engine;
    }
    public short getDrive() {
        return drive;
    }
    public boolean isAc() {
        return ac;
    }
    public boolean isCruiseControl() {
        return cruiseControl;
    }
    public boolean isCabriolet() {
        return cabriolet;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public String getDescription() {
        return description;
    }
    public byte[] getPhoto() {
        return photo;
    }
    public boolean isDisabled() {
        return disabled;
    }
    
    //setters
    public void setId(Integer id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setVIN(String VIN) {
        this.VIN = VIN;
    }
    public void setVehicleClass(VehicleClass vehicleClass) {
        this.vehicleClass = vehicleClass;
    }
    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
    public void setYear(short year) {
        this.year = year;
    }
    public void setGearbox(short gearbox) {
        this.gearbox = gearbox;
    }
    public void setEngine(String engine) {
        this.engine = engine;
    }
    public void setDrive(short drive) {
        this.drive = drive;
    }
    public void setAc(boolean ac) {
        this.ac = ac;
    }
    public void setCruiseControl(boolean cruiseControl) {
        this.cruiseControl = cruiseControl;
    }
    public void setCabriolet(boolean cabriolet) {
        this.cabriolet = cabriolet;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    
}
