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
import java.util.ListIterator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursearchive.logic.ExternalLogic;
import org.sakaiproject.coursearchive.logic.CourseArchiveLogic;
import org.sakaiproject.coursearchive.model.CourseArchiveItem;
import org.sakaiproject.site.api.Site;

/**
 * This is a backing bean for the JSF app which handles the events and
 * sends the information from the logic layer to the UI
 * @author Sakai App Builder -AZ
 */
public class CourseArchiveBean {

	private static Log log = LogFactory.getLog(CourseArchiveBean.class);

	private static final String TEXT_DEFAULT = "";
	private static final Boolean HIDDEN_DEFAULT = Boolean.TRUE;
	
	private DataModel itemsModel;
	private CourseArchiveItemWrapper currentItem = null;

	private CourseArchiveLogic logic;
	public void setLogic(CourseArchiveLogic logic) {
		this.logic = logic;
	}

	private ExternalLogic externalLogic;
	public void setExternalLogic(ExternalLogic externalLogic) {
		this.externalLogic = externalLogic;
	}

	private String itemText = TEXT_DEFAULT;
	public String getItemText() {
		return itemText;
	}
	public void setItemText(String itemText) {
		this.itemText = itemText;
	}

	private Boolean itemHidden = HIDDEN_DEFAULT;
	public Boolean getItemHidden() {
		return itemHidden;
	}
	public void setItemHidden(Boolean itemHidden) {
		this.itemHidden = itemHidden;
	}

	public String getCurrentUserDisplayName() {
		return externalLogic.getUserDisplayName(externalLogic.getCurrentUserId());
	}


	public CourseArchiveBean() {
	}

	public DataModel getAllItems() {
		log.debug("wrapping items for JSF datatable...");
		List wrappedItems = new ArrayList();

		List items = logic.getAllVisibleItems(externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId());
		for (Iterator iter = items.iterator(); iter.hasNext(); ) {
			CourseArchiveItemWrapper wrapper = 
				new CourseArchiveItemWrapper((CourseArchiveItem) iter.next());
			// Mark the item if the current user owns it and can delete it
			if( logic.canWriteItem(wrapper.getItem(), externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId()) ) {
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
		// Test for empty items and don't add them
		if (itemText != null && !itemText.equals("")) {
			String message;
			CourseArchiveItem item;
			if (currentItem == null) {
				item = new CourseArchiveItem();
				// ownerId, locationId, and dateCreated are set in the logic.saveItem
				message = "Added new item:" + itemText;
			} else {
				item = currentItem.getItem();
				message = "Updated item:" + itemText;
			}
			item.setTitle(itemText);
			if (itemHidden == null) { itemHidden = Boolean.FALSE; }
			item.setHidden(itemHidden);

			logic.saveItem(item);

			fc.addMessage("items", new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));

			// Reset text
			itemText = "";
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
		for (Iterator iter = items.iterator(); iter.hasNext(); ) {
			CourseArchiveItemWrapper wrapper = 
				(CourseArchiveItemWrapper) iter.next();
			if(wrapper.isSelected()) {
				logic.removeItem(wrapper.getItem());
				itemsRemoved++;
			}
		}
		String message = "Removed " + itemsRemoved + " items";
		fc.addMessage("items", new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
		return "deleteItems";
	}


	public String processActionNew() {
		log.debug("in process action new...");
		currentItem = null;
		// set the values to the new defaults
		itemText = TEXT_DEFAULT;
		itemHidden = HIDDEN_DEFAULT;
		return "newItem";
	}

	public String processActionUpdate() {
		log.debug("in process action update...");
		currentItem = (CourseArchiveItemWrapper) itemsModel.getRowData(); // gets the user selected item
		// set the values to those of the selected item
		itemText = currentItem.getItem().getTitle();
		itemHidden = currentItem.getItem().getHidden();
		return "updateItem";
	}

	public String processActionList() {
		log.debug("in process action list...");
		return "listItems";
	}

	public List getCurrentUserSites() {
		List sites = externalLogic.getCurrentUserSites();
		ArrayList<SelectItem> items = new ArrayList<SelectItem>(sites.size());

		ListIterator iter = sites.listIterator();
		while(iter.hasNext()) {
			Site s = (Site)iter.next();
			items.add(new SelectItem(s.getId(), s.getTitle()));
		}

		return items;
	}
}
