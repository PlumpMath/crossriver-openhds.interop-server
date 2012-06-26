package org.openhds.mobileinterop.dao;

import org.hibernate.SessionFactory;
import org.openhds.mobileinterop.model.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AuditDaoImpl implements AuditDao {
	
	private final SessionFactory factory;

	@Autowired
	public AuditDaoImpl(SessionFactory factory) {
		this.factory = factory;
	}

	@Override
	public Audit addAudit(Audit audit) {
		factory.getCurrentSession().save(audit);
		return audit;
	}

}
