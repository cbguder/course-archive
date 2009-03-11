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

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.genericdao.api.search.Restriction;
import org.sakaiproject.genericdao.api.search.Search;
import org.sakaiproject.genericdao.api.search.Order;

import org.sakaiproject.genericdao.hibernate.HibernateGeneralGenericDao;

import org.sakaiproject.coursearchive.logic.ExternalLogic;
import org.sakaiproject.coursearchive.dao.CourseArchiveDao;
import org.sakaiproject.coursearchive.logic.CourseArchiveLogic;
import org.sakaiproject.coursearchive.model.CourseArchiveItem;
import org.sakaiproject.coursearchive.model.CourseArchiveStudent;
import org.sakaiproject.coursearchive.model.CourseArchiveAssignment;

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
	public boolean canWriteItem(CourseArchiveItem item, String userId) {
		log.debug("checking if can write for: " + userId + " and item=" + item.getCode() );
		if(item.getOwnerId().equals(userId)) {
			// owner can always modify an item
			return true;
		} else if(externalLogic.isUserAdmin(userId)) {
			// the system super user can modify any item
			return true;
		}
		return false;
	}

	public boolean canDeleteItems(String userId) {
		return externalLogic.isUserAdmin(userId);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.coursearchive.logic.CourseArchiveLogic#getAllItems()
	 */
	public List<CourseArchiveItem> getAllItems() {
		log.debug("Fetching all items");
		return dao.findAll(CourseArchiveItem.class);
	}

	public List<CourseArchiveItem> getUserItems() {
		return getUserItems(false);
	}

	public List<CourseArchiveItem> getUserItems(boolean includeOlder) {
		log.debug("Fetching user items");

		Search search = new Search();
		search.addRestriction(new Restriction("ownerId", externalLogic.getCurrentUserId()));

		if(!includeOlder) {
			Calendar calendar = Calendar.getInstance();
			int year  = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);

			if(month >= 9) {
				year -= 2;
			} else {
				year -= 3;
			}

			String term = Integer.toString(year) + "02";

			search.addRestriction(new Restriction("term", term, Restriction.GREATER));
		}

		search.addOrder(new Order("term", false));

		return dao.findBySearch(CourseArchiveItem.class, search);
	}

	public List<CourseArchiveItem> searchItems(String query) {
		log.debug("Searching items");

		Search search = new Search();
		search.setConjunction(false);
		search.addRestriction(new Restriction("code", "%"+query+"%", Restriction.LIKE));
		search.addRestriction(new Restriction("name", "%"+query+"%", Restriction.LIKE));
		search.addRestriction(new Restriction("primaryInstructor", "%"+query+"%", Restriction.LIKE));
		search.addOrder(new Order("term", false));

		return dao.findBySearch(CourseArchiveItem.class, search);
	}

	public List<CourseArchiveAssignment> getItemAssignments(CourseArchiveItem item) {
		return dao.findBySearch(CourseArchiveAssignment.class, new Search("item.id", item.getId()));
	}

	public long getItemEnrollment(CourseArchiveItem item) {
		return dao.countBySearch(CourseArchiveStudent.class, new Search("item.id", item.getId()));
	}

	public List<CourseArchiveStudent> getItemStudents(CourseArchiveItem item) {
		return dao.findBySearch(CourseArchiveStudent.class, new Search("item.id", item.getId()));
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.coursearchive.logic.CourseArchiveLogic#removeItem(org.sakaiproject.coursearchive.model.CourseArchiveItem)
	 */
	public void removeItem(CourseArchiveItem item) {
		log.debug("In removeItem with item:" + item.getId() + ":" + item.getCode());
		// check if current user can remove this item
		if(canDeleteItems(externalLogic.getCurrentUserId())) {
			dao.deleteByItemId(CourseArchiveAssignment.class, item.getId());
			dao.deleteByItemId(CourseArchiveStudent.class,    item.getId());
			dao.delete(item);
			log.info("Removing item: " + item.getId() + ":" + item.getCode());
		} else {
			throw new SecurityException("Current user cannot remove item " + item.getId() + " because they do not have permission");
		}
	}

	public void removeAssignment(CourseArchiveAssignment assignment) {
		CourseArchiveItem item = getItemById(assignment.getItem().getId());

		if(canWriteItem(item, externalLogic.getCurrentUserId())) {
			dao.delete(assignment);
		} else {
			throw new SecurityException("Current user cannot uptade item " + item.getId() + " because they do not have permission");
		}
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.coursearchive.logic.CourseArchiveLogic#saveItem(org.sakaiproject.coursearchive.model.CourseArchiveItem)
	 */
	public void saveItem(CourseArchiveItem item) {
		log.debug("In saveItem with item:" + item.getCode());
		// set the owner and site to current if they are not set
		if(item.getOwnerId() == null) {
			item.setOwnerId(externalLogic.getCurrentUserId());
		}
		if(item.getDateCreated() == null) {
			item.setDateCreated(new Date());
		}
		// save item if new OR check if the current user can update the existing item
		if((item.getId() == null) || canWriteItem(item, externalLogic.getCurrentUserId())) {
			dao.save(item);
			log.info("Saving item: " + item.getId() + ":" + item.getCode());
		} else {
			throw new SecurityException("Current user cannot update item " + item.getId() + " because they do not have permission");
		}
	}

	public void saveAssignment(CourseArchiveAssignment assignment) {
		CourseArchiveItem item = getItemById(assignment.getItem().getId());

		if(canWriteItem(item, externalLogic.getCurrentUserId())) {
			dao.save(assignment);
		} else {
			throw new SecurityException("Current user cannot update item " + item.getId() + " because they do not have permission");
		}
	}

	public void mergeItems(List<CourseArchiveItem> items) {
		if(externalLogic.isUserAdmin(externalLogic.getCurrentUserId())) {
			CourseArchiveItem newItem  = new CourseArchiveItem(items.get(0));
			for(int i = 1; i < items.size(); i++) {
				CourseArchiveItem item = items.get(i);

				newItem.setA      (newItem.getA()       + item.getA());
				newItem.setA_MINUS(newItem.getA_MINUS() + item.getA_MINUS());
				newItem.setB_PLUS (newItem.getB_PLUS()  + item.getB_PLUS());
				newItem.setB      (newItem.getB()       + item.getB());
				newItem.setB_MINUS(newItem.getB_MINUS() + item.getB_MINUS());
				newItem.setC_PLUS (newItem.getC_PLUS()  + item.getC_PLUS());
				newItem.setC      (newItem.getC()       + item.getC());
				newItem.setC_MINUS(newItem.getC_MINUS() + item.getC_MINUS());
				newItem.setD_PLUS (newItem.getD_PLUS()  + item.getD_PLUS());
				newItem.setD      (newItem.getD()       + item.getD());
				newItem.setF      (newItem.getF()       + item.getF());
			}

			newItem.setDateCreated(new Date());
			dao.save(newItem);

			for(Iterator iter = items.iterator(); iter.hasNext();) {
				CourseArchiveItem item = (CourseArchiveItem)iter.next();
				dao.updateItemId(CourseArchiveStudent.class,    item.getId(), newItem.getId());
				dao.updateItemId(CourseArchiveAssignment.class, item.getId(), newItem.getId());
				dao.delete(item);
			}
		} else {
			throw new SecurityException("Current user cannot merge items because they do not have permission");
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
