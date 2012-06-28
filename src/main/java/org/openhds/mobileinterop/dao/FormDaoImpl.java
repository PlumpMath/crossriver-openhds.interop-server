package org.openhds.mobileinterop.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openhds.mobileinterop.model.FormError;
import org.openhds.mobileinterop.model.FormSubmission;
import org.openhds.mobileinterop.model.FormGroup;
import org.openhds.mobileinterop.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class FormDaoImpl implements FormDao {

	private SessionFactory sessionFactory;
	private static Logger logger = LoggerFactory.getLogger(FormDaoImpl.class);

	@Autowired
	public FormDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	@Transactional
	public void saveFormSubmission(FormSubmission submission) {
		if (StringUtils.isBlank(submission.getDerivedFromUri())) {
			FormGroup group = FormGroup.startNewGroup(submission);
			save(group);
		} else {
			FormGroup group = findGroupByDerivedSubmission(submission);
			group.addSubmission(submission);

			for (FormError error : submission.getFormErrors()) {
				error.setSubmission(submission);
			}
		}
	}

	private FormGroup findGroupByDerivedSubmission(FormSubmission submission) {
		FormSubmission dervivedSubmission = findDerivedSubmission(submission);
		FormGroup group = dervivedSubmission.getGroup();
		return group;
	}

	private FormSubmission findDerivedSubmission(FormSubmission submission) {
		FormSubmission dervivedSubmission = (FormSubmission) getCurrentSession()
				.createCriteria(FormSubmission.class)
				.add(Restrictions.eq("odkUri", submission.getDerivedFromUri())).uniqueResult();
		return dervivedSubmission;
	}

	private void save(Object obj) {
		getCurrentSession().save(obj);
	}

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	@Transactional
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
	@Transactional(readOnly = true)
	public List<FormGroup> findAllFormSubmissions(int pageSize) {
		return (List<FormGroup>) getCurrentSession().createCriteria(FormGroup.class)
				.setMaxResults(pageSize).list();
	}

	@Override
	@Transactional(readOnly = true)
	public FormSubmission findFormSubmissionById(long id) {
		return (FormSubmission) getCurrentSession().createCriteria(FormSubmission.class).add(Restrictions.eq("id", id))
				.uniqueResult();
	}

	@Override
	@Transactional(readOnly = true)
	public FormGroup findFormSubmissionGroupById(long groupId) {
		return (FormGroup) getCurrentSession().createCriteria(FormGroup.class)
				.add(Restrictions.eq("id", groupId)).uniqueResult();
	}

	@Override
	@Transactional
	public void completeFormSubmissionGroup(String completedFormId, FormSubmission submission) {
		FormGroup group = findGroupByDerivedSubmission(submission);
		group.setCompletedFormId(completedFormId);
		group.completeFormSubmissionGroup(submission);
	}

	@Override
	@Transactional
	public void voidGroup(long groupId) {
		FormGroup group = findFormSubmissionGroupById(groupId);
		group.voidGroup();
	}
}
