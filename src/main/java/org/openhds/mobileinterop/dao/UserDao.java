package org.openhds.mobileinterop.dao;

import java.util.List;

import org.openhds.mobileinterop.model.User;

public interface UserDao {

	public List<User> findAllUsers();

	public boolean saveUser(User user, String roleName);
	
	public boolean updateUser(User user);

	public User findUserById(String previousOwner);

	public void deleteUser(String username);
}
