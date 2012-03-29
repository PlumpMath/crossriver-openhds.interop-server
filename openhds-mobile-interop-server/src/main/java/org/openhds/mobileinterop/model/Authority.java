package org.openhds.mobileinterop.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;



@Entity
@Table(name="authorities")
public class Authority implements Serializable {
	
	@EmbeddedId
	AuthorityPK authorityPK;
	
	public static final String FIELD_WORKER = "ROLE_FIELDWORKER";
	public static final String ADMIN = "ROLE_ADMIN";

	public AuthorityPK getAuthorityPK() {
		return authorityPK;
	}

	public void setAuthorityPK(AuthorityPK authorityPK) {
		this.authorityPK = authorityPK;
	}
}
