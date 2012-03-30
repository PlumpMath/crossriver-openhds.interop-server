package org.openhds.mobileinterop.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openhds.mobileinterop.model.FormError;
import org.openhds.mobileinterop.model.FormRevision;
import org.openhds.mobileinterop.model.FormSubmission;
import org.openhds.mobileinterop.model.FormSubmission.SubmissionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FormDaoImpl implements FormDao {

	private SessionFactory sessionFactory;
	private static Logger logger = LoggerFactory.getLogger(FormDaoImpl.class);

	@Autowired
	public FormDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void saveFormSubmission(FormSubmission submission) {
		save(submission);

		for (FormError error : submission.getFormErrors()) {
			error.setSubmission(submission);
			save(error);
		}

		if (submission.getDerivedFromUri() == null || submission.getDerivedFromUri().trim().isEmpty()) {
			FormRevision revision = new FormRevision();
			revision.setFirstRevisionUri(submission.getOdkUri());
			revision.setUri(submission.getOdkUri());
			save(revision);
		} else {
			FormRevision previousRevision = findRevisionByProperty("derivedFromUri", submission.getDerivedFromUri());
			
			if (previousRevision == null) {
				// the first revision has its derived from uri property set to null
				// this could be the second revision
				previousRevision = findRevisionByProperty("firstRevisionUri", submission.getDerivedFromUri());
			}
			
			if (previousRevision == null) {
				logger.warn("Form had derivedFromUri but found no matching form: " + submission.getId());
			} else {
				FormRevision newRevision = new FormRevision();
				newRevision.setFirstRevisionUri(previousRevision.getFirstRevisionUri());
				newRevision.setDerivedFromUri(previousRevision.getUri());
				newRevision.setUri(submission.getOdkUri());
				save(newRevision);
			}
		}
	}

	private FormRevision findRevisionByProperty(String propertyName, Object propertyValue) {
		return (FormRevision) getCurrentSession().createCriteria(FormRevision.class)
				.add(Restrictions.eq(propertyName, propertyValue)).uniqueResult();
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
		return (List<FormSubmission>) getCurrentSession().createCriteria(FormSubmission.class)
				.add(Restrictions.eq("formOwnerId", ownerId))
				.add(Restrictions.eq("submissionStatus", SubmissionStatus.SUBMITTED)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FormSubmission> findAllFormSubmissions(int pageSize) {
		return (List<FormSubmission>) getCurrentSession().createCriteria(FormSubmission.class).setMaxResults(pageSize)
				.list();
	}

	@Override
	public FormSubmission findFormSubmissionById(long id) {
		return (FormSubmission) getCurrentSession().createCriteria(FormSubmission.class).add(Restrictions.eq("id", id))
				.uniqueResult();
	}

	@Override
	public void updateFormToFixed(String uri) {
		FormSubmission sub = (FormSubmission) getCurrentSession().createCriteria(FormSubmission.class)
				.add(Restrictions.eq("odkUri", uri)).uniqueResult();

		if (sub != null) {
			sub.setSubmissionStatus(SubmissionStatus.FIXED);
		}
	}
}
