package org.openhds.mobileinterop.web.admin;

import java.util.ArrayList;
import java.util.List;

import org.openhds.mobileinterop.model.User;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;

public abstract class AbstractUserController {

	protected User getUserFromSubmittedValues(MultiValueMap<String, String> formValues) {
		User user = new User();
		user.setUsername(formValues.getFirst("username"));
		user.setPassword(formValues.getFirst("password"));
		
		return user;
	}

	protected List<String> validateUser(User user, String confirmPassword) {
		List<String> errors = new ArrayList<String>();
		if (!user.validUsername()) {
			errors.add("No username provided");
		}
	
		if (!user.validPassword()) {
			errors.add("No password was provided");
		}
	
		if (!user.passwordMatch(confirmPassword)) {
			errors.add("Passwords do not match");
		}
	
		return errors;
	}

	protected ModelAndView showCreatePageWithErrors(List<String> errors, User user) {
		return addModelObjects("userCreate", errors, user);
	}

	protected ModelAndView addModelObjects(String viewName, List<String> errors, User user) {
		ModelAndView mv = new ModelAndView(viewName);
		mv.addObject("formUser", user);
		mv.addObject("errors", errors);
		 
		return mv;
	}

}
