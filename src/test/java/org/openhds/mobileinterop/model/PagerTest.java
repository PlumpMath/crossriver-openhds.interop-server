package org.openhds.mobileinterop.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PagerTest {

	@Test
	public void shouldBeOnlyOnePage() {
		Pager pager = new Pager(50, 49);
		
		assertEquals(1, pager.getNumberOfPages());
	}
	
	@Test
	public void shouldBeTwoPages() {
		Pager pager = new Pager(50, 60);
		
		assertEquals(2, pager.getNumberOfPages());
	}
	
	@Test
	public void shouldGetStartItemForFirstPage() {
		Pager pager = new Pager(20, 100);
		
		assertEquals(0, pager.getStartItemForPage(1));
	}
	
	@Test
	public void shouldGetStartItemForSecondPage() {
		Pager pager = new Pager(20, 100);
		
		assertEquals(20, pager.getStartItemForPage(2));
	}
	
	@Test
	public void shouldGetStartItemForLastPage() {
		Pager pager = new Pager(20, 100);
		
		assertEquals(80, pager.getStartItemForPage(5));
	}
	
	@Test
	public void shouldReturnFirstStartItemOnInvalidPage() {
		Pager pager = new Pager(20, 100);
		
		assertEquals(0, pager.getStartItemForPage(10));
	}
	
	@Test
	public void shouldBeOutOfRange() {
		Pager pager = new Pager(20, 100);
		
		assertFalse(pager.isPageInRange(10));
	}
	
	@Test
	public void shouldBeInRange() {
		Pager pager = new Pager(20, 100);
		
		assertTrue(pager.isPageInRange(3));
	}
}
