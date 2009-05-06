/******************************************************************************
 * ExternalLogicImpl.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.coursearchive.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.api.app.syllabus.SyllabusData;
import org.sakaiproject.api.app.syllabus.SyllabusItem;
import org.sakaiproject.api.app.syllabus.SyllabusManager;

import org.sakaiproject.authz.api.FunctionManager;
import org.sakaiproject.authz.api.SecurityService;

import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;

import org.sakaiproject.coursearchive.logic.ExternalLogic;

import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;

import org.sakaiproject.exception.IdUnusedException;

import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;

import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;

import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

/**
 * This is the implementation for logic which is external to our app logic
 * @author Sakai App Builder -AZ
 */
public class ExternalLogicImpl implements ExternalLogic {

	private static Log log = LogFactory.getLog(ExternalLogicImpl.class);

	private FunctionManager functionManager;
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}

	private ToolManager toolManager;
	public void setToolManager(ToolManager toolManager) {
		this.toolManager = toolManager;
	}

	private SecurityService securityService;
	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	private SessionManager sessionManager;
	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	private SiteService siteService;
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	private UserDirectoryService userDirectoryService;
	public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
		this.userDirectoryService = userDirectoryService;
	}

	private SyllabusManager syllabusManager;
	public void setSyllabusManager(SyllabusManager syllabusManager) {
		this.syllabusManager = syllabusManager;
	}

	private ContentHostingService contentHostingService;
	public void setContentHostingService(ContentHostingService contentHostingService) {
		this.contentHostingService = contentHostingService;
	}


	/**
	 * Place any code that should run when this class is initialized by spring here
	 */
	public void init() {
		log.debug("init");
		// register Sakai permissions for this tool
		functionManager.registerFunction(ITEM_WRITE_ANY);
		functionManager.registerFunction(ITEM_READ_HIDDEN);
	}


	/* (non-Javadoc)
	 * @see org.sakaiproject.coursearchive.logic.ExternalLogic#getCurrentLocationId()
	 */
	public String getCurrentLocationId() {
		String location = null;
		try {
			String context = toolManager.getCurrentPlacement().getContext();
			location  = context;
//			  Site s = siteService.getSite( context );
//			  location = s.getReference(); // get the entity reference to the site
		} catch (Exception e) {
			// sakai failed to get us a location so we can assume we are not inside the portal
			return NO_LOCATION;
		}
		if(location == null) {
			location = NO_LOCATION;
		}
		return location;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.coursearchive.logic.ExternalLogic#getLocationTitle(java.lang.String)
	 */
	public String getLocationTitle(String locationId) {
		String title = null;
		try {
			Site site = siteService.getSite(locationId);
			title = site.getTitle();
		} catch (IdUnusedException e) {
			log.warn("Cannot get the info about locationId: " + locationId);
			title = "----------";
		}
		return title;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.coursearchive.logic.ExternalLogic#getCurrentUserId()
	 */
	public String getCurrentUserId() {
		return sessionManager.getCurrentSessionUserId();
	}

	public String getUserDisplayName(String userId) {
		String name = null;
		try {
			name = userDirectoryService.getUser(userId).getDisplayName();
		} catch (UserNotDefinedException e) {
			log.warn("Cannot get user displayname for id: " + userId);
			name = "--------";
		}
		return name;
	}

	public String getUserId(String eid) {
		String id = "";

		try {
			id = userDirectoryService.getUserByEid(eid).getId();
		} catch(UserNotDefinedException e) {
			log.warn("Cannot get user ID for EID: " + eid);
		}

		return id;
	}

	public String getUserEid(String userId) {
		String eid = "";

		try {
			eid = userDirectoryService.getUser(userId).getEid();
		} catch(UserNotDefinedException e) {
			log.warn("Cannot get user EID for ID: " + userId);
		}

		return eid;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.coursearchive.logic.ExternalLogic#isUserAdmin(java.lang.String)
	 */
	public boolean isUserAdmin(String userId) {
		return securityService.isSuperUser(userId);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.coursearchive.logic.ExternalLogic#isUserAllowedInLocation(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean isUserAllowedInLocation(String userId, String permission, String locationId) {
		if(securityService.unlock(userId, permission, locationId)) {
			return true;
		}
		return false;
	}

	public SyllabusItem getSyllabusItemBySiteId(String siteId) {
		return syllabusManager.getSyllabusItemByContextId(siteId);
	}

	public Set getSyllabiForSyllabusItem(SyllabusItem syllabusItem) {
		return syllabusManager.getSyllabiForSyllabusItem(syllabusItem);
	}

	public Set getSyllabusAttachmentsForSyllabusData(SyllabusData syllabusData) {
		return syllabusManager.getSyllabusAttachmentsForSyllabusData(syllabusData);
	}

	public ContentResource copyAttachmentResource(String oldId) {
		try {
			ContentResource oldAttachment = contentHostingService.getResource(oldId);
			return addAttachmentResource(
				oldAttachment.getProperties().getProperty(ResourceProperties.PROP_DISPLAY_NAME),
				oldAttachment.getContentType(),
				oldAttachment.getContent(),
				oldAttachment.getProperties());
		} catch(Exception e) {
			return null;
		}
	}

	public ContentResource addAttachmentResource(String name, String type, byte[] content) {
		ResourcePropertiesEdit properties = contentHostingService.newResourceProperties();
		properties.addProperty(ResourceProperties.PROP_DISPLAY_NAME, name);

		return addAttachmentResource(name, type, content, properties);
	}

	public ContentResource addAttachmentResource(String name, String type, byte[] content, ResourceProperties properties) {
		try {
			ContentResource resource = contentHostingService.addAttachmentResource(
					name,
					"course-archive",
					"syllabus",
					type,
					content,
					properties);
			return resource;
		} catch(Exception e) {
			return null;
		}
	}

	public void removeAttachmentResource(String resourceId) {
		try {
			contentHostingService.removeResource(resourceId);
		} catch(Exception e) {
		}
	}
}
