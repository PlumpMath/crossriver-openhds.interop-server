package org.openhds.mobileinterop.dao;

import java.util.List;

import org.openhds.mobileinterop.model.User;
import org.springframework.transaction.annotation.Transactional;

public interface UserDao {

	@Transactional(readOnly = true)
	public List<User> getAllUsers();

	@Transactional
	public boolean saveUser(User user, String roleName);
	
	@Transactional
	public boolean updateUser(User user);

	@Transactional(readOnly = true)
	public User findUserById(String previousOwner);

	@Transactional
	public void deleteUser(String username);
}
