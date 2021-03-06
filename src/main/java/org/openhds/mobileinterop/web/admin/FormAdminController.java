package org.openhds.mobileinterop.web.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
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
import org.openhds.mobileinterop.dao.FormDao.GroupFilter;
import org.openhds.mobileinterop.model.FormGroup;
import org.openhds.mobileinterop.model.FormSubmission;
import org.openhds.mobileinterop.model.Pager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	private static final int PAGE_ITEM_COUNT = 50;
	private static final String INFO_MSG_ATTRIBUTE = "infoMsg";
	private static final Logger logger = LoggerFactory.getLogger(FormAdminController.class);
	private static final List<String> FORM_STATUS_VALUES = Arrays.asList("1", "2", "3", "4");

	private final FormDao dao;
	private final FormTypeConverter converter;
	private final ApplicationSettingDao appSettingDao;
	private final Map<String, String> filterStatusValues;

	@Autowired
	public FormAdminController(FormDao dao, FormTypeConverter converter, ApplicationSettingDao appSettingDao) {
		this.dao = dao;
		this.converter = converter;
		this.appSettingDao = appSettingDao;
		filterStatusValues = new HashMap<String, String>();
		filterStatusValues.put("1", "any");
		filterStatusValues.put("2", "completed");
		filterStatusValues.put("3", "active");
		filterStatusValues.put("4", "errors");
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public ModelAndView searchForFormById(@RequestParam(defaultValue = "0") String formId, HttpSession session) {
		// ideally we don't use HttpSession
		// but prior to Spring 3.1, RedirectView puts all model attributes into the URL
	    session.removeAttribute("currentFilter");
		long parsedFormId;
		ModelAndView mv = new ModelAndView();
		try {
			parsedFormId = Long.parseLong(formId);
		} catch (NumberFormatException e) {
			session.setAttribute(INFO_MSG_ATTRIBUTE, "You must provide the form id as a number, e.g. 105");
			mv.setViewName("redirect:list");
			return mv;
		}

		FormSubmission submission = dao.findFormSubmissionById(parsedFormId);
		if (submission == null) {
			session.setAttribute(INFO_MSG_ATTRIBUTE, "There was no form found with id: " + formId);
			mv.setViewName("redirect:list");
			return mv;
		}

		long groupId = submission.getGroup().getId();
		return new ModelAndView("redirect:group/" + groupId);
	}
	
	@RequestMapping(value = "/group/{groupId}")
	public ModelAndView viewFormSubmission(@PathVariable long groupId, HttpSession session) {
	    GroupFilterFormatter currentFilter = (GroupFilterFormatter) session.getAttribute("currentFilter");
	    if (currentFilter != null) {
		    session.removeAttribute("currentFilter");
		}
	    return buildSubmissionGroupView(groupId, currentFilter);
	}

	private ModelAndView buildSubmissionGroupView(long groupId, GroupFilterFormatter formatter) {
		FormGroup submissionGroup = dao.findFormSubmissionGroupById(groupId);
		ModelAndView mv = new ModelAndView("viewSubmissionGroup");
		mv.addObject("group", submissionGroup);
		mv.addObject("currentFilter", formatter);
		if (submissionGroup.getCompletedFormId() != null) {
			mv.addObject("openhdsUrl", converter.getOpenHdsUrlForType(
					appSettingDao.readApplicationSetting("openhdsUrl", "http://openhds.rcg.usm.maine.edu/openhds"),
					submissionGroup.getSubmissionGroupType(), submissionGroup.getCompletedFormId()));
		}

		mv.addObject("revisions", calculateDiffs(submissionGroup));
		return mv;
	}

	@RequestMapping(value = "/group/{groupId}", method = RequestMethod.POST)
	public ModelAndView updateSubmissionGroup(@PathVariable long groupId,
			@RequestBody MultiValueMap<String, String> formValues) {
		if (StringUtils.isNotEmpty(formValues.getFirst("voided"))) {
			dao.voidGroup(groupId);
		} else if (StringUtils.isNotEmpty(formValues.getFirst("deleted"))) {
			dao.deleteGroup(groupId);
			return new ModelAndView("redirect:/admin");
		}
		return buildSubmissionGroupView(groupId, null);
	}

	private List<Revision> calculateDiffs(FormGroup group) {
		if (!group.hasMultipleSubmissions()) {
			return null;
		}
		List<Revision> revisions = new ArrayList<Revision>();

		FormSubmission firstRevision = group.getFirstRevision();
		FormSubmission nextRevision;
		while ((nextRevision = group.nextRevisionFrom(firstRevision.getOdkUri())) != null) {
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
			for (Delta d : deltas) {
				Diff diff = new Diff();
				diff.originalLines = (List<String>) d.getOriginal().getLines();
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

			while ((line = reader1.readLine()) != null) {
				lines.add(HtmlUtils.htmlEscape(line));
			}
		} catch (IOException e) {
		}

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

	@RequestMapping(value = "/group/{groupId}/submission/{submissionId}", method = RequestMethod.POST)
	public ModelAndView updateFormSubmission(@PathVariable long groupId, @PathVariable long submissionId,
			@RequestBody MultiValueMap<String, String> formValues) {
		if (StringUtils.isNotEmpty(formValues.getFirst("formOwnerId"))) {
			dao.updateOwnerIdForSubmission(submissionId, formValues.getFirst("formOwnerId"));
		}

		return viewFormSubmission(groupId, submissionId);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView getFormSubmissionList(HttpSession session) {
		Object infoMsg = session.getAttribute(INFO_MSG_ATTRIBUTE);
		session.removeAttribute(INFO_MSG_ATTRIBUTE);
		ModelAndView view = getFormSubmissionListOnPage(1);
		if (infoMsg != null) {
			view.addObject(INFO_MSG_ATTRIBUTE, infoMsg.toString());
		}
		return view;
	}
	
	@RequestMapping(value = "/list/{pageNum}", method = RequestMethod.GET)
	public ModelAndView getFormSubmissionListOnPage(@PathVariable int pageNum) {
		long totalCnt = dao.getFormGroupCount();
		if (totalCnt == 0) {
			return new ModelAndView("viewFormSubmissions");
		}

		Pager pager = new Pager(PAGE_ITEM_COUNT, totalCnt);
		if (!pager.isPageInRange(pageNum)) {
			return new ModelAndView("redirect:/admin/form/list");
		}

		int startItem = pager.getStartItemForPage(pageNum);

		ModelAndView mv = new ModelAndView("viewFormSubmissions");

		if (pageNum > 1) {
			mv.addObject("previousPage", pageNum - 1);
		}

		if ((pageNum + 1) <= pager.getNumberOfPages()) {
			mv.addObject("nextPage", pageNum + 1);
		}

		List<FormGroup> group = dao.findAllFormSubmissions(startItem, PAGE_ITEM_COUNT);

		mv.addObject("startItem", startItem + 1);
		mv.addObject("totalCnt", totalCnt);
		mv.addObject("groupCnt", startItem + group.size());
		mv.addObject("submissions", group);
		return mv;
	}
	
	@RequestMapping("/filter/{pageNum}")
	public ModelAndView filterSubmissions(@PathVariable int pageNum, HttpSession session,
			@RequestParam(defaultValue = "") String submitButton,
			@RequestParam(defaultValue = "0") String filterStatus, @RequestParam(defaultValue = "") String formType) {
		if ("clear".equalsIgnoreCase(submitButton)) {
			return new ModelAndView("redirect:/admin/form/list");
		}
		GroupFilter filter = new GroupFilter();
		if (!FORM_STATUS_VALUES.contains(filterStatus)) {
			filterStatus = "1";
		}
		
		filter.setFormStatus(filterStatusValues.get(filterStatus));
		
		if (StringUtils.isNotBlank(formType) && !"all".equalsIgnoreCase(formType)) {
			filter.setFormType(formType);
		}
		
		long totalCnt = dao.findFilterFormGroupsCount(filter);
        if (totalCnt == 0) {
            ModelAndView mv = new ModelAndView("viewFormSubmissions");
            mv.addObject("filterStatus", filterStatus);
            mv.addObject("formType", formType);

            return mv;
        }
		
		Pager pager = new Pager(PAGE_ITEM_COUNT, totalCnt);
		if (!pager.isPageInRange(pageNum)) {
			return new ModelAndView("redirect:/admin/form/list");
		}
		
		int startItem = pager.getStartItemForPage(pageNum);
		filter.setStartItem(startItem);
		filter.setPageSize(PAGE_ITEM_COUNT);
		List<FormGroup> group = dao.findAllFormGroups(filter);
		
		ModelAndView mv = new ModelAndView("viewFormSubmissions");
		mv.addObject("startItem", startItem + 1);
		mv.addObject("totalCnt", totalCnt);
		mv.addObject("filterStatus", filterStatus);
		mv.addObject("formType", formType);
		mv.addObject("groupCnt", startItem + group.size());
		if (pageNum > 1) {
			mv.addObject("previousPage", pageNum - 1);
		}

		if ((pageNum + 1) <= pager.getNumberOfPages()) {
			mv.addObject("nextPage", pageNum + 1);
		}
		
		mv.addObject("submissions", group);
		GroupFilterFormatter formatter = new GroupFilterFormatter(pageNum, filterStatus, formType);
		session.setAttribute("currentFilter", formatter);
		mv.addObject("filter", formatter);
		return mv;
	}
	
	public static class GroupFilterFormatter implements Serializable {

		private String filterStatus;
		private String formType;
        private int pageNum;

		public GroupFilterFormatter(int pageNum, String filterStatus, String formType) {
			this.pageNum = pageNum;
		    this.filterStatus = filterStatus;
			this.formType = formType;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder("?");
			builder.append("filterStatus=" + filterStatus);
			builder.append("&formType=" + formType);
			return builder.toString();
		}
		
		public String toStringWithPageNum() {
		    return pageNum + toString();
		}
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
