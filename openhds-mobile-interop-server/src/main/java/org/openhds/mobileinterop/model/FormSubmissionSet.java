package org.openhds.mobileinterop.model;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="formSubmissionSet")
public class FormSubmissionSet {

	private Set<FormSubmission> submissions = new HashSet<FormSubmission>();
	
	public void addSubmission(FormSubmission submission) {
		submissions.add(submission);
	}

	@XmlElementWrapper(name="submissions")
	@XmlElement(name="formSubmission")
	public Set<FormSubmission> getSubmissions() {
		return submissions;
	}

}
