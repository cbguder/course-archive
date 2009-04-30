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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.api.app.syllabus.SyllabusAttachment;
import org.sakaiproject.api.app.syllabus.SyllabusData;
import org.sakaiproject.api.app.syllabus.SyllabusItem;

import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;

import org.sakaiproject.coursearchive.dao.CourseArchiveDao;
import org.sakaiproject.coursearchive.logic.CourseArchiveLogic;
import org.sakaiproject.coursearchive.logic.ExternalLogic;
import org.sakaiproject.coursearchive.model.CourseArchiveAssignment;
import org.sakaiproject.coursearchive.model.CourseArchiveAssignmentType;
import org.sakaiproject.coursearchive.model.CourseArchiveAttachment;
import org.sakaiproject.coursearchive.model.CourseArchiveItem;
import org.sakaiproject.coursearchive.model.CourseArchiveStudent;
import org.sakaiproject.coursearchive.model.CourseArchiveSyllabus;

import org.sakaiproject.entity.api.ResourceProperties;

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
		search.addRestriction(new Restriction("otherInstructors", "%"+query+"%", Restriction.LIKE));
		search.addRestriction(new Restriction("assistants", "%"+query+"%", Restriction.LIKE));
		search.addOrder(new Order("term", false));

		return dao.findBySearch(CourseArchiveItem.class, search);
	}

	public List<CourseArchiveAssignment> getItemAssignments(CourseArchiveItem item) {
		List<CourseArchiveAssignment> assignments = dao.findBySearch(CourseArchiveAssignment.class, new Search("item.id", item.getId()));

		for(Iterator<CourseArchiveAssignment> iter = assignments.iterator(); iter.hasNext();) {
			CourseArchiveAssignment assignment = iter.next();
			assignment.setType(dao.findById(CourseArchiveAssignmentType.class, assignment.getType().getId()));
		}

		return assignments;
	}

	public long getItemEnrollment(CourseArchiveItem item) {
		return dao.countBySearch(CourseArchiveStudent.class, new Search("item.id", item.getId()));
	}

	public List<CourseArchiveStudent> getItemStudents(CourseArchiveItem item) {
		return dao.findBySearch(CourseArchiveStudent.class, new Search("item.id", item.getId()));
	}

	public List<CourseArchiveSyllabus> getItemSyllabi(CourseArchiveItem item) {
		return dao.findBySearch(CourseArchiveSyllabus.class, new Search("item.id", item.getId()));
	}

	public List<CourseArchiveAttachment> getSyllabusAttachments(CourseArchiveSyllabus syllabus) {
		return dao.findBySearch(CourseArchiveAttachment.class, new Search("syllabus.id", syllabus.getId()));
	}

	public CourseArchiveAssignmentType getAssignmentTypeById(Long id) {
		return dao.findById(CourseArchiveAssignmentType.class, id);
	}

	public List<CourseArchiveAssignmentType> getAssignmentTypes() {
		return dao.findAll(CourseArchiveAssignmentType.class);
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

			List<CourseArchiveSyllabus> syllabi = getItemSyllabi(item);
			for(Iterator<CourseArchiveSyllabus> iter = syllabi.iterator(); iter.hasNext();)
				removeSyllabus(iter.next());

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
			throw new SecurityException("Current user cannot update item " + item.getId() + " because they do not have permission");
		}
	}

	public void removeSyllabus(CourseArchiveSyllabus syllabus) {
		CourseArchiveItem item = getItemById(syllabus.getItem().getId());

		if(canWriteItem(item, externalLogic.getCurrentUserId())) {
			List<CourseArchiveAttachment> attachments = getSyllabusAttachments(syllabus);
			for(Iterator<CourseArchiveAttachment> iter = attachments.iterator(); iter.hasNext();)
				externalLogic.removeAttachment(iter.next().getResourceId());

			dao.deleteBySyllabusId(CourseArchiveAttachment.class, syllabus.getId());
			dao.delete(syllabus);
		} else {
			throw new SecurityException("Current user cannot update item " + item.getId() + " because they do not have permission");
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

	public void saveSyllabus(CourseArchiveSyllabus syllabus) {
		CourseArchiveItem item = getItemById(syllabus.getItem().getId());

		if(canWriteItem(item, externalLogic.getCurrentUserId())) {
			dao.save(syllabus);
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

			TreeSet<String> otherInstructors = new TreeSet<String>();
			TreeSet<String> assistants       = new TreeSet<String>();

			int A       = 0;
			int A_MINUS = 0;
			int B_PLUS  = 0;
			int B       = 0;
			int B_MINUS = 0;
			int C_PLUS  = 0;
			int C       = 0;
			int C_MINUS = 0;
			int D_PLUS  = 0;
			int D       = 0;
			int F       = 0;

			Iterator<CourseArchiveItem> iter = items.iterator();
			while(iter.hasNext()) {
				CourseArchiveItem item = iter.next();
				codes.add(item.getCode());

				if(!item.getPrimaryInstructor().equals("") &&
				   !item.getPrimaryInstructor().equals(first.getPrimaryInstructor())) {
					otherInstructors.add(item.getPrimaryInstructor());
				}

				String[] itemOtherInstructors = item.getOtherInstructors().split("\r\n|\r|\n");
				for(int i = 0; i < itemOtherInstructors.length; i++) {
					String instructor = itemOtherInstructors[i].trim();
					if(!instructor.equals("")) {
						otherInstructors.add(instructor);
					}
				}

				String[] itemAssistants = item.getAssistants().split("\r\n|\r|\n");
				for(int i = 0; i < itemAssistants.length; i++) {
					String assistant = itemAssistants[i].trim();
					if(!assistant.equals("")) {
						assistants.add(assistant);
					}
				}

				A       += item.getA();
				A_MINUS += item.getA_MINUS();
				B_PLUS  += item.getB_PLUS();
				B       += item.getB();
				B_MINUS += item.getB_MINUS();
				C_PLUS  += item.getC_PLUS();
				C       += item.getC();
				C_MINUS += item.getC_MINUS();
				D_PLUS  += item.getD_PLUS();
				D       += item.getD();
				F       += item.getF();
			}

			StringBuilder itemOtherInstructors = new StringBuilder();
			for(Iterator i = otherInstructors.iterator(); i.hasNext();) {
				itemOtherInstructors.append(i.next());
				itemOtherInstructors.append("\n");
			}
			newItem.setOtherInstructors(itemOtherInstructors.toString().trim());

			StringBuilder itemAssistants = new StringBuilder();
			for(Iterator i = assistants.iterator(); i.hasNext();) {
				itemAssistants.append(i.next());
				itemAssistants.append("\n");
			}
			newItem.setAssistants(itemAssistants.toString().trim());

			newItem.setA      (A);
			newItem.setA_MINUS(A_MINUS);
			newItem.setB_PLUS (B_PLUS);
			newItem.setB      (B);
			newItem.setB_MINUS(B_MINUS);
			newItem.setC_PLUS (C_PLUS);
			newItem.setC      (C);
			newItem.setC_MINUS(C_MINUS);
			newItem.setD_PLUS (D_PLUS);
			newItem.setD      (D);
			newItem.setF      (F);

			newItem.setCode(mergeCodes(codes));
			newItem.setDateCreated(new Date());
			dao.save(newItem);

			iter = items.iterator();
			while(iter.hasNext()) {
				CourseArchiveItem item = iter.next();
				dao.updateItemId(CourseArchiveStudent.class,    item.getId(), newItem.getId());
				dao.updateItemId(CourseArchiveAssignment.class, item.getId(), newItem.getId());
				dao.updateItemId(CourseArchiveSyllabus.class,   item.getId(), newItem.getId());
				dao.delete(item);
			}
		} else {
			throw new SecurityException("Current user cannot merge items because they do not have permission");
		}
	}

	public Set getSyllabiForSiteId(String siteId) {
		SyllabusItem syllabusItem = externalLogic.getSyllabusItemBySiteId(siteId);
		if(syllabusItem != null) {
			return externalLogic.getSyllabiForSyllabusItem(syllabusItem);
		} else {
			return new HashSet();
		}
	}

	public void archiveSyllabi(CourseArchiveItem item, SyllabusData syllabusData) {
		if(!canWriteItem(item, externalLogic.getCurrentUserId())) {
			throw new SecurityException("Current user cannot update item " + item.getId() + " because they do not have permission");
		}

		CourseArchiveSyllabus syllabus = new CourseArchiveSyllabus(item, syllabusData.getTitle(), syllabusData.getAsset());
		dao.save(syllabus);

		Set attachments = externalLogic.getSyllabusAttachmentsForSyllabusData(syllabusData);

		for(Iterator iter = attachments.iterator(); iter.hasNext();) {
			SyllabusAttachment syllabusAttachment = (SyllabusAttachment)iter.next();
			String oldId = syllabusAttachment.getAttachmentId();
			ContentResource newAttachment = externalLogic.copyAttachment(oldId);

			if(newAttachment != null) {
				CourseArchiveAttachment attachment = new CourseArchiveAttachment(syllabus);
				attachment.setName(newAttachment.getProperties().getProperty(ResourceProperties.PROP_DISPLAY_NAME));
				attachment.setType(newAttachment.getContentType());
				attachment.setResourceId(newAttachment.getId());
				attachment.setResourceURL(newAttachment.getUrl());
				dao.save(attachment);
			}
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
