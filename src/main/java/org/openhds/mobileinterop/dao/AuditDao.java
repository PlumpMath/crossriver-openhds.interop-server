package org.openhds.mobileinterop.dao;

import org.openhds.mobileinterop.model.Audit;
import org.springframework.transaction.annotation.Transactional;

public interface AuditDao {
	
	@Transactional
	public Audit addAudit(Audit audit);

}
