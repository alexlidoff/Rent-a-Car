package beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import managers.StaffManager;
import model.Staff;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import service.TransBeanCommunication;

@Named("staffListBean")
@ViewScoped
public class StaffListBean implements Serializable {

    private static final long serialVersionUID = -8027949858421828734L;
    @Inject
    private StaffManager staffManager;
    private LazyDataModel<Staff> staffDataModel;
    private boolean chooseMode;
    private ApplicationStaffBean applicationStaffBean;


    public StaffListBean() {
        chooseMode = false;
    }

    @PostConstruct
    private void init() {

        staffDataModel = new LazyDataModel<Staff>() {

            private static final long serialVersionUID = 5870586808707389173L;

            @Override
            public List<Staff> load(int first, int pageSize,
                                    String sortField, SortOrder sortOrder,
                                    Map<String, String> filters) {

                staffDataModel.setRowCount(staffManager.count(sortField,
                        sortOrder, filters));
                return staffManager.getByConditions(first, pageSize,
                        sortField, sortOrder, filters);
            }

            @Override
            public Staff getRowData(String rowKey) {
                @SuppressWarnings("unchecked")
                List<Staff> staff = (List<Staff>) getWrappedData();

                for (Staff employee : staff) {
                    if (employee.getId().equals(Integer.valueOf(rowKey)))
                        return employee;
                }

                return null;
            }


            @Override
            public String getRowKey(Staff employee) {
                return String.valueOf(employee.getId());
            }

        };

    }


    //getters
    public LazyDataModel<Staff> getStaffDataModel() {
        return staffDataModel;
    }

    public boolean isChooseMode() {
        return chooseMode;
    }

    public ApplicationStaffBean getApplicationStaffBean() {
        return applicationStaffBean;
    }


    //setters
    public void setApplicationStaffBean(ApplicationStaffBean applicationStaffBean) {
        this.applicationStaffBean = applicationStaffBean;
    }

    public void setStaffDataModel(LazyDataModel<Staff> staffDataModel) {
        this.staffDataModel = staffDataModel;
    }

    public void setChooseMode(boolean chooseMode) {
        this.chooseMode = chooseMode;
    }


    //methods
    public String openEmployee(Staff employee) {
        //setting chosen employee to flash to get it in StaffBean
        TransBeanCommunication.putValue("employeeToOpen", employee);
        return "openStaffForm";
    }

    public void chooseEmployee(Staff employee) {
        applicationStaffBean.setManager(employee);
    }

    public void onRowDoubleClick(SelectEvent event) {
        Staff employee = (Staff) event.getObject();
        if (employee == null) {
            return;
        }

        if (chooseMode) {
            //choose mode - setting employee into applicationStaffBean and closing choose dialog
            chooseEmployee(employee);
            RequestContext.getCurrentInstance().execute("PF('staffChooseDialog').hide();");
        } else {
            //usual mode
            //setting chosen employee to flash to get it in StaffBean
            TransBeanCommunication.putValue("employeeToOpen", employee);
            //than opening staff form with help of navigation rule
            FacesContext context = FacesContext.getCurrentInstance();
            NavigationHandler navigationHandler = context.getApplication().
                    getNavigationHandler();
            navigationHandler.handleNavigation(context, null, "openStaffForm");
        }
    }

}
