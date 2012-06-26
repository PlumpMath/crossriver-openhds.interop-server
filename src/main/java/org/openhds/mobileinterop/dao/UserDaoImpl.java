package org.openhds.mobileinterop.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
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
		return (List<User>) getCurrentSession().createCriteria(User.class).add(Restrictions.ne("username", "admin"))
				.list();
	}

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public boolean saveUser(User user, String roleName) {
		user.setEnabled(true);
		getCurrentSession().save(user);

		AuthorityPK authorityPK = new AuthorityPK();
		authorityPK.setUsername(user.getUsername());
		authorityPK.setAuthority(roleName);

		Authority authority = new Authority();
		authority.setAuthorityPK(authorityPK);

		getCurrentSession().save(authority);

		return true;
	}

	@Override
	public User findUserById(String previousOwner) {
		return (User) getCurrentSession().createCriteria(User.class).add(Restrictions.eq("username", previousOwner))
				.uniqueResult();
	}

	@Override
	public boolean updateUser(User user) {
		User savedUser = findUserById(user.getUsername());
		if (savedUser == null) {
			return false;
		}

		savedUser.setUsername(user.getUsername());
		savedUser.setPassword(user.getPassword());
		savedUser.getManagedFieldworkers().clear();
		savedUser.getManagedFieldworkers().addAll(user.getManagedFieldworkers());

		return true;
	}

	@Override
	public void deleteUser(String username) {
		User user = findUserById(username);
		getCurrentSession().delete(user);
	}

}
