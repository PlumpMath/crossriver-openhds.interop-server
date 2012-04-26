package org.openhds.mobileinterop.web.admin;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.openhds.mobileinterop.dao.FormDao;
import org.openhds.mobileinterop.model.FormSubmission;
import org.openhds.mobileinterop.model.FormSubmissionGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

/**
 * Handles administrative GUI tasks relating to form submissions
 */
@Controller
@RequestMapping("/admin/form")
public class FormAdminController {
	private final static Logger logger = LoggerFactory
			.getLogger(FormAdminController.class);

	private FormDao dao;

	@Autowired
	public FormAdminController(FormDao dao) {
		this.dao = dao;
	}
	
	@RequestMapping(value = "/group/{groupId}")
	public ModelAndView viewFormSubmission(@PathVariable long groupId) {
		FormSubmissionGroup submissionGroup = dao.findFormSubmissionGroupById(groupId);
		ModelAndView mv = new ModelAndView("viewSubmissionGroup");
		mv.addObject("group", submissionGroup);
		return mv;
	}

	@RequestMapping(value = "/group/{groupId}/submission/{submissionId}")
	public ModelAndView viewFormSubmission(@PathVariable long groupId, @PathVariable long submissionId) {
		FormSubmission submission = dao.findFormSubmissionById(submissionId);
		ModelAndView mv = new ModelAndView("viewSubmission");
		mv.addObject("form", submission);
		mv.addObject("group", groupId);
		String prettyXml = prettyPrintXml(submission.getFormInstanceXml());
		mv.addObject("instanceXml", HtmlUtils.htmlEscape(prettyXml));
		return mv;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView getFormSubmissionList() {
		ModelAndView mv = new ModelAndView("viewFormSubmissions");
		mv.addObject("submissions", dao.findAllFormSubmissions(50));
		return mv;
	}

	public String prettyPrintXml(String xml) {
		Source xmlInput = new StreamSource(new StringReader(xml));
		StreamResult xmlOutput = new StreamResult(new StringWriter());

		// Configure transformer
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(xmlInput, xmlOutput);
		} catch (TransformerConfigurationException e) {
		} catch (TransformerFactoryConfigurationError e) {
		} catch (TransformerException e) {
		}

		return xmlOutput.getWriter().toString();
	}
}
