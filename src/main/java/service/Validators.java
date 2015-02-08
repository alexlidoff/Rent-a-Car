package service;

import java.math.BigDecimal;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import localization.LocalizationManager;
import beans.UserBean;
import dao.UserDAO;


@Named(value = "validators")
@ApplicationScoped
public class Validators {

    @Inject
    private UserBean userBean;
    @Inject
    private UserDAO userDAO;


    public void validateEmail(FacesContext context, UIComponent component,
                              Object value) {

        String email = ((String) value).toLowerCase();
        String messageText = null;
        boolean valueNotValid = false;

        //length
        if (email.length() < 5 || email.length() > 100) {
            valueNotValid = true;
            messageText = LocalizationManager.
                    getLocalizedText("emailNotCorrectMessage");
        }

        //regex
        if (!email.matches("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)+$")) {
            valueNotValid = true;
            messageText = LocalizationManager.
                    getLocalizedText("emailNotCorrectMessage");
        }

        //if the user is not logged we should check email for not being in use
        if (!userBean.isUserLogged()) {
            if (userDAO.getUserByLogin(email) != null) {
                valueNotValid = true;
                messageText = LocalizationManager.
                        getLocalizedText("emailIsInUseMessage", email);
            }
        }

        if (valueNotValid) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    messageText, messageText);
            throw new ValidatorException(message);
        }

    }

    public void validateZIPCode(FacesContext context, UIComponent component,
                                Object value) {

        String zip = (String) value;

        //empty value is valid
        if (zip.isEmpty()) {
            return;
        }

        boolean valueNotValid = false;
        //length
        if (zip.length() < 5 || zip.length() > 10) {
            valueNotValid = true;
        }
        //regex - only digits are allowed
        if (!zip.matches("\\d+")) {
            valueNotValid = true;
        }

        if (valueNotValid) {
            String messageText = LocalizationManager.
                    getLocalizedText("zipCodeNotCorrectMessage");
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    messageText, messageText);
            throw new ValidatorException(message);
        }

    }

    public void validatePassword(FacesContext context, UIComponent component,
                                 Object value) {

        String password = (String) value;

        if (password.isEmpty()) {
            //empty password means that this is not password change or
            //password first enter event, but just a client profile save event
            //without password re-entering
            return;
        }

        boolean valueNotValid = false;
        String messageText = null;

        //length
        if (password.length() < 5) {
            valueNotValid = true;
            messageText = LocalizationManager.
                    getLocalizedText("passwordTooShortMessage");
        } else if (password.length() > 20) {
            valueNotValid = true;
            messageText = LocalizationManager.
                    getLocalizedText("passwordTooLongMessage");
        }

        //special characters
        if (password.matches("\\s+")) {
            valueNotValid = true;
            messageText = LocalizationManager.
                    getLocalizedText("passwordIncorrectCharacterMessage");
        }

        if (valueNotValid) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    messageText, messageText);
            throw new ValidatorException(message);
        }

    }

    public void validatePrice(FacesContext context, UIComponent component,
                              Object value) {

        boolean valueNotValid = false;

        BigDecimal price = BigDecimal.ZERO;
        try {
            price = (BigDecimal) value;
        } catch (Exception e) {
            valueNotValid = true;
        }

        if (!valueNotValid) {
            if (price != price.abs()) {
                valueNotValid = true;
            }
            if (price.equals(BigDecimal.ZERO)) {
                valueNotValid = true;
            }
        }

        if (valueNotValid) {
            String messageText = LocalizationManager.
                    getLocalizedText("fieldIncorrectMessage");
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    messageText, messageText);
            throw new ValidatorException(message);
        }

    }

}
