/******************************************************************************
 * ExternalLogic.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.coursearchive.logic;

import java.util.Set;

import org.sakaiproject.content.api.ContentResource;

import org.sakaiproject.api.app.syllabus.SyllabusData;
import org.sakaiproject.api.app.syllabus.SyllabusItem;

/**
 * This is the interface for logic which is external to our app logic
 * @author Sakai App Builder -AZ
 */
public interface ExternalLogic {

	public final static String NO_LOCATION = "noLocationAvailable";

	// permissions
	public final static String ITEM_WRITE_ANY = "coursearchive.write.any";
	public final static String ITEM_READ_HIDDEN = "coursearchive.read.hidden";

	public String getUserId(String eid);
	public String getUserEid(String userId);

	/**
	 * @return the current sakai user id (not username)
	 */
	public String getCurrentUserId();

	/**
	 * Get the display name for a user by their unique id
	 * @param userId the current sakai user id (not username)
	 * @return display name (probably firstname lastname) or "----------" (10 hyphens) if none found
	 */
	public String getUserDisplayName(String userId);

	/**
	 * @return the current location id of the current user
	 */
	public String getCurrentLocationId();

	/**
	 * @param locationId a unique id which represents the current location of the user (entity reference)
	 * @return the title for the context or "--------" (8 hyphens) if none found
	 */
	public String getLocationTitle(String locationId);

	/**
	 * Check if this user has super admin access
	 * @param userId the internal user id (not username)
	 * @return true if the user has admin access, false otherwise
	 */
	public boolean isUserAdmin(String userId);

	/**
	 * Check if a user has a specified permission within a context, primarily
	 * a convenience method and passthrough
	 * 
	 * @param userId the internal user id (not username)
	 * @param permission a permission string constant
	 * @param locationId a unique id which represents the current location of the user (entity reference)
	 * @return true if allowed, false otherwise
	 */
	public boolean isUserAllowedInLocation(String userId, String permission, String locationId);

	public SyllabusItem getSyllabusItemBySiteId(String siteId);
	public Set getSyllabiForSyllabusItem(SyllabusItem syllabusItem);
	public Set getSyllabusAttachmentsForSyllabusData(SyllabusData syllabusData);
	public ContentResource addAttachmentResource(String name, String type, byte[] content);
	public ContentResource copyAttachmentResource(String oldId);
	public void removeAttachmentResource(String resourceId);
}
