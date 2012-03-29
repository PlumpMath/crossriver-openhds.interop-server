package org.openhds.mobileinterop.web.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.openhds.mobileinterop.dao.UserDao;
import org.openhds.mobileinterop.model.Authority;
import org.openhds.mobileinterop.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/users")
public class UserController {
	
	private UserDao dao;

	@Autowired
	public UserController(UserDao dao) {
		this.dao = dao;
	}
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public ModelAndView managementView() {
		ModelAndView mv = new ModelAndView("userManagement");
		mv.addObject("userList", dao.getAllUsers());
		
		return mv;
	}
	
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public ModelAndView getFieldWorkerPage() {
		return new ModelAndView("fieldWorkerCreate");
	}
	
	@RequestMapping(value="/createFieldWorker", method=RequestMethod.POST)
	public ModelAndView createFieldWorker(@RequestParam("username")String username, @RequestParam("password") String password, @RequestParam("role") String role) {
		ModelAndView mv = new ModelAndView();
		List<String> errors = new ArrayList<String>();
		
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		
		if (!hasUserName(user)) {
			errors.add("No username provided");
		}
		
		if (!hasPassword(user)) {
			errors.add("No password was provided");
		}
		
		if (errors.size() > 0) {
			mv.setViewName("fieldWorkerCreate");
			mv.addObject("formUser", user);
			mv.addObject("errors", errors);
			return mv;
		}
		
		user.setUsername(user.getUsername().toUpperCase());
		String roleName = null;
		if ("admin".equals(role)) {
			roleName = Authority.ADMIN;
		} else {
			roleName = Authority.FIELD_WORKER;
		}
		
		dao.saveUser(user, roleName);
		mv.setViewName("redirect:/admin/users/");
		return mv;
	}

	private boolean hasUserName(User user) {
		return user.getUsername() != null && !user.getUsername().trim().isEmpty();
	}

	private boolean hasPassword(User user) {
		return user.getPassword() != null && !user.getPassword().trim().isEmpty();
	}

}
