package service.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import localization.LocalizationManager;


@FacesConverter(value = "Rent-a-Car.service.converters.GearboxConverter")
public class GearboxConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        return Integer.valueOf(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        String gearbox = value.toString();
        switch (gearbox) {
            case "1":
                return LocalizationManager.getLocalizedText("gearboxManual");
            case "2":
                return LocalizationManager.getLocalizedText("gearboxAutomatic");
            default:
                return "unknown";
        }
    }

}
