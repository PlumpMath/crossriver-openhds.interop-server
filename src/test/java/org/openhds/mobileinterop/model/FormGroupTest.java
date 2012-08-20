package org.openhds.mobileinterop.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class FormGroupTest {
	
	FormGroup group;
	
	@Before
	public void setUp() {
		group = new FormGroup();
	}

	@Test
	public void shouldSetPreviousSubmissionsToInactive() {
		FormSubmission firstSubmission = new FormSubmission();
		FormSubmission secondSubmission = new FormSubmission();
		
		group.addSubmission(firstSubmission);
		group.addSubmission(secondSubmission);
		
		assertFalse(firstSubmission.isActive());
		assertTrue(secondSubmission.isActive());
	}
	
	@Test 
	public void shouldAddUploadAction() {
		FormSubmission submission = new FormSubmission();
		group.addSubmission(submission);
		
		FormAction[] actions = group.getFormActions().toArray(new FormAction[]{});
		assertEquals(1, actions.length);
		assertEquals(FormAction.ActionType.UPLOADED, actions[0].getActionType());
	}
	
	@Test
	public void shouldSetAllFormsToInactiveOnComplete() {
		FormSubmission fs1 = new FormSubmission();
		FormSubmission fs2 = new FormSubmission();
		group.addSubmission(fs1);
		group.completeFormSubmissionGroup(fs2);
		
		assertFalse(fs1.isActive());
		assertFalse(fs2.isActive());
	}
}
