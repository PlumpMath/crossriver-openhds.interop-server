package org.openhds.mobileinterop.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * A <code>FormUser</code> is a user that is capable of downloading submitted
 * form submissions
 */
@Entity
public class FormUser {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column(length=20)
	private String userName;
	
	@Column(length=20)
	private String password;
	
	@ElementCollection
	List<String> managedIds = new ArrayList<String>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getManagedIds() {
		return managedIds;
	}

	public void setManagedIds(List<String> managedIds) {
		this.managedIds = managedIds;
	}
}
