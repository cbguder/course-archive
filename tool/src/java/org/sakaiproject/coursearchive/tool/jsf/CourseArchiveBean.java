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
	private CourseArchiveItemWrapper currentItem = null;
	private CourseArchiveLogic logic;
	private ExternalLogic externalLogic;

	private String itemText;
	private String itemInstructor;
	private int itemEnrollment;
	private Boolean itemCanDelete;

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
			if(logic.canWriteItem(wrapper.getItem(), externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId())) {
				wrapper.setCanDelete(true);
			} else {
				wrapper.setCanDelete(false);
			}
			wrappedItems.add(wrapper);
		}
		itemsModel = new ListDataModel(wrappedItems);
		return itemsModel;
	}

	public String processActionAdd() {
		log.debug("in process action add...");
		FacesContext fc = FacesContext.getCurrentInstance();

		if(itemText != null && !itemText.equals("")) {
			String message;
			CourseArchiveItem item;

			if(currentItem == null) {
				item = new CourseArchiveItem();
				// ownerId, locationId, and dateCreated are set in the logic.saveItem
				message = "Added new item:" + itemText;
			} else {
				item = currentItem.getItem();
				message = "Updated item:" + itemText;
			}

			item.setTitle(itemText);
			item.setInstructor(itemInstructor);
			item.setEnrollment(itemEnrollment);

			logic.saveItem(item);

			fc.addMessage("items", new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));

			resetItem();
		} else {
			String message = "Could not add item without a title";
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

	public void resetItem() {
		// set the values to the new defaults
		itemText = "";
		itemEnrollment = 0;
		itemInstructor = getCurrentUserDisplayName();
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

		itemText       = currentItem.getItem().getTitle();
		itemEnrollment = currentItem.getItem().getEnrollment();
		itemInstructor = currentItem.getItem().getInstructor();

		itemCanDelete  = currentItem.isCanDelete();
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
	public String getItemText() {
		return itemText;
	}
	public void setItemText(String itemText) {
		this.itemText = itemText;
	}
	public String getItemInstructor() {
		return itemInstructor;
	}
	public void setItemInstructor(String itemInstructor) {
		this.itemInstructor = itemInstructor;
	}
	public int getItemEnrollment() {
		return itemEnrollment;
	}
	public void setItemEnrollment(int itemEnrollment) {
		this.itemEnrollment = itemEnrollment;
	}
	public Boolean getItemCanDelete() {
		return itemCanDelete;
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
}
