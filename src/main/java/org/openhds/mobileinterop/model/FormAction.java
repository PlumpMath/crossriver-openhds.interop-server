package org.openhds.mobileinterop.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * A FormAction is a recordable act done to a {@link FormSubmission} 
 */
@Entity
public class FormAction {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	@Enumerated(EnumType.STRING)
	private ActionType actionType;
	
	private Calendar actionTime;
	
	@ManyToOne
	private FormGroup submissionGroup;
	
	@ManyToOne
	private FormSubmission submission;
	
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	public enum ActionType {
		UPLOADED("Uploaded"), DOWNLOADED("Downloaded"), COMPLETED("Completed");
		
		private final String status;
		
		private ActionType(String status) {
			this.status = status;
		}
		
		@Override
		public String toString() {
			return status;
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public Calendar getActionTime() {
		return actionTime;
	}

	public void setActionTime(Calendar actionTime) {
		this.actionTime = actionTime;
	}

	public FormGroup getSubmissionGroup() {
		return submissionGroup;
	}

	public void setSubmissionGroup(FormGroup submissionGroup) {
		this.submissionGroup = submissionGroup;
	}

	public FormSubmission getSubmission() {
		return submission;
	}

	public void setSubmission(FormSubmission submission) {
		this.submission = submission;
	}
	
	public String getFormattedDate() {
		return format.format(actionTime.getTime());
	}
}
