package fi.hut.soberit.agilefant.db.hibernate;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.common.base.Strings;

/**
 * Implementation of the email validator. When using the email annotation in the
 * hibernate data model, this class is used to validate if the field is a proper
 * email address.
 * 
 * @author Turkka Äijälä
 * @see fi.fi.hut.soberit.agilefant.db.hibernate.Email
 */
public class EmailValidator implements ConstraintValidator<Email, String>, Serializable {

    private static final long serialVersionUID = 4334203403474352735L;
	private Pattern pattern;
	private Matcher matcher;

    @Override
    public void initialize(Email parameters) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Strings.isNullOrEmpty(value) || org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(value))
            return true;
        return validate(value);
    }

	private static final String EMAIL_PATTERN = 
		"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z0-9]{2,})$";

	public EmailValidator() {
		pattern = Pattern.compile(EMAIL_PATTERN);
	}

	public boolean validate(final String hex) {
		matcher = pattern.matcher(hex);
		return matcher.matches();
	}

}