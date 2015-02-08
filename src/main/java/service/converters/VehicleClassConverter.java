package service.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import model.VehicleClass;


@FacesConverter(value = "Rent-a-Car.service.converters.VehicleClassConverter")
public class VehicleClassConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
	    String value) {
	return VehicleClass.valueOf(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
	    Object value) {
	return value.toString();
    }

}
