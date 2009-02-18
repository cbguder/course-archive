/******************************************************************************
 * CourseArchiveLogicImpl.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2008 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.coursearchive.logic;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.genericdao.api.search.Restriction;
import org.sakaiproject.genericdao.api.search.Search;

import org.sakaiproject.coursearchive.logic.ExternalLogic;
import org.sakaiproject.coursearchive.dao.CourseArchiveDao;
import org.sakaiproject.coursearchive.logic.CourseArchiveLogic;
import org.sakaiproject.coursearchive.model.CourseArchiveItem;

/**
 * This is the implementation of the business logic interface
 * @author Sakai App Builder -AZ
 */
public class CourseArchiveLogicImpl implements CourseArchiveLogic {

	private static Log log = LogFactory.getLog(CourseArchiveLogicImpl.class);

	private CourseArchiveDao dao;
	private ExternalLogic externalLogic;

	/**
	 * Place any code that should run when this class is initialized by spring here
	 */
	public void init() {
		log.debug("init");
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.coursearchive.logic.CourseArchiveLogic#getItemById(java.lang.Long)
	 */
	public CourseArchiveItem getItemById(Long id) {
		log.debug("Getting item by id: " + id);
		return dao.findById(CourseArchiveItem.class, id);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.coursearchive.logic.CourseArchiveLogic#canWriteItem(org.sakaiproject.coursearchive.model.CourseArchiveItem, java.lang.String, java.lang.String)
	 */
	public boolean canWriteItem(CourseArchiveItem item, String locationId, String userId) {
		log.debug("checking if can write for: " + userId + ", " + locationId + ": and item=" + item.getTitle() );
		if(item.getOwnerId().equals(userId)) {
			// owner can always modify an item
			return true;
		} else if(externalLogic.isUserAdmin(userId)) {
			// the system super user can modify any item
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.coursearchive.logic.CourseArchiveLogic#getAllItems()
	 */
	public List<CourseArchiveItem> getAllItems() {
		log.debug("Fetching all items");
		return dao.findAll(CourseArchiveItem.class);
	}

	public List<CourseArchiveItem> getUserItems() {
		log.debug("Fetching user items");
		return dao.findBySearch(CourseArchiveItem.class, new Search("ownerId", externalLogic.getCurrentUserId()));
	}

	public List<CourseArchiveItem> searchItems(String query) {
		log.debug("Searching items");
		return dao.findBySearch(CourseArchiveItem.class, new Search("title", "%"+query+"%", Restriction.LIKE));
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.coursearchive.logic.CourseArchiveLogic#removeItem(org.sakaiproject.coursearchive.model.CourseArchiveItem)
	 */
	public void removeItem(CourseArchiveItem item) {
		log.debug("In removeItem with item:" + item.getId() + ":" + item.getTitle());
		// check if current user can remove this item
		if(canWriteItem(item, externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId())) {
			dao.delete(item);
			log.info("Removing item: " + item.getId() + ":" + item.getTitle());
		} else {
			throw new SecurityException("Current user cannot remove item " + item.getId() + " because they do not have permission");
		}
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.coursearchive.logic.CourseArchiveLogic#saveItem(org.sakaiproject.coursearchive.model.CourseArchiveItem)
	 */
	public void saveItem(CourseArchiveItem item) {
		log.debug("In saveItem with item:" + item.getTitle());
		// set the owner and site to current if they are not set
		if(item.getOwnerId() == null) {
			item.setOwnerId(externalLogic.getCurrentUserId());
		}
		if(item.getDateCreated() == null) {
			item.setDateCreated(new Date());
		}
		// save item if new OR check if the current user can update the existing item
		if((item.getId() == null) || canWriteItem(item, externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId())) {
			dao.save(item);
			log.info("Saving item: " + item.getId() + ":" + item.getTitle());
		} else {
			throw new SecurityException("Current user cannot update item " + item.getId() + " because they do not have permission");
		}
	}

	/**
	 * Getters and Setters
	 */
	public void setDao(CourseArchiveDao dao) {
		this.dao = dao;
	}
	public void setExternalLogic(ExternalLogic externalLogic) {
		this.externalLogic = externalLogic;
	}
}
