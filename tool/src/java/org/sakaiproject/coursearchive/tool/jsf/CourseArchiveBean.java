/******************************************************************************
 * CourseArchiveBean.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2008 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.coursearchive.tool.jsf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursearchive.logic.ExternalLogic;
import org.sakaiproject.coursearchive.logic.CourseArchiveLogic;
import org.sakaiproject.coursearchive.model.CourseArchiveItem;

/**
 * This is a backing bean for the JSF app which handles the events and
 * sends the information from the logic layer to the UI
 * @author Sakai App Builder -AZ
 */
public class CourseArchiveBean {

	private static Log log = LogFactory.getLog(CourseArchiveBean.class);

	private DataModel itemsModel;

	private DataModel itemStudents;
	private DataModel itemAssignments;
	private CourseArchiveItemWrapper currentItem = null;
	private CourseArchiveLogic logic;
	private ExternalLogic externalLogic;

	private String itemCode;
	private String itemName;
	private String itemTerm;
	private String itemPrimaryInstructor;
	private String itemOtherInstructors;
	private String itemAssistants;
	private String itemComments;
	private long itemEnrollment;
	private boolean itemPublic;
	private boolean itemCanEdit;

	private boolean hasMore = true;

	private String searchQuery;

	public CourseArchiveBean() {
	}

	/**
	 * Actions
	 */

	public String processActionDelete() {
		FacesContext fc = FacesContext.getCurrentInstance();
		List items = (List) itemsModel.getWrappedData();
		int itemsRemoved = 0;

		for(Iterator iter = items.iterator(); iter.hasNext();) {
			CourseArchiveItemWrapper wrapper = (CourseArchiveItemWrapper)iter.next();
			if(wrapper.isSelected()) {
				logic.removeItem(wrapper.getItem());
				itemsRemoved++;
			}
		}

		String message = "Removed " + itemsRemoved + " items";
		fc.addMessage("items", new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
		return "deletedItems";
	}

	public String processActionEdit() {
		if(currentItem == null) { loadCurrentItem(); }
		return "editItem";
	}

	public String processActionList() {
		currentItem = null;
		hasMore     = true;
		return "listItems";
	}

	public String processActionSearch() {
		currentItem = null;

		if(searchQuery == null || searchQuery == "") {
			itemsModel = wrapItems(logic.getUserItems());
			hasMore    = true;
		} else {
			itemsModel = wrapItems(logic.searchItems(searchQuery));
			hasMore    = false;
		}

		return "listItems";
	}

	public String processActionShow() {
		if(currentItem == null) { loadCurrentItem(); }
		itemAssignments = new ListDataModel(logic.getItemAssignments(currentItem.getItem()));
		return "showItem";
	}

	public String processActionShowOlder() {
		currentItem = null;
		itemsModel  = wrapItems(logic.getUserItems(true));
		hasMore     = false;
		return "listItems";
	}

	public String processActionShowRoster() {
		itemStudents = new ListDataModel(logic.getItemStudents(currentItem.getItem()));
		return "showRoster";
	}

	public String processActionUpdate() {
		FacesContext fc = FacesContext.getCurrentInstance();

		if(itemCode != null && !itemCode.equals("") &&
		   itemName != null && !itemName.equals("") &&
		   itemTerm != null && !itemTerm.equals("") &&
		   itemPrimaryInstructor != null && !itemPrimaryInstructor.equals("")) {
			CourseArchiveItem item = currentItem.getItem();

			item.setCode(itemCode);
			item.setName(itemName);
			item.setTerm(itemTerm);
			item.setPrimaryInstructor(itemPrimaryInstructor);
			item.setOtherInstructors(itemOtherInstructors);
			item.setAssistants(itemAssistants);
			item.setComments(itemComments);
			item.setPublic(itemPublic);

			logic.saveItem(item);

			String message = "Updated item: " + item.getTitle();
			fc.addMessage("items", new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));

			resetItem();
		} else {
			String message = "Could not add item without a code, name, term or primary instructor.";
			fc.addMessage("items", new FacesMessage(FacesMessage.SEVERITY_WARN, message, message));
		}

		return "updatedItem";
	}

	/**
	 * Item Management
	 */

