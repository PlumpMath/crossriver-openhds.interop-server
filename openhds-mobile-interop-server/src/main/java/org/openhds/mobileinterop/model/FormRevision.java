package org.openhds.mobileinterop.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * A FormGroup is a sequence of form submissions that are related
 * For example, a user might originally download a form with errors
 * and then resubmit it, which in turn causes validation errors. This class
 * captures all related forms over their lifetime
 */
@Entity
public class FormRevision {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String firstRevisionUri;
	
	private String derivedFromUri;
	
	private String uri;

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
}
