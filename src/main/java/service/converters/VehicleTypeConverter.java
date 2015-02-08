package service.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import dao.VehicleTypeDAO;
import model.VehicleType;


@FacesConverter(value = "Rent-a-Car.service.converters.VehicleTypeConverter")
public class VehicleTypeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
	    String value) {
	
	Context cont = null;
	try {
	    cont = new InitialContext();
	} catch (NamingException e) {
	    return null;
	}
	
	VehicleTypeDAO vehicleTypeDAO = null;
	try {
	    vehicleTypeDAO = (VehicleTypeDAO) cont.lookup(
		    "java:global/Rent-a-Car/VehicleTypeDAO!dao.VehicleTypeDAO");
	} catch (NamingException e) {
	    return null;
	}
	
	VehicleType vehicleType = vehicleTypeDAO.getById(Integer.valueOf(value));
	
	return vehicleType;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
	    Object value) {
	
	VehicleType vehicleType = (VehicleType) value;
	return String.valueOf(vehicleType.getId());
    }

}
