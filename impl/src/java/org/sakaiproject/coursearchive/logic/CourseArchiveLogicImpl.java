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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.coursearchive.dao.CourseArchiveDao;
import org.sakaiproject.coursearchive.logic.CourseArchiveLogic;
import org.sakaiproject.coursearchive.logic.ExternalLogic;
import org.sakaiproject.coursearchive.model.CourseArchiveAssignment;
import org.sakaiproject.coursearchive.model.CourseArchiveItem;
import org.sakaiproject.coursearchive.model.CourseArchiveStudent;
import org.sakaiproject.coursearchive.model.CourseArchiveSyllabus;

import org.sakaiproject.genericdao.api.search.Order;
import org.sakaiproject.genericdao.api.search.Restriction;
import org.sakaiproject.genericdao.api.search.Search;

import org.sakaiproject.genericdao.hibernate.HibernateGeneralGenericDao;

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

		if(item.getOwnerId().equals(userId) || item.getDelegateId().equals(userId)) {
			// owner or delegate can only modify last term's items
			return item.getTerm().equals(getTerm(-1));
		} else if(externalLogic.isUserAdmin(userId)) {
			// the system super user can modify any item
			return true;
		}

		return false;
	}

	public boolean canDeleteItems(String userId) {
		return externalLogic.isUserAdmin(userId);
	}

	protected String getTerm(int offset) {
		Calendar calendar = Calendar.getInstance();
		int year  = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int term  = 1;

		if(month < Calendar.SEPTEMBER) {
			year -= 1;
			if(month > Calendar.FEBRUARY) {
				term = 2;
			}
		}

		term += offset;

		while(term < 1) {
			year -= 1;
			term += 2;
		}

		while(term > 2) {
			year += 1;
			term -= 2;
		}

		return String.format("%04d%02d", year, term);
	}

	protected String getCurrentTerm() {
		return getTerm(0);
	}

	protected String getLimitTerm() {
		String aTerm = getTerm(-4);

		if(aTerm.endsWith("02")) {
			return aTerm;
		} else {
			return getTerm(-5);
		}
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

		if(includeOlder) {
			return dao.getUserItems(externalLogic.getCurrentUserId());
		} else {
			return dao.getUserItems(externalLogic.getCurrentUserId(), getLimitTerm());
		}
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

	protected String commonPrefix(List<String> strings) {
		int stringCount = strings.size();

		if(stringCount == 0) {
			return "";
		} else if(stringCount == 1) {
			return strings.get(0);
		}

		int minLength = Integer.MAX_VALUE;
		for(Iterator<String> iter = strings.iterator(); iter.hasNext();) {
			int len = iter.next().length();
			if(len < minLength) {
				minLength = len;
			}
		}

		StringBuilder result = new StringBuilder(minLength);

		String first = strings.get(0);
		for(int i = 0; i < minLength; i++) {
			boolean notFound = false;
			char c = first.charAt(i);

			for(Iterator<String> iter = strings.iterator(); iter.hasNext();) {
				if(iter.next().charAt(i) != c) {
					notFound = true;
					break;
				}
			}

			if(notFound) {
				break;
			} else {
				result.append(c);
			}
		}

		return result.toString();
	}

	protected String mergeCodes(List<String> codes) {
		String commonCodePrefix = commonPrefix(codes);
		int prefixLength = commonCodePrefix.length();

		StringBuilder result = new StringBuilder(commonCodePrefix);

		for(Iterator<String> iter = codes.iterator(); iter.hasNext();) {
			result.append(iter.next().substring(prefixLength));
		}

		return result.toString();
	}

	public void mergeItems(List<CourseArchiveItem> items) {
		if(externalLogic.isUserAdmin(externalLogic.getCurrentUserId())) {
			CourseArchiveItem first   = items.get(0);
			CourseArchiveItem newItem = new CourseArchiveItem(first);

			ArrayList<String> codes   = new ArrayList<String>(items.size());
			codes.add(first.getCode());

			Iterator<CourseArchiveItem> iter = items.iterator();
			iter.next();

			while(iter.hasNext()) {
				CourseArchiveItem item = iter.next();
				codes.add(item.getCode());

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

			newItem.setCode(mergeCodes(codes));
			newItem.setDateCreated(new Date());
			dao.save(newItem);

			iter = items.iterator();
			while(iter.hasNext()) {
				CourseArchiveItem item = iter.next();
				dao.updateItemId(CourseArchiveStudent.class,    item.getId(), newItem.getId());
				dao.updateItemId(CourseArchiveAssignment.class, item.getId(), newItem.getId());
				dao.delete(item);
			}
		} else {
			throw new SecurityException("Current user cannot merge items because they do not have permission");
		}
	}

	public void archiveSyllabus(CourseArchiveItem item) {
		if(!canWriteItem(item, externalLogic.getCurrentUserId())) {
			throw new SecurityException("Current user cannot update item " + item.getId() + " because they do not have permission");
		}

		item.setSyllabusURL(externalLogic.getSyllabusURLForSiteId(item.getSiteId()));
		dao.save(item);

		List<String> syllabusData = externalLogic.getSyllabusDataForSiteId(item.getSiteId());

		for(Iterator<String> iter = syllabusData.iterator(); iter.hasNext();) {
			String title = iter.next();
			String asset = iter.next();
			CourseArchiveSyllabus syllabus = new CourseArchiveSyllabus(item, title, asset);
			dao.save(syllabus);
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
