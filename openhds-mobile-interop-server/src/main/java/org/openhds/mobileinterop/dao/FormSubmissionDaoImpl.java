package org.openhds.mobileinterop.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openhds.mobileinterop.model.FormError;
import org.openhds.mobileinterop.model.FormSubmission;
import org.openhds.mobileinterop.model.FormSubmission.SubmissionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FormSubmissionDaoImpl implements FormSubmissionDao {

	private SessionFactory sessionFactory;

	@Autowired
	public FormSubmissionDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void saveFormSubmission(FormSubmission submission) {
		save(submission);

		for (FormError error : submission.getFormErrors()) {
			error.setSubmission(submission);
			save(error);
		}
	}

	private void save(Object obj) {
		getCurrentSession().save(obj);
	}

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	public List<FormSubmission> findSubmissionsByOwner(String ownerId) {
		return findListSubmissionByOwnerId(ownerId);
	}

	@SuppressWarnings("unchecked")
	private List<FormSubmission> findListSubmissionByOwnerId(String ownerId) {
		return (List<FormSubmission>) getCurrentSession()
				.createCriteria(FormSubmission.class)
				.add(Restrictions.eq("formOwnerId", ownerId))
				.add(Restrictions.eq("submissionStatus",
						SubmissionStatus.SUBMITTED)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FormSubmission> findAllFormSubmissions(int pageSize) {
		return (List<FormSubmission>) getCurrentSession()
				.createCriteria(FormSubmission.class).setMaxResults(pageSize)
				.list();
	}
}
