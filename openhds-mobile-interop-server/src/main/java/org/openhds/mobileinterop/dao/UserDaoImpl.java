package org.openhds.mobileinterop.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.openhds.mobileinterop.model.Authority;
import org.openhds.mobileinterop.model.AuthorityPK;
import org.openhds.mobileinterop.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao {
	
	private SessionFactory sessionFactory;
	
	@Autowired
	public UserDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllUsers() {
		return (List<User>) getCurrentSession().createCriteria(User.class).list();
	}

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void saveUser(User user, String roleName) {
		getCurrentSession().save(user);
		
		AuthorityPK authorityPK = new AuthorityPK();
		authorityPK.setUsername(user.getUsername());
		authorityPK.setAuthority(roleName);
		
		Authority authority = new Authority();
		authority.setAuthorityPK(authorityPK);
		
		getCurrentSession().save(authority);
	}
}