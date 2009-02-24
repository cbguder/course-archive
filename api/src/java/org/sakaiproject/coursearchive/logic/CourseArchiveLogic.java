/******************************************************************************
 * CourseArchiveLogic.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2008 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.coursearchive.logic;

import java.util.List;

import org.sakaiproject.coursearchive.model.CourseArchiveItem;

/**
 * This is the interface for the app Logic
 * @author Sakai App Builder -AZ
 */
public interface CourseArchiveLogic {

	/**
	 * This returns an item based on an id
	 * @param id the id of the item to fetch
	 * @return a CourseArchiveItem or null if none found
	 */
	public CourseArchiveItem getItemById(Long id);

	/**
	 * Check if a specified user can write this item in a specified site
	 * @param item to be modified or removed
	 * @param locationId a unique id which represents the current location of the user (entity reference)
	 * @param userId the internal user id (not username)
	 * @return true if item can be modified, false otherwise
	 */
	public boolean canWriteItem(CourseArchiveItem item, String userId);

	public boolean canDeleteItems(String userId);

	/**
	 * This returns the List of all items
	 * @return a List of CourseArchiveItem objects
	 */
	public List<CourseArchiveItem> getAllItems();

	public List<CourseArchiveItem> getUserItems();

	/**
	 * Save (Create or Update) an item (uses the current site)
	 * @param item the CourseArchiveItem to create or update
	 */
	public void saveItem(CourseArchiveItem item);

	/**
	 * Remove an item
	 * @param item the CourseArchiveItem to remove
	 */
	public void removeItem(CourseArchiveItem item);

	public List<CourseArchiveItem> searchItems(String query);
}
