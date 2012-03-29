package org.openhds.mobileinterop.dao;

import java.util.List;

import org.openhds.mobileinterop.model.FormSubmission;
import org.springframework.transaction.annotation.Transactional;

public interface FormSubmissionDao {
	
	@Transactional
	public void saveFormSubmission(FormSubmission submission);
	
	@Transactional(readOnly=true)
	public List<FormSubmission> findSubmissionsByOwner(String ownerId);

	@Transactional(readOnly=true)
	public List<FormSubmission> findAllFormSubmissions(int pageSize);

	@Transactional(readOnly=true)
	public FormSubmission findFormSubmissionById(long id);
}
