package org.openhds.mobileinterop.web;
import javax.servlet.http.HttpServletResponse;

import org.openhds.mobileinterop.dao.FormSubmissionDao;
import org.openhds.mobileinterop.model.FormSubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Receives a form submission from an external server
 */
@Controller
@RequestMapping("/form-submission")
public class FormSubmissionController {
	private final static Logger logger = LoggerFactory.getLogger(FormSubmissionController.class);
	
	private FormSubmissionDao dao;
	
	@Autowired
	public FormSubmissionController(FormSubmissionDao dao) {
		this.dao = dao;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public void handleFormSubmission(@RequestBody FormSubmission submission, HttpServletResponse response) {
		logger.debug(submission.toString());
		dao.saveFormSubmission(submission);
	}
}
