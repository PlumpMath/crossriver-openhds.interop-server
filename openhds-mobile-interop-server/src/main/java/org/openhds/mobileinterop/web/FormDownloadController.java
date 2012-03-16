package org.openhds.mobileinterop.web;

import org.openhds.mobileinterop.dao.FormSubmissionDao;
import org.openhds.mobileinterop.model.FormSubmissionSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/form-download")
public class FormDownloadController {
	
	private FormSubmissionDao dao;

	@Autowired
	public FormDownloadController(FormSubmissionDao dao) {
		this.dao = dao;
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public FormSubmissionSet getFormInstancesForUser() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication auth = context.getAuthentication();
		String user = auth.getName();
		dao.findSubmissionsByOwner(user);
		return null;
	}

}