	public DataModel getItems() {
		if(itemsModel == null) {
			itemsModel = wrapItems(logic.getUserItems());
		}

		return itemsModel;
	}

	public String getItemTitle() {
		return currentItem.getItem().getTitle();
	}

	public DataModel wrapItems(List items) {
		log.debug("wrapping items for JSF datatable...");
		List<CourseArchiveItemWrapper> wrappedItems = new ArrayList<CourseArchiveItemWrapper>();

		for(Iterator iter = items.iterator(); iter.hasNext();) {
			CourseArchiveItem item = (CourseArchiveItem)iter.next();
			item.setEnrollment(logic.getItemEnrollment(item));
			CourseArchiveItemWrapper wrapper = new CourseArchiveItemWrapper(item);

			// Mark the item if the current user owns it and can delete it
			if(logic.canWriteItem(wrapper.getItem(), externalLogic.getCurrentUserId())) {
				wrapper.setCanEdit(true);
			} else {
				wrapper.setCanEdit(false);
			}

			wrappedItems.add(wrapper);
		}

		return new ListDataModel(wrappedItems);
	}

	public void resetItem() {
		currentItem = null;
		itemCode = "";
		itemName = "";
		itemTerm = "";
		itemPrimaryInstructor = getCurrentUserDisplayName();
		itemOtherInstructors = "";
		itemAssistants = "";
		itemEnrollment = 0;
		itemComments = "";
		itemPublic = false;
		itemStudents = null;
	}

	private void loadCurrentItem() {
		currentItem = (CourseArchiveItemWrapper)itemsModel.getRowData();
		CourseArchiveItem item = currentItem.getItem();

		itemCode       = item.getCode();
		itemName       = item.getName();
		itemTerm       = item.getTerm();
		itemEnrollment = item.getEnrollment();
		itemComments   = item.getComments();
		itemPublic     = item.isPublic();

		itemPrimaryInstructor = item.getPrimaryInstructor();
		itemOtherInstructors  = item.getOtherInstructors();
		itemAssistants        = item.getAssistants();

		itemCanEdit = currentItem.isCanEdit();
	}

	/**
	 * User Management
	 */

	public String getCurrentUserDisplayName() {
		return externalLogic.getUserDisplayName(externalLogic.getCurrentUserId());
	}

	public Boolean getUserCanDeleteItems() {
		return logic.canDeleteItems(externalLogic.getCurrentUserId());
	}

	/**
	 * Getters and Setters
	 */
	public void setLogic(CourseArchiveLogic logic) {
		this.logic = logic;
	}
	public void setExternalLogic(ExternalLogic externalLogic) {
		this.externalLogic = externalLogic;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemTerm() {
		return itemTerm;
	}
	public void setItemTerm(String itemTerm) {
		this.itemTerm = itemTerm;
	}
	public String getItemPrimaryInstructor() {
		return itemPrimaryInstructor;
	}
	public void setItemPrimaryInstructor(String itemPrimaryInstructor) {
		this.itemPrimaryInstructor = itemPrimaryInstructor;
	}
	public String getItemOtherInstructors() {
		return itemOtherInstructors;
	}
	public void setItemOtherInstructors(String itemOtherInstructors) {
		this.itemOtherInstructors = itemOtherInstructors;
	}
	public String getItemAssistants() {
		return itemAssistants;
	}
	public void setItemAssistants(String itemAssistants) {
		this.itemAssistants = itemAssistants;
	}
	public boolean getItemPublic() {
		return itemPublic;
	}
	public void setItemPublic(boolean itemPublic) {
		this.itemPublic = itemPublic;
	}
	public long getItemEnrollment() {
		return itemEnrollment;
	}
	public String getItemComments() {
		return itemComments;
	}
	public void setItemComments(String itemComments) {
		this.itemComments = itemComments;
	}
	public boolean getItemCanEdit() {
		return itemCanEdit;
	}
	public String getSearchQuery() {
		return searchQuery;
	}
	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}
	public DataModel getItemStudents() {
		return itemStudents;
	}
	public DataModel getItemAssignments() {
		return itemAssignments;
	}
	public boolean isHasMore() {
		return hasMore;
	}
}
