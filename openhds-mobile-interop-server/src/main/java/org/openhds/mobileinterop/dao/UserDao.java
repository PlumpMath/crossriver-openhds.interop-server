package org.openhds.mobileinterop.dao;

import java.util.List;

import org.openhds.mobileinterop.model.User;
import org.springframework.transaction.annotation.Transactional;

public interface UserDao {

	@Transactional(readOnly=true)
	public List<User> getAllUsers();

	@Transactional
	public void saveUser(User user, String roleName);
}