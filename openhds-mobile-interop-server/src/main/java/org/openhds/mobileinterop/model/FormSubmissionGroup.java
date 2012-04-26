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

import org.openhds.mobileinterop.model.FormAction.ActionType;

/**
 * A FormSubmissionGroup is a collection of FormSubmissions
 */
@Entity
public class FormSubmissionGroup {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@OneToMany(mappedBy="group")
	private List<FormSubmission> submissions = new ArrayList<FormSubmission>();
	
	@OneToMany(cascade={CascadeType.ALL}, mappedBy="submissionGroup")
	@OrderBy("actionTime")
	private Set<FormAction> formActions = new HashSet<FormAction>();
	
	private String submissionGroupType;
	
	private boolean completed = false;
	
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
		for(FormSubmission oldSubmisssion : submissions) {
			oldSubmisssion.setActive(false);
		}
		
		submissions.add(submission);
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
	
	public static FormSubmissionGroup startNewGroup(FormSubmission submission) {
		FormSubmissionGroup group = new FormSubmissionGroup();
		group.addSubmission(submission);
		group.setSubmissionGroupType(submission.getFormType());
		
		return group;
	}

	public void completeFormSubmissionGroup(FormSubmission submission) {
		addSubmission(submission);
		setCompleted(true);
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

}