package beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import localization.LocalizationManager;
import managers.ApplicationManager;
import model.Application;
import model.ApplicationStatus;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import service.TransBeanCommunication;


@Named("appListStaffBean")
@ViewScoped
public class ApplicationsListStaffBean implements Serializable {

    private static final long serialVersionUID = 6202848360381066282L;
    @Inject
    private ApplicationManager applicationManager;
    private LazyDataModel<Application> applicationDataModel;


    public ApplicationsListStaffBean() {
    }

    @PostConstruct
    private void init() {

        applicationDataModel = new LazyDataModel<Application>() {

            private static final long serialVersionUID = -2553339330257729230L;


            @Override
            public List<Application> load(int first, int pageSize,
                                          String sortField, SortOrder sortOrder,
                                          Map<String, String> filters) {

                applicationDataModel.setRowCount(applicationManager.count(sortField,
                        sortOrder, filters));
                return applicationManager.getByConditions(first, pageSize,
                        sortField, sortOrder, filters);
            }


            @Override
            public Application getRowData(String rowKey) {
                @SuppressWarnings("unchecked")
                List<Application> applications = (List<Application>) getWrappedData();

                for (Application application : applications) {
                    if (application.getId().equals(Integer.valueOf(rowKey)))
                        return application;
                }

                return null;
            }


            @Override
            public String getRowKey(Application application) {
                return String.valueOf(application.getId());
            }

        };

        //setting applicationStatus filter = New - to show only new applications by default
        //TURNED OFF - decided to make default status+id sorting instead of filtering
//	DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().
//		getViewRoot().findComponent("applicationsListForm:applicationsTable");
//	Map<String, String> filters = new HashMap<>();
//	filters.put("applicationStatus", "New");
//	dataTable.setFilters(filters);

    }


    //getters
    public LazyDataModel<Application> getApplicationDataModel() {
        return applicationDataModel;
    }


    //setters
    public void setApplicationDataModel(LazyDataModel<Application> applicationDataModel) {
        this.applicationDataModel = applicationDataModel;
    }


    //methods
    public SelectItem[] getApplicationStatuses() {
        ApplicationStatus[] applicationStatuses = ApplicationStatus.values();
        SelectItem[] statuses = new SelectItem[applicationStatuses.length + 1];
        statuses[0] = new SelectItem("", "-");
        for (int i = 0; i < applicationStatuses.length; i++) {
            statuses[i + 1] = new SelectItem(applicationStatuses[i],
                    applicationStatuses[i].toString());
        }
        return statuses;
    }

    public SelectItem[] getBooleanOptions() {
        SelectItem[] options = new SelectItem[3];
        options[0] = new SelectItem("", "-");
        options[1] = new SelectItem(Boolean.TRUE.toString(),
                LocalizationManager.getLocalizedText("booleanTrue"));
        options[2] = new SelectItem(Boolean.FALSE.toString(),
                LocalizationManager.getLocalizedText("booleanFalse"));
        return options;
    }

    public String openApplication(Application application) {
        //setting chosen application to flash to get it in VehicleBean
        TransBeanCommunication.putValue("applicationToOpen", application);
        return "openApplicationFormStaff";
    }

    public void onRowDoubleClick(SelectEvent event) {
        Application application = (Application) event.getObject();
        if (application == null) {
            return;
        }
        //setting chosen application to flash to get it in ApplicationBean
        TransBeanCommunication.putValue("applicationToOpen", application);
        FacesContext context = FacesContext.getCurrentInstance();
        NavigationHandler navigationHandler = context.getApplication().
                getNavigationHandler();
        navigationHandler.handleNavigation(context, null, "openApplicationFormStaff");
    }

}
