package org.openhds.mobileinterop.model;


public class Pager {

	private final int pageSize;
	private final long totalItemCount;

	public Pager(int pageSize, long totalItemCount) {
		this.pageSize = pageSize;
		this.totalItemCount = totalItemCount;
	}

	public int getNumberOfPages() {
		if (totalItemCount < pageSize) {
			return 1;
		}
		
		int pageCount = (int) Math.ceil((double) totalItemCount / pageSize);

		return pageCount;
	}
	
	public int getStartItemForPage(int pageNum) {
		if (!isPageInRange(pageNum)) {
			return 0;
		}
		return pageSize * (pageNum - 1);
	}

	public boolean isPageInRange(int pageNum) {
		if (pageNum < 1 || pageNum > getNumberOfPages()) {
			return false;
		}
		
		return true;
	}

}
