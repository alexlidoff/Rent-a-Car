package service.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import localization.LocalizationManager;


@FacesConverter(value = "Rent-a-Car.service.converters.BooleanConverter")
public class BooleanConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        return Boolean.valueOf(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        boolean b = (boolean) value;
        if (b) {
            return LocalizationManager.getLocalizedText("booleanTrue");
        } else {
            return LocalizationManager.getLocalizedText("booleanFalse");
        }
    }

}
