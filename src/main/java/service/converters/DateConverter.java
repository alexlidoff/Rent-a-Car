package service.converters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter(value = "Rent-a-Car.service.converters.DateConverter")
public class DateConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
	    String value) {
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	Date date;
	try {
	    date = sdf.parse(value);
	} catch (ParseException e) {
	    date = null;
	}
	return date;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
	    Object value) {
	DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
	return dateFormat.format(value);
    }

}
