package org.openhds.mobileinterop.dao;

import java.util.List;

import org.openhds.mobileinterop.model.FormGroup;
import org.openhds.mobileinterop.model.FormSubmission;
import org.openhds.mobileinterop.model.User;

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
	
	long findFilterFormGroupsCount(GroupFilter filter);
	
	List<FormGroup> findAllFormGroups(GroupFilter filter);
	
	public static class GroupFilter {
		
		private int startItem;
		private int pageSize;
		private String formType;
		private String formStatus;

		public void setStartItem(int startItem) {
			this.startItem = startItem;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}

		public int getStartItem() {
			return startItem;
		}

		public int getPageSize() {
			return pageSize;
		}

		public boolean hasFormType() {
			return formType != null && !formType.trim().isEmpty();
		}

		public String getFormType() {
			return formType;
		}

		public void setFormType(String formType) {
			this.formType = formType;
		}
		
		public String getFormStatus() {
			return formStatus;
		}

		public void setFormStatus(String formStatus) {
			this.formStatus = formStatus;
		}
	}
}
