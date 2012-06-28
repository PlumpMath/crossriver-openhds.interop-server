package org.openhds.mobileinterop.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.apache.commons.lang.StringUtils;
import org.openhds.mobileinterop.model.FormAction.ActionType;

/**
 * A FormGroup is container for a collection of FormSubmissions
 */
@Entity
public class FormGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "group")
	private List<FormSubmission> submissions = new ArrayList<FormSubmission>();

	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "submissionGroup")
	@OrderBy("actionTime")
	private Set<FormAction> formActions = new HashSet<FormAction>();

	private String submissionGroupType;

	private boolean completed = false;
	
	private String completedFormId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<FormSubmission> getSubmissions() {
		return submissions;
	}

	public void setSubmissions(List<FormSubmission> submissions) {
		this.submissions = submissions;
	}

	public void addSubmission(FormSubmission submission) {
		for (FormSubmission oldSubmisssion : submissions) {
			oldSubmisssion.setActive(false);
		}

		submissions.add(submission);
		submission.setGroup(this);
		addUploadAction(submission);
	}

	public Set<FormAction> getFormActions() {
		return formActions;
	}

	public void setFormActions(Set<FormAction> formActions) {
		this.formActions = formActions;
	}

	public String getSubmissionGroupType() {
		return submissionGroupType;
	}

	public void setSubmissionGroupType(String submissionGroupType) {
		this.submissionGroupType = submissionGroupType;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public void addUploadAction(FormSubmission submission) {
		FormAction action = new FormAction();
		action.setActionTime(Calendar.getInstance());
		action.setActionType(ActionType.UPLOADED);
		action.setSubmissionGroup(this);
		action.setSubmission(submission);
		formActions.add(action);
	}

	public static FormGroup startNewGroup(FormSubmission submission) {
		FormGroup group = new FormGroup();
		group.addSubmission(submission);
		group.setSubmissionGroupType(submission.getFormType());
		submission.setGroup(group);
		return group;
	}

	public void completeFormSubmissionGroup(FormSubmission submission) {
		addSubmission(submission);
		setCompleted(true);
		submission.setGroup(this);
		submission.setActive(false);
		addCompleteAction(submission);
	}

	private void addCompleteAction(FormSubmission submission) {
		FormAction action = new FormAction();
		action.setActionTime(Calendar.getInstance());
		action.setActionType(ActionType.COMPLETED);
		action.setSubmissionGroup(this);
		action.setSubmission(submission);
		formActions.add(action);
	}

	public void addDownloadAction(FormSubmission formSubmission) {
		FormAction action = new FormAction();
		action.setActionTime(Calendar.getInstance());
		action.setActionType(ActionType.DOWNLOADED);
		action.setSubmissionGroup(this);
		action.setSubmission(formSubmission);
		formActions.add(action);
	}

	public String getCompletedFormId() {
		return completedFormId;
	}

	public void setCompletedFormId(String completedFormId) {
		this.completedFormId = completedFormId;
	}

	public boolean hasMultipleSubmissions() {
		return submissions.size() > 1;
	}

	public FormSubmission getFirstRevision() {
		for (FormSubmission sub : submissions) {
			if (StringUtils.isEmpty(sub.getDerivedFromUri())) {
				return sub;
			}
		}
		
		return null;
	}

	public FormSubmission nextRevisionFrom(String odkUri) {
		for (FormSubmission sub : submissions) {
			if (sub.getDerivedFromUri().equals(odkUri)) {
				return sub;
			}
		}
		
		return null;
	}

	public void voidGroup() {
		completed = true;
		for(FormSubmission submission : getSubmissions()) {
			if (submission.isActive()) {
				submission.setActive(false);
				break;
			}
		}		
	}
}