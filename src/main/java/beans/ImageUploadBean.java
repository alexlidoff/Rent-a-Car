package beans;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Inject;
import javax.inject.Named;

import model.Vehicle;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;


@Named("imageUploadBean")
@SessionScoped
public class ImageUploadBean implements Serializable {

    private static final long serialVersionUID = -703169672120292876L;
    private Vehicle vehicle;
    @Inject
    private ImageBean imageBean;
    
    public Vehicle getVehicle() {
	return vehicle;
    }
    
    public void setVehicle(Vehicle vehicle) {
	this.vehicle = vehicle;
    }

    
    public long getRandomValue() {
	return System.currentTimeMillis();
    }
    
    public StreamedContent getImage() {
	FacesContext context = FacesContext.getCurrentInstance();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            //rendering the view phase.
            //Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {            
            //browser is requesting the image.
            //Return a real StreamedContent with the image bytes.
            if (vehicle.getPhoto() == null) {
        	
        	return imageBean.getImage(vehicle.getId());
        	
            } else {
              
        	return new DefaultStreamedContent(
        		new ByteArrayInputStream(vehicle.getPhoto()) );
            }
        }
    }
    
}
