package localization;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.Cookie;

import java.io.Serializable;

@Named(value = "localizationManager")
@SessionScoped
public class LocalizationManager implements Serializable {
    
    private static final long serialVersionUID = -636535369247949076L;
    
    private Locale locale = getDefaultLocale();
    
    
    public static String getLocalizedText(String key, Object ... params) {
	try {
	    ResourceBundle bundle = ResourceBundle.getBundle("localization.texts.texts",
		    FacesContext.getCurrentInstance().getViewRoot().getLocale());
	    return MessageFormat.format(bundle.getString(key), params);
	} catch (Exception e) {
	    return key + " not found";
	}
    }
    
    
    private Locale getDefaultLocale() {
	FacesContext context = FacesContext.getCurrentInstance();
	Map<String, Object> cookies = context.getExternalContext().
		getRequestCookieMap();
	Cookie cookie = (Cookie) cookies.get("language");
	if (cookie != null) {
	    return new Locale(cookie.getValue());
	} else {
	    return context.getApplication().getDefaultLocale();
	}
    }
    
    private String transformLanguage(String language) {
	switch (language) {
	case "english":
	    return "en";
	case "русский":
	    return "ru";
	case "українська":
	    return "uk";
	case "en":
	    return "english";
	case "ru":
	    return "русский";
	case "uk":
	    return "українська";
	default:
	    return "en";
	}
    }
    
    public Locale getLocale() {
	return locale;
    }
    
    public String getLanguage() {
	return transformLanguage(locale.getLanguage());
    }
    
    public void setLanguage(String languageFull) {
	locale = new Locale(transformLanguage(languageFull));
	FacesContext.getCurrentInstance().getViewRoot().
		setLocale(locale);
	
	Map<String, Object> properties = new HashMap<String, Object>();
	properties.put("maxAge", 2592000);//30 days
	FacesContext.getCurrentInstance().getExternalContext().
		addResponseCookie("language", locale.getLanguage(), properties);
    }
    
    public String[] getLanguages() {
	return new String[] {"english", "русский", "українська"};
    }
    
}
