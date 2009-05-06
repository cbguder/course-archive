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
import java.util.Set;

import org.sakaiproject.api.app.syllabus.SyllabusData;

import org.sakaiproject.coursearchive.model.CourseArchiveAssignment;
import org.sakaiproject.coursearchive.model.CourseArchiveAssignmentType;
import org.sakaiproject.coursearchive.model.CourseArchiveAttachment;
import org.sakaiproject.coursearchive.model.CourseArchiveItem;
import org.sakaiproject.coursearchive.model.CourseArchiveStudent;
import org.sakaiproject.coursearchive.model.CourseArchiveSyllabus;

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
	public List<CourseArchiveItem> getUserItems(boolean includeOlder);

	/**
	 * Save (Create or Update) an item (uses the current site)
	 * @param item the CourseArchiveItem to create or update
	 */
	public void saveItem(CourseArchiveItem item);

	public void saveAssignment(CourseArchiveAssignment assignment);

	public void saveSyllabus(CourseArchiveSyllabus syllabus);

	/**
	 * Remove an item
	 * @param item the CourseArchiveItem to remove
	 */
	public void removeItem(CourseArchiveItem item);

	public void removeAssignment(CourseArchiveAssignment assignment);

	public void removeSyllabus(CourseArchiveSyllabus syllabus);

	public List<CourseArchiveItem> searchItems(String query);

	public List<CourseArchiveAssignment> getItemAssignments(CourseArchiveItem item);

	public List<CourseArchiveStudent> getItemStudents(CourseArchiveItem item);

	public List<CourseArchiveSyllabus> getItemSyllabi(CourseArchiveItem item);

	public List<CourseArchiveAttachment> getSyllabusAttachments(CourseArchiveSyllabus syllabus);

	public CourseArchiveAssignmentType getAssignmentTypeById(Long id);

	public List<CourseArchiveAssignmentType> getAssignmentTypes();

	public long getItemEnrollment(CourseArchiveItem item);

	public void mergeItems(List<CourseArchiveItem> items);

	public Set getSyllabiForSiteId(String siteId);

	public void archiveSyllabi(CourseArchiveItem item, SyllabusData syllabusData);

	public CourseArchiveAttachment createAttachment(CourseArchiveSyllabus syllabus, String name, String type, byte[] content);
}
