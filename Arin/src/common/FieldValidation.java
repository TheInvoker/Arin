package common;

import java.util.regex.Pattern;

public class FieldValidation {
	
	// regex for email
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	 
	private static Pattern pattern = Pattern.compile(EMAIL_PATTERN);
	
	
	public static String fixURL(String url) {
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = "http://" + url;
		}
		return url;
	}	
	
	// checks if username is valid
	public static Boolean isValidName(String name) {
		return isNameOk(name);
	}
	
	// checks if email is valid
	public static Boolean isValidEmail(String email) {
		return pattern.matcher(email).matches();
	}
	
	// checks if password is valid
	public static Boolean isValidPassword(String password) {
		return isNameOk(password);
	}
	
	public static Boolean isValidThreadTitle(String title) {
		return isNameOk(title);
	}
	
	public static Boolean isValidFishName(String name) {
		return isNameOk(name);
	}
	
	
	private static Boolean isNameOk(String name) {
		if (name.trim().length() == 0) {
			return false;
		}
		
		for (int i=0; i<name.length(); i+=1) {
			char c = name.charAt(i);
			if (c != ' ' && !Character.isLetterOrDigit(c)) {
				return false;
			}
		}
		return true;
	}
}
