package org.openhds.mobileinterop.web.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.openhds.mobileinterop.FormTypeConverter;
import org.openhds.mobileinterop.dao.ApplicationSettingDao;
import org.openhds.mobileinterop.dao.FormDao;
import org.openhds.mobileinterop.model.FormGroup;
import org.openhds.mobileinterop.model.FormSubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * Handles administrative GUI tasks relating to form submissions
 */
@Controller
@RequestMapping("/admin/form")
public class FormAdminController {
	private final static Logger logger = LoggerFactory.getLogger(FormAdminController.class);

	private FormDao dao;
	private FormTypeConverter converter;
	private ApplicationSettingDao appSettingDao;

	@Autowired
	public FormAdminController(FormDao dao, FormTypeConverter converter, ApplicationSettingDao appSettingDao) {
		this.dao = dao;
		this.converter = converter;
		this.appSettingDao = appSettingDao;
	}

	@RequestMapping(value = "/group/{groupId}")
	public ModelAndView viewFormSubmission(@PathVariable long groupId) {
		return buildSubmissionGroupView(groupId);
	}

	private ModelAndView buildSubmissionGroupView(long groupId) {
		FormGroup submissionGroup = dao.findFormSubmissionGroupById(groupId);
		ModelAndView mv = new ModelAndView("viewSubmissionGroup");
		mv.addObject("group", submissionGroup);
		if (submissionGroup.getCompletedFormId() != null) {
			mv.addObject(
					"openhdsUrl",
					converter.getOpenHdsUrlForType(appSettingDao.readApplicationSetting("openhdsUrl", "http://openhds.rcg.usm.maine.edu/openhds"), 
							submissionGroup.getSubmissionGroupType(),
							submissionGroup.getCompletedFormId()));
		}
		
		mv.addObject("revisions", calculateDiffs(submissionGroup));
		return mv;
	}
	
	@RequestMapping(value = "/group/{groupId}", method=RequestMethod.POST)
	public ModelAndView updateSubmissionGroup(@PathVariable long groupId, @RequestBody MultiValueMap<String, String> formValues) {
		if (StringUtils.isNotEmpty(formValues.getFirst("voided"))) {
			dao.voidGroup(groupId);
		}
		return buildSubmissionGroupView(groupId);
	}

	private List<Revision> calculateDiffs(FormGroup group) {
		if (!group.hasMultipleSubmissions()) {
			return null;
		}
		List<Revision> revisions = new ArrayList<Revision>();
		
		FormSubmission firstRevision = group.getFirstRevision();
		FormSubmission nextRevision;
		while((nextRevision = group.nextRevisionFrom(firstRevision.getOdkUri())) != null) {
			Revision revision = new Revision(firstRevision.getId(), nextRevision.getId());
			String pretty1 = prettyPrintXml(firstRevision.getFormInstanceXml());
			String pretty2 = prettyPrintXml(nextRevision.getFormInstanceXml());
			BufferedReader reader1 = new BufferedReader(new StringReader(pretty1));
			BufferedReader reader2 = new BufferedReader(new StringReader(pretty2));
			
			List<String> lines1 = getLines(reader1);
			List<String> lines2 = getLines(reader2);
			Patch patch = DiffUtils.diff(lines1, lines2);
			
			List<Delta> deltas = patch.getDeltas();
			List<Diff> diffs = new ArrayList<Diff>();
			for(Delta d : deltas) {
				Diff diff = new Diff();
				diff.originalLines = (List<String>)d.getOriginal().getLines();
				diff.revisedLines = (List<String>) d.getRevised().getLines();
				diffs.add(diff);
			}
			
			revision.setDiffs(diffs);
			firstRevision = nextRevision;
			revisions.add(revision);
		}
		
		return revisions;
	}	
	
	private List<String> getLines(BufferedReader reader1) {
		List<String> lines = new ArrayList<String>();
		try {
			String line = null;

			while((line = reader1.readLine()) != null) {
				lines.add(HtmlUtils.htmlEscape(line));
			}
		} catch (IOException e) { }
		
		return lines;
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
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(xmlInput, xmlOutput);
		} catch (TransformerConfigurationException e) {
		} catch (TransformerFactoryConfigurationError e) {
		} catch (TransformerException e) {
		}
		return xmlOutput.getWriter().toString();
	}
	
	public static class Revision {
		List<Diff> diffs;
		private String first;
		private String second;
		
		public Revision(long id1, long id2) {
			this.first = "Form " + id1;
			this.second = "Form " + id2;
		}

		public List<Diff> getDiffs() {
			return diffs;
		}

		public void setDiffs(List<Diff> diffs) {
			this.diffs = diffs;
		}

		public String getFirst() {
			return first;
		}

		public void setFirst(String first) {
			this.first = first;
		}

		public String getSecond() {
			return second;
		}

		public void setSecond(String second) {
			this.second = second;
		}
	}
	
	public static class Diff {
		List<String> originalLines;
		List<String> revisedLines;
		
		public List<String> getOriginalLines() {
			return originalLines;
		}
		public void setOriginalLines(List<String> originalLines) {
			this.originalLines = originalLines;
		}
		public List<String> getRevisedLines() {
			return revisedLines;
		}
		public void setRevisedLines(List<String> revisedLines) {
			this.revisedLines = revisedLines;
		}
		
	}
}
