package org.openhds.mobileinterop.web.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openhds.mobileinterop.dao.UserDao;
import org.openhds.mobileinterop.model.Authority;
import org.openhds.mobileinterop.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles administrative tasks relating to the users of the interop servers
 */
@Controller
@RequestMapping(value = "/admin/users")
public class UserController extends AbstractUserController {

	private UserDao dao;

	@Autowired
	public UserController(UserDao dao) {
		this.dao = dao;
	}

	@RequestMapping(value = { "", "/" })
	public ModelAndView managementView() {
		ModelAndView mv = new ModelAndView("userManagement");
		mv.addObject("userList", dao.getAllUsers());

		return mv;
	}

	@RequestMapping(value = "/create")
	public ModelAndView getFieldWorkerPage() {
		return new ModelAndView("userCreate");
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ModelAndView createFieldWorker(@RequestBody MultiValueMap<String, String> formValues) {
		User user = getUserFromSubmittedValues(formValues);

		List<String> errors = validateUser(user, formValues.getFirst("confirmPassword"));
		if (errors.size() > 0) {
			return showCreatePageWithErrors(errors, user);
		}

		User persistedUser = dao.findUserById(user.getUsername());
		if (persistedUser != null) {
			errors.add("Already a user registered with that username");
			return showCreatePageWithErrors(errors, user);
		}

		addSupervisedFieldWorkers(formValues, user);

		dao.saveUser(user, Authority.SUPERVISOR);
		ModelAndView mv = new ModelAndView();
		mv.setViewName("redirect:/admin/users/");
		return mv;
	}

	private void addSupervisedFieldWorkers(MultiValueMap<String, String> formValues, User user) {
		List<String> supervisedFws = formValues.get("supervisedFieldworker");
		for (String fw : supervisedFws) {
			if (StringUtils.isNotBlank(fw)) {
				user.getManagedFieldworkers().add(fw);
			}
		}
	}

	@RequestMapping("/edit/{username}")
	public ModelAndView showEditUser(@PathVariable("username") String username) {
		if ("admin".equalsIgnoreCase(username)) {
			return redirectToUserList();
		}

		User user = dao.findUserById(username);
		if (user == null) {
			return redirectToUserList();
		}

		ModelAndView mv = new ModelAndView("userEdit");
		mv.addObject("user", user);

		return mv;
	}

	@RequestMapping(value = "/edit/{username}", method = RequestMethod.POST)
	public ModelAndView updateUser(@PathVariable("username") String username,
			@RequestBody MultiValueMap<String, String> formValues) {
		formValues.remove("username");
		formValues.add("username", username);

		User user = getUserFromSubmittedValues(formValues);
		addSupervisedFieldWorkers(formValues, user);

		List<String> errors = new ArrayList<String>();
		validatePasswords(user, formValues.getFirst("confirmPassword"), errors, false);
		if (errors.size() > 0) {
			return showEditPageWithErrors(errors, user);
		}

		dao.updateUser(user);

		return redirectToUserList();
	}

	private ModelAndView redirectToUserList() {
		return new ModelAndView("redirect:/admin/users/");
	}

	private ModelAndView showEditPageWithErrors(List<String> errors, User user) {
		return addModelObjects("userEdit", errors, user);
	}

	@RequestMapping("/delete/{username}")
	public ModelAndView showDeleteUser(@PathVariable("username") String username) {
		if ("admin".equalsIgnoreCase(username)) {
			return redirectToUserList();
		}

		User user = dao.findUserById(username);
		if (user == null) {
			return redirectToUserList();
		}

		ModelAndView mv = new ModelAndView("userDelete");
		mv.addObject("username", username);

		return mv;
	}

	@RequestMapping(value = "/delete/{username}", method = RequestMethod.POST)
	public ModelAndView deleteUser(@PathVariable("username") String username,
			@RequestBody MultiValueMap<String, String> formValues) {
		if (formValues.containsKey("delete")) {
			dao.deleteUser(username);
		}

		return redirectToUserList();
	}
}
