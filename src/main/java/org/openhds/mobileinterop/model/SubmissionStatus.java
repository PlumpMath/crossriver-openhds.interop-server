package org.openhds.mobileinterop.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum SubmissionStatus {
	@XmlEnumValue("Awaiting Supervisor Download")
	AWAITING_SUPERVISOR_DOWNLOAD("Awaiting Supervisor Download"),
	
	@XmlEnumValue("Downloaded by Supervisor")
	DOWNLOAD_BY_SUPERVISOR("Downloaded by Supervisor"),
	
	@XmlEnumValue("Successfully Submitted")
	SUCCESSFULLY_SUBMITTED("Successfully Submitted"),
	
	@XmlEnumValue("Failed Validation")
	FAILED_VALIDATION("Failed Validation");
	
	private final String status;
	
	private SubmissionStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return status;
	}
}
