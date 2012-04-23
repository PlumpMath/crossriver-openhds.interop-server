package org.openhds.mobileinterop.model;

public class DatabaseOptions {
	private String databaseUsername;
	private String databasePassword;
	private String databaseName;
	private String createDatabase;
	
	public String getDatabaseUsername() {
		return databaseUsername;
	}
	public void setDatabaseUsername(String databaseUsername) {
		this.databaseUsername = databaseUsername;
	}
	public String getDatabasePassword() {
		return databasePassword;
	}
	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getCreateDatabase() {
		return createDatabase;
	}
	public void setCreateDatabase(String createDatabase) {
		this.createDatabase = createDatabase;
	}
}
