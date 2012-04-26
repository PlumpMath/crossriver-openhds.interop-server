package org.openhds.mobileinterop.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openhds.mobileinterop.model.FormError;
import org.openhds.mobileinterop.model.FormRevision;
import org.openhds.mobileinterop.model.FormSubmission;
import org.openhds.mobileinterop.model.FormSubmissionGroup;
import org.openhds.mobileinterop.model.SubmissionStatus;
import org.openhds.mobileinterop.model.User;
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
		if (StringUtils.isBlank(submission.getDerivedFromUri())) {
			FormSubmissionGroup group = FormSubmissionGroup.startNewGroup(submission);
			submission.setGroup(group);
			save(group);
			save(submission);
		} else {
			FormSubmissionGroup group = findGroupByDerivedSubmission(submission);
			group.addSubmission(submission);
			submission.setGroup(group);
			
			for (FormError error : submission.getFormErrors()) {
				error.setSubmission(submission);
			}
			
			save(submission);
		}
	}

	private FormSubmissionGroup findGroupByDerivedSubmission(FormSubmission submission) {
		FormSubmission dervivedSubmission = findDerivedSubmission(submission);
		FormSubmissionGroup group = dervivedSubmission.getGroup();
		return group;
	}

	private FormSubmission findDerivedSubmission(FormSubmission submission) {
		FormSubmission dervivedSubmission = (FormSubmission) getCurrentSession()
				.createCriteria(FormSubmission.class)
				.add(Restrictions.eq("odkUri", submission.getDerivedFromUri())).uniqueResult();
		return dervivedSubmission;
	}

	private FormRevision discoverRevision(FormSubmission submission) {
		FormRevision revision = null;
		if (!submission.hasDerivedUri()) {
			revision = new FormRevision();
			revision.asFirstRevision(submission);
		} else {
			FormRevision previousRevision = findRevisionByProperty("derivedFromUri", submission.getDerivedFromUri());

			if (previousRevision == null) {
				// the first revision has its derived from uri property set to
				// null
				// this could be the second revision
				previousRevision = findRevisionByProperty("firstRevisionUri", submission.getDerivedFromUri());
			}

			if (previousRevision == null) {
				logger.warn("Form had derivedFromUri but found no matching form: " + submission.getId());
			} else {
				revision = new FormRevision();
				revision.asSiblingRevision(previousRevision, submission.getOdkUri());
			}
		}

		return revision;
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

	@SuppressWarnings("unchecked")
	public List<FormSubmission> findDownloadableSubmissionsForUser(User user) {
		List<FormSubmission> subs =  (List<FormSubmission>) getCurrentSession().createCriteria(FormSubmission.class)
				.add(Restrictions.in("formOwnerId", user.getManagedFieldworkers()))
				.add(Restrictions.eq("active", true)).list();	
		
		for(FormSubmission sub : subs) {
			sub.addDownloadActionToGroup();
		}
		
		return subs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FormSubmissionGroup> findAllFormSubmissions(int pageSize) {
		return (List<FormSubmissionGroup>) getCurrentSession().createCriteria(FormSubmissionGroup.class)
				.setMaxResults(pageSize).list();
	}

	@Override
	public FormSubmission findFormSubmissionById(long id) {
		return (FormSubmission) getCurrentSession().createCriteria(FormSubmission.class).add(Restrictions.eq("id", id))
				.uniqueResult();
	}

	@Override
	public FormSubmissionGroup findFormSubmissionGroupById(long groupId) {
		return (FormSubmissionGroup) getCurrentSession().createCriteria(FormSubmissionGroup.class)
				.add(Restrictions.eq("id", groupId)).uniqueResult();
	}

	@Override
	public void completeFormSubmissionGroup(FormSubmission submission) {
		FormSubmissionGroup group = findGroupByDerivedSubmission(submission);
		group.completeFormSubmissionGroup(submission);
		submission.setGroup(group);
		submission.setActive(false);
		save(submission);
	}
}
