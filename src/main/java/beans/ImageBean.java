package beans;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

import localization.LocalizationManager;
import model.Vehicle;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import dao.VehicleDAO;


@Named(value = "imageBean")
@ApplicationScoped
public class ImageBean implements Serializable {

    private static final long serialVersionUID = -2251987285888777476L;
    @Inject
    private VehicleDAO vehicleDAO;


    public ImageBean() {
    }


    public Vehicle getVehicle(Integer id) {
        return vehicleDAO.getById(id);
    }

    public StreamedContent getNoImage() {
        String absPath = ((ServletContext) FacesContext.getCurrentInstance().
                getExternalContext().getContext()).getRealPath("/");
        String path = absPath + "resources/images/no_photo.jpg";
        File noImageFile = new File(path);
        BufferedImage img = null;
        try {
            img = ImageIO.read(noImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "jpg", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new DefaultStreamedContent(
                new ByteArrayInputStream(baos.toByteArray()));
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
            String vehicleId = context.getExternalContext().getRequestParameterMap().
                    get("vehicleId");
            if (vehicleId == null || vehicleId.isEmpty()) {
                return getNoImage();
            } else {
                Vehicle vehicle = getVehicle(Integer.valueOf(vehicleId));
                if (vehicle.getPhoto() == null) {
                    return getNoImage();
                } else {
                    return new DefaultStreamedContent(
                            new ByteArrayInputStream(vehicle.getPhoto()));
                }
            }
        }
    }

    public StreamedContent getImage(Integer vehicleId) {

        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            //rendering the view phase.
            //Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            //browser is requesting the image.
            //Return a real StreamedContent with the image bytes.            
            if (vehicleId == null || vehicleId == 0) {
                return getNoImage();
            } else {
                Vehicle vehicle = getVehicle(vehicleId);
                if (vehicle.getPhoto() == null) {
                    return getNoImage();
                } else {
                    return new DefaultStreamedContent(
                            new ByteArrayInputStream(vehicle.getPhoto()));
                }
            }
        }
    }

    public MapModel getMapModel() {
        MapModel mapModel = new DefaultMapModel();
        LatLng ourCoords = new LatLng(37.405909d, -122.030960d);
        mapModel.addOverlay(new Marker(ourCoords,
                LocalizationManager.getLocalizedText("mainHeader1")));
        return mapModel;
    }

}
