package service.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import localization.LocalizationManager;


@FacesConverter(value = "Rent-a-Car.service.converters.DriveConverter")
public class DriveConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) {
        return Integer.valueOf(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              Object value) {
        String drive = value.toString();
        switch (drive) {
            case "1":
                return LocalizationManager.getLocalizedText("frontWheelDrive");
            case "2":
                return LocalizationManager.getLocalizedText("rearWheelDrive");
            case "3":
                return LocalizationManager.getLocalizedText("fullWheelDrive");
            default:
                return "unknown";
        }
    }

}
