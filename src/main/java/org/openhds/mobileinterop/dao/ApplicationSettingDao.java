package org.openhds.mobileinterop.dao;

import java.util.List;

import org.openhds.mobileinterop.model.ApplicationSetting;
import org.springframework.transaction.annotation.Transactional;

public interface ApplicationSettingDao {

	@Transactional
	public void saveApplicationSetting(String group, String name, String value);
	
	@Transactional(readOnly=true)
	public String readApplicationSetting(String name, String defaultValue);
	
	@Transactional(readOnly=true)
	public List<ApplicationSetting> findSettingsForGroup(String groupName);

	@Transactional
	public void saveSettings(List<ApplicationSetting> settings); 
}
