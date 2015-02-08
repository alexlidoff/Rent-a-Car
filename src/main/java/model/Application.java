package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import dao.GetIdAble;


@Entity
@Table(name = "applications")
@NamedQueries({
        @NamedQuery(name = "Application.getAll",
                query = "SELECT apl FROM Application AS apl"),
        @NamedQuery(name = "Application.getByClient",
                query = "SELECT apl FROM Application AS apl WHERE apl.client = :client ORDER BY apl.id DESC")
})
public class Application implements Serializable, GetIdAble<Integer> {

    private static final long serialVersionUID = -9048867884771776461L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    @Column(name = "startDate", nullable = false)
    @Temporal(value = TemporalType.DATE)
    private Date startDate;
    @Column(name = "endDate", nullable = true)
    @Temporal(value = TemporalType.DATE)
    private Date endDate;
    @Column(name = "amount", nullable = true, columnDefinition = "DECIMAL")
    private BigDecimal amount;
    @Column(name = "applicationStatus", nullable = false, columnDefinition = "VARCHAR")
    @Enumerated(EnumType.STRING)
    private ApplicationStatus applicationStatus;
    @ManyToOne
    @JoinColumn(name = "applicationManager_id")
    private Staff applicationManager;
    @Column(name = "note", nullable = true, columnDefinition = "TEXT")
    private String note;
    @Column(name = "vehicleReleased", nullable = false, columnDefinition = "BIT")
    private boolean vehicleReleased;
    @Column(name = "vehicleReleasingDate", nullable = true)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date vehicleReleasingDate;
    @ManyToOne
    @JoinColumn(name = "releasedByManager_id")
    private Staff releasedByManager;
    @Column(name = "vehicleAccepted", nullable = false, columnDefinition = "BIT")
    private boolean vehicleAccepted;
    @Column(name = "vehicleAcceptingDate", nullable = true)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date vehicleAcceptingDate;
    @ManyToOne
    @JoinColumn(name = "acceptedByManager_id")
    private Staff acceptedByManager;
    @Column(name = "rentPaid", columnDefinition = "BIT")
    private boolean rentPaid;
    @Column(name = "extraPayment", nullable = true, columnDefinition = "DECIMAL")
    private BigDecimal extraPayment;
    @Column(name = "extraPaymentNote", nullable = true, columnDefinition = "TEXT")
    private String extraPaymentNote;
    @Column(name = "extraPaid", columnDefinition = "BIT")
    private boolean extraPaid;


    //getters
    public Integer getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public Staff getApplicationManager() {
        return applicationManager;
    }

    public String getNote() {
        return note;
    }

    public boolean isVehicleReleased() {
        return vehicleReleased;
    }

    public Date getVehicleReleasingDate() {
        return vehicleReleasingDate;
    }

    public Staff getReleasedByManager() {
        return releasedByManager;
    }

    public boolean isVehicleAccepted() {
        return vehicleAccepted;
    }

    public Date getVehicleAcceptingDate() {
        return vehicleAcceptingDate;
    }

    public Staff getAcceptedByManager() {
        return acceptedByManager;
    }

    public boolean isRentPaid() {
        return rentPaid;
    }

    public BigDecimal getExtraPayment() {
        return extraPayment;
    }

    public String getExtraPaymentNote() {
        return extraPaymentNote;
    }

    public boolean isExtraPaid() {
        return extraPaid;
    }

    //setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public void setApplicationManager(Staff applicationManager) {
        this.applicationManager = applicationManager;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setVehicleReleased(boolean vehicleReleased) {
        this.vehicleReleased = vehicleReleased;
    }

    public void setVehicleReleasingDate(Date vehicleReleasingDate) {
        this.vehicleReleasingDate = vehicleReleasingDate;
    }

    public void setReleasedByManager(Staff releasedByManager) {
        this.releasedByManager = releasedByManager;
    }

    public void setVehicleAccepted(boolean vehicleAccepted) {
        this.vehicleAccepted = vehicleAccepted;
    }

    public void setVehicleAcceptingDate(Date vehicleAcceptingDate) {
        this.vehicleAcceptingDate = vehicleAcceptingDate;
    }

    public void setAcceptedByManager(Staff acceptedByManager) {
        this.acceptedByManager = acceptedByManager;
    }

    public void setRentPaid(boolean rentPaid) {
        this.rentPaid = rentPaid;
    }

    public void setExtraPayment(BigDecimal extraPayment) {
        this.extraPayment = extraPayment;
    }

    public void setExtraPaymentNote(String extraPaymentNote) {
        this.extraPaymentNote = extraPaymentNote;
    }

    public void setExtraPaid(boolean extraPaid) {
        this.extraPaid = extraPaid;
    }

}
