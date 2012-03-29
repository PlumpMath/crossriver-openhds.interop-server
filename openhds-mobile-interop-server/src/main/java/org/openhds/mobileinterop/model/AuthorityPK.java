package org.openhds.mobileinterop.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class AuthorityPK implements Serializable {

	private String username;
	
	private String authority;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}
}
