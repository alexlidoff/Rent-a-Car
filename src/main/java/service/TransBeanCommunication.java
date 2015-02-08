package service;

import javax.faces.context.FacesContext;

public class TransBeanCommunication {

    //flash
    public static Object putValue(String key, Object value) {
        return FacesContext.getCurrentInstance().getExternalContext().
                getFlash().put(key, value);
    }

    public static Object getValue(String key) {
        return FacesContext.getCurrentInstance().getExternalContext().
                getFlash().get(key);
    }


    //session
    public static Object putValue(String key, Object value, boolean session) {
        return FacesContext.getCurrentInstance().getExternalContext().
                getSessionMap().put(key, value);
    }

    public static Object getValue(String key, boolean session) {
        return FacesContext.getCurrentInstance().getExternalContext().
                getSessionMap().get(key);
    }

}
