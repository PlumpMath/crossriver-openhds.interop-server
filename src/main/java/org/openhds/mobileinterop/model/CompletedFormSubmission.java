package org.openhds.mobileinterop.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML Wrapper class so that a client can include a completed id
 * which is the uuid of the processed form submission
 */
@XmlRootElement(name="completedFormSubmission")
public class CompletedFormSubmission {
	
	private FormSubmission formSubmission;
	
	private String completedFormSubmissionId;

	public FormSubmission getFormSubmission() {
		return formSubmission;
	}

	public void setFormSubmission(FormSubmission formSubmission) {
		this.formSubmission = formSubmission;
	}

	public String getCompletedFormSubmissionId() {
		return completedFormSubmissionId;
	}

	public void setCompletedFormSubmissionId(String completedFormSubmissionId) {
		this.completedFormSubmissionId = completedFormSubmissionId;
	}

}
