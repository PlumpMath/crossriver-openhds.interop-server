package org.openhds.mobileinterop.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@XmlRootElement(name="formError")
public class FormError {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@ManyToOne(optional=false)
	private FormSubmission submission;

	@Lob
	@Column(nullable=false)
	private String error;
	
	public FormError() {}

	public FormError(String error) {
		this.error = error;
	}

	@XmlTransient
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@XmlTransient
	public FormSubmission getSubmission() {
		return submission;
	}

	public void setSubmission(FormSubmission submission) {
		this.submission = submission;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	@Override public String toString() {
		return "FormError[error=" + error + "]";
	}
}
