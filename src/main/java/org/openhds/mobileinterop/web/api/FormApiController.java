package org.openhds.mobileinterop.web.api;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.openhds.mobileinterop.dao.FormDao;
import org.openhds.mobileinterop.dao.UserDao;
import org.openhds.mobileinterop.model.CompletedFormSubmission;
import org.openhds.mobileinterop.model.FormSubmission;
import org.openhds.mobileinterop.model.FormSubmissionSet;
import org.openhds.mobileinterop.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/api/form")
public class FormApiController {

	private FormDao dao;
	private UserDao userDao;

	@Autowired
	public FormApiController(FormDao dao, UserDao userDao) {
		this.dao = dao;
		this.userDao = userDao;
	}

	@RequestMapping(method = RequestMethod.POST)
	public void handleFormSubmission(@RequestBody FormSubmission submission, HttpServletResponse response) {
		dao.saveFormSubmission(submission);
	}
	
	@RequestMapping(value = "/complete", method = RequestMethod.POST)
	public void setFormSubmissionToFixed(@RequestBody CompletedFormSubmission completedSubmission, HttpServletResponse response) {
		dao.completeFormSubmissionGroup(completedSubmission.getCompletedFormSubmissionId(), completedSubmission.getFormSubmission());
		
		response.setStatus(HttpServletResponse.SC_OK);
		try {
			response.flushBuffer();
		} catch (IOException e) { }
	}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public ResponseEntity<FormSubmissionSet> getFormInstancesForUser() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication auth = context.getAuthentication();
		String username = auth.getName();
		User user = userDao.findUserById(username);
		List<FormSubmission> submissions = dao.findDownloadableSubmissionsForUser(user);
		if (submissions.size() == 0) {
			return new ResponseEntity<FormSubmissionSet>(HttpStatus.NO_CONTENT);
		}

		FormSubmissionSet set = new FormSubmissionSet();
		for (FormSubmission submission : submissions) {
			set.addSubmission(submission);
		}

		return new ResponseEntity<FormSubmissionSet>(set, HttpStatus.OK);
	}
}
