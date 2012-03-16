package org.openhds.mobileinterop.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Represents a form submission from a mobile device
 */
@Entity
@XmlRootElement(name="formSubmission")
public class FormSubmission {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@OneToMany(mappedBy="submission")
	private Set<FormError> formErrors = new HashSet<FormError>();
	
	private String formType;
	
	@Column(length=50)
	private String formOwnerId;
	
	@Lob
	@Column(nullable=false)
	private String formInstanceXml;
	
	@Enumerated(EnumType.STRING)
	private SubmissionStatus submissionStatus = SubmissionStatus.SUBMITTED;
	
	@XmlEnum
	public enum SubmissionStatus {
		@XmlEnumValue("submitted")
		SUBMITTED("submitted"),
		
		@XmlEnumValue("downloaded")
		DOWNLOADED("downloaded"),
		
		@XmlEnumValue("inactive")
		INACTIVE("inactive");
		
		private final String status;
		
		private SubmissionStatus(String status) {
			this.status = status;
		}
		
		@Override
		public String toString() {
			return status;
		}
	}

	@XmlTransient
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@XmlElementWrapper(name="formErrors")
	@XmlElement(name="formError")
	public Set<FormError> getFormErrors() {
		return formErrors;
	}

	public void setFormErrors(Set<FormError> formErrors) {
		this.formErrors = formErrors;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public String getFormInstanceXml() {
		return formInstanceXml;
	}

	public void setFormInstanceXml(String formInstanceXml) {
		this.formInstanceXml = formInstanceXml;
	}

	public String getFormOwnerId() {
		return formOwnerId;
	}

	public void setFormOwnerId(String formOwnerId) {
		this.formOwnerId = formOwnerId;
	}

	@XmlTransient
	public SubmissionStatus getSubmissionStatus() {
		return submissionStatus;
	}

	public void setSubmissionStatus(SubmissionStatus submissionStatus) {
		this.submissionStatus = submissionStatus;
	}
	
	@Override
	public String toString() {
		return "FormSubmission [id=" + id + ", formType=" + formType + 
				", formOwnerId=" + formOwnerId + ", submissionStatus=" + submissionStatus +
				", errors=" + formErrors.toString() + "]";
	}
}
