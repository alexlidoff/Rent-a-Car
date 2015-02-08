package managers;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.primefaces.model.SortOrder;

import model.Application;
import model.ApplicationStatus;
import model.Client;
import model.Vehicle;
import dao.ApplicationDAO;


public class ApplicationManager implements Serializable {

    private static final long serialVersionUID = 3127753155163495126L;
    @Inject
    private ApplicationDAO applicationDAO;

    public List<Application> getAllApplications() {
        return applicationDAO.getAll();
    }

    public List<Application> getClientApplications(Client client) {
        return applicationDAO.getClientApplications(client);
    }

    public void saveApplication(Application application) {
        if (application.getId() == null || application.getId() == 0) {
            application.setApplicationStatus(ApplicationStatus.New);
            applicationDAO.save(application);
        } else {
            applicationDAO.update(application);
        }
    }

    public void markAppicationPaid(Application application) {
        application.setRentPaid(true);
        saveApplication(application);
    }

    public boolean existApplicationsInPeriod(Vehicle vehicle, Date begin, Date end) {
        Long apps = applicationDAO.getApplicationsQuantityInPeriod(vehicle, begin, end);
        return (apps > 0);
    }

    public List<Application> getByConditions(int first, int pageSize, String sortField,
                                             SortOrder sortOrder, Map<String, String> filters) {
        return applicationDAO.getByConditions(first, pageSize, sortField,
                sortOrder, filters);
    }

    public int count(String sortField, SortOrder sortOrder,
                     Map<String, String> filters) {
        return applicationDAO.getByConditions(-1, -1, null, null, filters).size();
    }

}
