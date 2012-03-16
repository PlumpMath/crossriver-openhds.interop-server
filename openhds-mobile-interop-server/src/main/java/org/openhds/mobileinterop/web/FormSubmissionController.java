package org.openhds.mobileinterop.web;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.openhds.mobileinterop.dao.FormSubmissionDao;
import org.openhds.mobileinterop.model.FormSubmission;
import org.openhds.mobileinterop.model.FormSubmissionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@RequestMapping(value="/find/list/ownerId/{ownerId}", method=RequestMethod.GET)
	public ResponseEntity<FormSubmissionSet> getSubmissionByOwner(@PathVariable("ownerId") String ownerId) {
		List<FormSubmission> submissions = dao.findSubmissionsByOwner(ownerId);
		if (submissions.size() == 0) {
			return new ResponseEntity<FormSubmissionSet>(HttpStatus.NOT_FOUND);
		}
		
		FormSubmissionSet set = new FormSubmissionSet();
		for(FormSubmission submission : submissions) {
			set.addSubmission(submission);
		}
		
		return new ResponseEntity<FormSubmissionSet>(set, HttpStatus.OK);
	}
}
