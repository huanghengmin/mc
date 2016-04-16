package com.inetec.ichange.mc;

import java.util.List;

/**
 * ��ҳ����ʹ�÷���֧��
 * 
 * @author ������
 * 
 * @param <T>
 */
public class Pagination<T> {

	public final static int PAGESIZE = 15;

	private int pageSize = PAGESIZE;

	private List<T> items;

	private int totalCount;

	private int[] indexes = new int[0];

	private int startIndex = 0;

	//private int pageIndex = 0;

	public Pagination(List<T> items, int totalCount) {
		setPageSize(PAGESIZE);
		setTotalCount(totalCount);
		setItems(items);
		setStartIndex(0);
		//this.setStartPage(0);
	}

	public Pagination(List<T> items, int totalCount, int startIndex/*,
			int pageIndex*/) {
		setPageSize(PAGESIZE);
		setTotalCount(totalCount);
		setItems(items);
		setStartIndex(startIndex);
		//setStartPage(pageIndex);
	}

	public Pagination(List<T> items, int totalCount, int pageSize,
			int startIndex/*, int pageIndex*/) {
		setPageSize(pageSize);
		setTotalCount(totalCount);
		setItems(items);
		setStartIndex(startIndex);
		//setStartPage(pageIndex);
	}

	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {
		this.items = items;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public int getTotalPages() {
		int totalPage = totalCount / pageSize;
		if (totalPage <= 0)
			totalPage = 1;
		if (totalCount % pageSize > 0)
			totalPage = totalPage + 1;
		return totalPage;
	}

	/*
	public void setStartPage(int pageIndex) {
		int totalPages = getTotalPages();
		if (totalPages <= 0)
			pageIndex = 1;
		else if (pageIndex >= totalPages)
			this.pageIndex = totalPages;
		else if (pageIndex <= 0)
			this.pageIndex = 1;
		else {
			this.pageIndex = pageIndex;
		}
	}

	public int getStartPage() {
		return this.pageIndex;
	}
	
	public int getPreviousPage() {
		int prePage = pageIndex - 1;
		if (prePage <= 0)
			prePage = 1;
		// if(prePage>getTotalPages())
		return prePage;
	}

	public int getNextPage() {
		int nextPage = pageIndex + 1;
		if (nextPage > this.getTotalPages())
			return this.getStartPage();
		else
			return nextPage;

	}
	*/
	public void setTotalCount(int totalCount) {
		if (totalCount > 0) {
			this.totalCount = totalCount;
			int count = totalCount / pageSize;
			if (totalCount % pageSize > 0)
				count++;
			indexes = new int[count];
			for (int i = 0; i < count; i++) {
				indexes[i] = pageSize * i;
			}
		} else {
			this.totalCount = 0;
		}
	}

	public int[] getIndexes() {
		return indexes;
	}

	public void setIndexes(int[] indexes) {
		this.indexes = indexes;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		if (totalCount <= 0)
			this.startIndex = 0;
		else if (startIndex >= totalCount)
			this.startIndex = indexes[indexes.length - 1];
		else if (startIndex < 0)
			this.startIndex = 0;
		else {
			this.startIndex = indexes[startIndex / pageSize];
		}
	}

	public int getNextIndex() {
		int nextIndex = getStartIndex() + pageSize;
		if (nextIndex >= totalCount)
			return getStartIndex();
		else
			return nextIndex;
	}

	public int getPreviousIndex() {
		int previousIndex = getStartIndex() - pageSize;
		if (previousIndex < 0)
			return 0;
		else
			return previousIndex;
	}
}
