package org.sakaiproject.coursearchive.tool.jsf;

public class CourseArchiveWrapper<E> {
	private E item;
	private boolean isSelected;

	public CourseArchiveWrapper(E item) {
		this.item = item;
	}

	public E getItem() {
		return item;
	}
	public void setItem(E item) {
		this.item = item;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
