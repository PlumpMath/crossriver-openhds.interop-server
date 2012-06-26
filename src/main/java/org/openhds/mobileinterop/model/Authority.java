package org.openhds.mobileinterop.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This class contains the roles for a user of the system
 * It's designed to conform to the requirements for Spring's JDBC
 * user service
 */
@Entity
@Table(name="authorities")
public class Authority implements Serializable {
	
	@EmbeddedId
	AuthorityPK authorityPK;
	
	public static final String SUPERVISOR = "ROLE_SUPERVISOR";
	public static final String ADMIN = "ROLE_ADMIN";

	public AuthorityPK getAuthorityPK() {
		return authorityPK;
	}

	public void setAuthorityPK(AuthorityPK authorityPK) {
		this.authorityPK = authorityPK;
	}
}
