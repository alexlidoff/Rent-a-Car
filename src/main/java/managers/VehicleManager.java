package managers;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import model.Vehicle;

import org.primefaces.model.SortOrder;

import dao.VehicleDAO;

public class VehicleManager implements Serializable {

    private static final long serialVersionUID = -3022307093066689237L;
    @Inject
    VehicleDAO vehicleDAO;


    public Vehicle getById(Integer id) {
        if (id == null) {
            return null;
        }
        return vehicleDAO.getById(id);
    }

    public List<Vehicle> getAll() {
        return vehicleDAO.getAll();
    }

    public List<Vehicle> getAllNotDisabled() {
        return vehicleDAO.getAllNotDisabled();
    }

    public List<Vehicle> getByConditions(int first, int pageSize, String sortField,
                                         SortOrder sortOrder, Map<String, String> filters,
                                         Date startDate, Date endDate, boolean clientMode) {

        return vehicleDAO.getByConditions(first, pageSize, sortField,
                sortOrder, filters, startDate, endDate, clientMode);
    }

    public int count(String sortField, SortOrder sortOrder,
                     Map<String, String> filters,
                     Date startDate, Date endDate, boolean clientMode) {

        return vehicleDAO.getByConditions(-1, -1, null, null, filters,
                startDate, endDate, clientMode).size();
    }

    public void saveVehicle(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId() == 0) {
            vehicleDAO.save(vehicle);
        } else {
            vehicleDAO.update(vehicle);
        }
    }

}
