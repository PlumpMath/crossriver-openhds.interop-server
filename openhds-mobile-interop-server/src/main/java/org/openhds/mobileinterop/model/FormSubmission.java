package org.openhds.mobileinterop.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
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
	
	@OneToMany(cascade={CascadeType.ALL}, mappedBy="submission")
	private Set<FormError> formErrors = new HashSet<FormError>();
	
	private String formType;
	
	private String odkUri;
	
	private String formId;
	
	private String derivedFromUri;
	
	@Column(length=50)
	private String formOwnerId;
	
	@Lob
	@Column(nullable=false)
	private String formInstanceXml;
	
	@ManyToOne
	private FormSubmissionGroup group;
	
	private boolean active = true;
	
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

	public String getOdkUri() {
		return odkUri;
	}

	public void setOdkUri(String odkUri) {
		this.odkUri = odkUri;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getDerivedFromUri() {
		return derivedFromUri;
	}

	public void setDerivedFromUri(String derivedFromUri) {
		this.derivedFromUri = derivedFromUri;
	}

	@Override
	public String toString() {
		return "FormSubmission [id=" + id + ", formType=" + formType + 
				", formOwnerId=" + formOwnerId + 
				", errors=" + formErrors.toString() + "]";
	}

	public boolean hasDerivedUri() {
		return derivedFromUri != null && !derivedFromUri.trim().isEmpty();
	}

	@XmlTransient
	public FormSubmissionGroup getGroup() {
		return group;
	}

	public void setGroup(FormSubmissionGroup group) {
		this.group = group;
	}

	@XmlTransient
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void addDownloadActionToGroup() {
		group.addDownloadAction(this);
	}

}
