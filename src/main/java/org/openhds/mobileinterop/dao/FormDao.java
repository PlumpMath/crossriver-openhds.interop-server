package org.openhds.mobileinterop.dao;

import java.util.List;

import org.openhds.mobileinterop.model.FormSubmission;
import org.openhds.mobileinterop.model.FormGroup;
import org.openhds.mobileinterop.model.User;
import org.springframework.transaction.annotation.Transactional;

public interface FormDao {

	public void saveFormSubmission(FormSubmission submission);

	public List<FormSubmission> findDownloadableSubmissionsForUser(User user);

	public List<FormGroup> findAllFormSubmissions(int startItem, int pageSize);

	public FormSubmission findFormSubmissionById(long id);

	public FormGroup findFormSubmissionGroupById(long groupId);

	public void completeFormSubmissionGroup(String completedFormId, FormSubmission submission);

	public void voidGroup(long groupId);

	public void updateOwnerIdForSubmission(long submissionId, String owner);

	public void deleteGroup(long groupId);
	
	long getFormGroupCount();
}
