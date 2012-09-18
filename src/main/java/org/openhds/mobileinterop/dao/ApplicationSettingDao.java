package org.openhds.mobileinterop.dao;

import java.util.List;

import org.openhds.mobileinterop.model.ApplicationSetting;
import org.springframework.transaction.annotation.Transactional;

public interface ApplicationSettingDao {

	public void saveApplicationSetting(String group, String name, String value);

	public String readApplicationSetting(String name, String defaultValue);

	public List<ApplicationSetting> findSettingsForGroup(String groupName);

	public void saveSettings(List<ApplicationSetting> settings);
}
