package org.openhds.mobileinterop.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * A FormRevision represents the linkage between form submissions
 * A {@link FormSubmission} can be downloaded a number of times to a phone
 */
@Entity
public class FormRevision {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String firstRevisionUri;
	
	private String derivedFromUri;
	
	private String uri;
	
	public FormRevision() { }
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstRevisionUri() {
		return firstRevisionUri;
	}

	public void setFirstRevisionUri(String firstRevisionUri) {
		this.firstRevisionUri = firstRevisionUri;
	}

	public String getDerivedFromUri() {
		return derivedFromUri;
	}

	public void setDerivedFromUri(String derivedFromUri) {
		this.derivedFromUri = derivedFromUri;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void asFirstRevision(FormSubmission submission) {
		firstRevisionUri = submission.getOdkUri();
		uri = submission.getOdkUri();		
	}	
	
	public void asSiblingRevision(FormRevision previousRevision, String uri) {
		firstRevisionUri = previousRevision.firstRevisionUri;
		derivedFromUri = previousRevision.derivedFromUri;
		this.uri = uri;
		
	}
}
