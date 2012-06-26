package org.openhds.mobileinterop.dao;

import java.util.List;

import org.openhds.mobileinterop.model.FormSubmission;
import org.openhds.mobileinterop.model.FormGroup;
import org.openhds.mobileinterop.model.User;
import org.springframework.transaction.annotation.Transactional;

public interface FormDao {

	@Transactional
	public void saveFormSubmission(FormSubmission submission);

	@Transactional
	public List<FormSubmission> findDownloadableSubmissionsForUser(User user);

	@Transactional(readOnly = true)
	public List<FormGroup> findAllFormSubmissions(int pageSize);

	@Transactional(readOnly = true)
	public FormSubmission findFormSubmissionById(long id);

	@Transactional(readOnly = true)
	public FormGroup findFormSubmissionGroupById(long groupId);

	@Transactional
	public void completeFormSubmissionGroup(String completedFormId, FormSubmission submission);
}