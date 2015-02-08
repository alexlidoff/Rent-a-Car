package beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import managers.ClientManager;
import model.Client;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import service.TransBeanCommunication;


@Named("clientsListBean")
@ViewScoped
public class ClientsListStaffBean implements Serializable {

    private static final long serialVersionUID = -7193746560840639261L;
    @Inject
    private ClientManager clientManager;
    private LazyDataModel<Client> clientDataModel;
    private boolean chooseMode;
    private ApplicationStaffBean applicationStaffBean;
    
    
    public ClientsListStaffBean() {
	chooseMode = false;
    }
    
    @PostConstruct
    private void init() {
	
	clientDataModel = new LazyDataModel<Client>() {
	    
	    private static final long serialVersionUID = -6587725658073802713L;

	    @Override
	    public List<Client> load(int first, int pageSize,
		    String sortField, SortOrder sortOrder,
		    Map<String, String> filters) {
		
		clientDataModel.setRowCount(clientManager.count(sortField, 
			sortOrder, filters));
		return clientManager.getByConditions(first, pageSize, 
			sortField, sortOrder, filters);
	    }
	    
	    @Override
	    public Client getRowData(String rowKey) {
		@SuppressWarnings("unchecked")
		List<Client> clients = (List<Client>) getWrappedData();

	        for(Client client : clients) {
	            if (client.getId().equals(Integer.valueOf(rowKey)))
	                return client;
	        }

	        return null;
	    }
	    

	    @Override
	    public String getRowKey(Client client) {
		return String.valueOf(client.getId());
	    }
	    
	};
		
    }
    
    //getters
    public LazyDataModel<Client> getClientDataModel() {
	return clientDataModel;
    }
    public boolean isChooseMode() {
	return chooseMode;
    }
    public ApplicationStaffBean getApplicationStaffBean() {
	return applicationStaffBean;
    }


    //setters
    public void setClientDataModel(LazyDataModel<Client> clientDataModel) {
	this.clientDataModel = clientDataModel;
    }
    public void setChooseMode(boolean chooseMode) {
  	this.chooseMode = chooseMode;
      }
    public void setApplicationStaffBean(ApplicationStaffBean applicationStaffBean) {
	this.applicationStaffBean = applicationStaffBean;
    }
    
    
    //methods
    public String openClient(Client client) {
	//setting chosen client to flash to get it in ClientBean
	TransBeanCommunication.putValue("clientToOpen", client);
	return "openClientFormStaff";
    }
    
    public void chooseClient(Client client) {
	applicationStaffBean.setClient(client);
    }
    
    public void onRowDoubleClick(SelectEvent event) {
	Client client = (Client) event.getObject();
	if (client == null) {
	    return;
	}
	
	if (chooseMode) {
	    //choose mode - setting client into applicationStaffBean and closing choose dialog
	    chooseClient(client);
	    RequestContext.getCurrentInstance().execute("PF('clientChooseDialog').hide();");
	} else {
	    //usual mode
	    //setting chosen client to flash to get it in ClientBean
	    TransBeanCommunication.putValue("clientToOpen", client);
	    //than opening client form with help of navigation rule
	    FacesContext context = FacesContext.getCurrentInstance();
	    NavigationHandler navigationHandler = context.getApplication().
		    getNavigationHandler();
	    navigationHandler.handleNavigation(context, null, "openClientFormStaff");
	}
    }
    
}
