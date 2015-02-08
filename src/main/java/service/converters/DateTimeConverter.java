package service.converters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter(value = "Rent-a-Car.service.converters.DateTimeConverter")
public class DateTimeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss");
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss");
        return sdf.format(value);
    }

}
