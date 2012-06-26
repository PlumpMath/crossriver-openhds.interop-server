package org.openhds.mobileinterop.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openhds.mobileinterop.model.ApplicationSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationSettingDaoImpl implements ApplicationSettingDao {

	private final SessionFactory factory;

	@Autowired
	public ApplicationSettingDaoImpl(SessionFactory factory) {
		this.factory = factory;
	}

	@Override
	public void saveApplicationSetting(String group, String name, String value) {
		ApplicationSetting setting = getPersistedSetting(name);
		if (setting == null) {
			setting = new ApplicationSetting();
			setting.setGroupName(group);
			setting.setName(name);
			setting.setValue(value);
			factory.getCurrentSession().save(setting);
		} else {
			setting.setGroupName(group);
			setting.setValue(value);
		}
	}

	private ApplicationSetting getPersistedSetting(String name) {
		ApplicationSetting setting = (ApplicationSetting) factory
				.getCurrentSession().createCriteria(ApplicationSetting.class)
				.add(Restrictions.eq("name", name)).uniqueResult();
		return setting;
	}

	@Override
	public String readApplicationSetting(String name, String defaultValue) {
		ApplicationSetting setting = getPersistedSetting(name);
		if (setting == null) {
			return defaultValue;
		}

		return setting.getValue();
	}

	@Override
	public List<ApplicationSetting> findSettingsForGroup(String groupName) {
		List<ApplicationSetting> settings = (List<ApplicationSetting>) factory
				.getCurrentSession().createCriteria(ApplicationSetting.class)
				.add(Restrictions.eq("groupName", groupName)).list();
		return settings;
	}

	@Override
	public void saveSettings(List<ApplicationSetting> settings) {
		for(ApplicationSetting set : settings) {
			saveApplicationSetting(set.getGroupName(), set.getName(), set.getValue());
		}
	}

}
