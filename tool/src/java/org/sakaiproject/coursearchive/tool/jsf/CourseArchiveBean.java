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
	private boolean itemPublic;
	private int itemEnrollment;

	private Boolean itemCanEdit;

	private String searchQuery;

	public CourseArchiveBean() {
	}

	public DataModel getAllItems() {
		List items = logic.getAllItems();
		return wrapItems(items);
	}

	public DataModel getUserItems() {
		List items = logic.getUserItems();
		return wrapItems(items);
	}

	public DataModel wrapItems(List items) {
		log.debug("wrapping items for JSF datatable...");
		List<CourseArchiveItemWrapper> wrappedItems = new ArrayList<CourseArchiveItemWrapper>();

		for(Iterator iter = items.iterator(); iter.hasNext();) {
			CourseArchiveItemWrapper wrapper = new CourseArchiveItemWrapper((CourseArchiveItem) iter.next());
			// Mark the item if the current user owns it and can delete it
			if(logic.canWriteItem(wrapper.getItem(), externalLogic.getCurrentUserId())) {
				wrapper.setCanEdit(true);
			} else {
				wrapper.setCanEdit(false);
			}
			wrappedItems.add(wrapper);
		}
		itemsModel = new ListDataModel(wrappedItems);
		return itemsModel;
	}

	public String processActionAdd() {
		log.debug("in process action add...");
		FacesContext fc = FacesContext.getCurrentInstance();

		if(itemCode != null && !itemCode.equals("") &&
		   itemName != null && !itemName.equals("") &&
		   itemTerm != null && !itemTerm.equals("") &&
		   itemPrimaryInstructor != null && !itemPrimaryInstructor.equals("")) {
			String message;
			CourseArchiveItem item;

			if(currentItem == null) {
				item = new CourseArchiveItem();
				// ownerId, locationId, and dateCreated are set in the logic.saveItem
				message = "Added new item:" + itemCode;
			} else {
				item = currentItem.getItem();
				message = "Updated item:" + itemCode;
			}

			item.setCode(itemCode);
			item.setName(itemName);
			item.setTerm(itemTerm);
			item.setPrimaryInstructor(itemPrimaryInstructor);
			item.setOtherInstructors(itemOtherInstructors);
			item.setAssistants(itemAssistants);
			item.setComments(itemComments);
			item.setPublic(itemPublic);

			logic.saveItem(item);

			fc.addMessage("items", new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));

			resetItem();
		} else {
			String message = "Could not add item without a code, name, term or primary instructor.";
			fc.addMessage("items", new FacesMessage(FacesMessage.SEVERITY_WARN, message, message));
		}

		return "addedItem";
	}

	public String processActionDelete() {
		log.debug("in process action delete...");
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
		return "deleteItems";
	}

	public String processActionSearch() {
		currentItem = null;
		List items = logic.searchItems(searchQuery);
		wrapItems(items);
		return "listItems";
	}

	public String processActionShowRoster() {
		itemStudents = new ListDataModel(currentItem.getItem().getStudents());
		return "showRoster";
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

	public String processActionUpdate() {
		log.debug("in process action update...");
		if(currentItem == null) { loadCurrentItem(); }
		return "updateItem";
	}

	public String processActionList() {
		log.debug("in process action list...");
		currentItem = null;
		getUserItems();
		return "listItems";
	}
	
	public String processActionShow() {
		log.debug("in process action show...");
		if(currentItem == null) { loadCurrentItem(); }
		return "showItem";
	}

	public String getCurrentUserDisplayName() {
		return externalLogic.getUserDisplayName(externalLogic.getCurrentUserId());
	}

	private void loadCurrentItem() {
		currentItem = (CourseArchiveItemWrapper) itemsModel.getRowData(); // gets the user selected item

		itemCode       = currentItem.getItem().getCode();
		itemName       = currentItem.getItem().getName();
		itemTerm       = currentItem.getItem().getTerm();
		itemEnrollment = currentItem.getItem().getEnrollment();
		itemComments   = currentItem.getItem().getComments();
		itemPublic     = currentItem.getItem().isPublic();

		itemPrimaryInstructor = currentItem.getItem().getPrimaryInstructor();
		itemOtherInstructors  = currentItem.getItem().getOtherInstructors();
		itemAssistants        = currentItem.getItem().getAssistants();

		itemCanEdit    = currentItem.isCanEdit();

		itemAssignments = new ListDataModel(currentItem.getItem().getAssignments());
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
	public int getItemEnrollment() {
		return itemEnrollment;
	}
	public void setItemEnrollment(int itemEnrollment) {
		this.itemEnrollment = itemEnrollment;
	}
	public String getItemComments() {
		return itemComments;
	}
	public void setItemComments(String itemComments) {
		this.itemComments = itemComments;
	}
	public Boolean getItemCanEdit() {
		return itemCanEdit;
	}
	public String getSearchQuery() {
		return searchQuery;
	}
	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}
	public DataModel getItems() {
		if(itemsModel == null || searchQuery == null || searchQuery == "") {
			getUserItems();
		}

		return itemsModel;
	}
	public Boolean getUserCanDeleteItems() {
		return logic.canDeleteItems(externalLogic.getCurrentUserId());
	}
	public DataModel getItemStudents() {
		return itemStudents;
	}
	public DataModel getItemAssignments() {
		return itemAssignments;
	}
	public String getItemTitle() {
		return currentItem.getItem().getTitle();
	}
}
