package org.openhds.mobileinterop.web;

import org.openhds.mobileinterop.model.FormUser;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value="/createFieldWorker.do")
public class UserController {
	
	@RequestMapping(method=RequestMethod.POST)
	public ModelAndView createFieldWorker(FormUser user, BindingResult result) {
		System.out.println(user.getManagedIds());
		return null;
	}

}
