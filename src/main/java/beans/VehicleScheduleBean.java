package beans;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import model.Application;
import model.Vehicle;

import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

import dao.ApplicationDAO;


@Named("vehSchBean")
@RequestScoped
public class VehicleScheduleBean implements Serializable {

    private static final long serialVersionUID = -8540365646126145278L;
    @Inject
    private ApplicationDAO applicationDAO;
    @Inject
    private VehiclesListBean vehiclesListBean;
    private ScheduleModel scheduleModel;


    public VehicleScheduleBean() {
    }

    @PostConstruct
    public void init() {
        fillSchedule();
    }


    //getters
    public ScheduleModel getScheduleModel() {
        return scheduleModel;
    }


    //setters
    public void setScheduleModel(ScheduleModel scheduleModel) {
        this.scheduleModel = scheduleModel;
    }


    //methods
    public void fillSchedule() {
        Vehicle vehicle = vehiclesListBean.getVehicleToViewSchedule();
        if (vehicle == null) {
            return;
        }
        scheduleModel = new DefaultScheduleModel();
        List<Application> apps = applicationDAO.getApplicationPeriodsByVehicle(vehicle);
        if (apps == null) {
            return;
        }
        for (Application app : apps) {

            //need to set endDate to the end of day
            Calendar cal = Calendar.getInstance();
            cal.setTime(app.getEndDate());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);

            scheduleModel.addEvent(
                    new DefaultScheduleEvent("vehicle busy",
                            app.getStartDate(),
                            cal.getTime()));
        }
    }

}
