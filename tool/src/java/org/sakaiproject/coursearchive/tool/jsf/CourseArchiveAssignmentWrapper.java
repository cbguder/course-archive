package org.sakaiproject.coursearchive.tool.jsf;

import org.sakaiproject.coursearchive.model.CourseArchiveAssignment;

public class CourseArchiveAssignmentWrapper {

	private CourseArchiveAssignment item;
	private boolean isSelected; // is this item selected by the user

	/**
	 * Constructor which accepts the object we are wrapping
	 * @param item the CourseArchiveAssignment we are wrapping
	 */
	public CourseArchiveAssignmentWrapper(CourseArchiveAssignment item) {
		this.item = item;
	}

	/**
	 * Basic setters and getters
	 */
	public CourseArchiveAssignment getItem() {
		return item;
	}
	public void setItem(CourseArchiveAssignment item) {
		this.item = item;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
