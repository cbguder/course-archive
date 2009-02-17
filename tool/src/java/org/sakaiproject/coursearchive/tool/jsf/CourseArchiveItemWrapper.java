/******************************************************************************
 * CourseArchiveItemWrapper.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2008 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.coursearchive.tool.jsf;

import org.sakaiproject.coursearchive.model.CourseArchiveItem;


/**
 * This is a wrapper class which is required to get the interactions from the user
 * without storing UI dependent information in the data POJO
 * @author Sakai App Builder -AZ
 */
public class CourseArchiveItemWrapper {

	private CourseArchiveItem item;
	private boolean canDelete; // can this item be deleted
	private boolean isSelected; // is this item selected by the user

	/**
	 * Constructor which accepts the object we are wrapping
	 * @param item the CourseArchiveItem we are wrapping
	 */
	public CourseArchiveItemWrapper(CourseArchiveItem item) {
		this.item = item;
	}

	/**
	 * Basic setters and getters
	 */
	public CourseArchiveItem getItem() {
		return item;
	}
	public void setItem(CourseArchiveItem item) {
		this.item = item;
	}
	public boolean isCanDelete() {
		return canDelete;
	}
	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
