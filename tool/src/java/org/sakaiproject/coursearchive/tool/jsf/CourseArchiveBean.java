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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.api.app.syllabus.SyllabusData;

import org.sakaiproject.coursearchive.logic.CourseArchiveLogic;
import org.sakaiproject.coursearchive.logic.ExternalLogic;

import org.sakaiproject.coursearchive.model.CourseArchiveAssignment;
import org.sakaiproject.coursearchive.model.CourseArchiveAssignmentType;
import org.sakaiproject.coursearchive.model.CourseArchiveItem;
import org.sakaiproject.coursearchive.model.CourseArchiveSyllabus;

/**
 * This is a backing bean for the JSF app which handles the events and
 * sends the information from the logic layer to the UI
 * @author Sakai App Builder -AZ
 */
public class CourseArchiveBean {

	private static Log log = LogFactory.getLog(CourseArchiveBean.class);

	private List<SelectItem> assignmentTypes;

	private DataModel itemsModel;
	private DataModel itemStudents;
	private DataModel itemAssignments;
	private DataModel itemSyllabi;
	private DataModel siteSyllabi;
	private DataModel syllabusAttachments;

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
	private String itemSyllabusURL;
	private String itemDelegateEid;
	private String itemDelegateName;
	private long itemEnrollment;
	private boolean itemPublic;
	private boolean itemCanEdit;

	private CourseArchiveSyllabus currentSyllabus;
	private Object newAttachment;

	private int itemA;
	private int itemA_MINUS;
	private int itemB_PLUS;
	private int itemB;
	private int itemB_MINUS;
	private int itemC_PLUS;
	private int itemC;
	private int itemC_MINUS;
	private int itemD_PLUS;
	private int itemD;
	private int itemF;

	private boolean hasMore = true;

	private String searchQuery;

	public CourseArchiveBean() {
	}

	/**
	 * Actions
	 */

	public String processActionAddAssignment() {
		List assignments = (List)itemAssignments.getWrappedData();
		CourseArchiveAssignment assignment = new CourseArchiveAssignment(currentItem.getItem());
		CourseArchiveWrapper<CourseArchiveAssignment> wrapper = new CourseArchiveWrapper<CourseArchiveAssignment>(assignment);
		assignments.add(wrapper);
		itemAssignments.setWrappedData(assignments);

		return "editItem";
	}

	public String processActionAddSyllabus() {
		currentSyllabus = new CourseArchiveSyllabus(currentItem.getItem());
		return "addSyllabus";
	}

	public String processActionSaveSyllabus() {
		String title = currentSyllabus.getTitle();

		if(title == null || title.equals("")) {
			String message = "Cannot save syllabus without a title";
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage("itemDetails", new FacesMessage(FacesMessage.SEVERITY_WARN, message, message));
			return "syllabusSaved";
		}

		logic.saveSyllabus(currentSyllabus);

		if(newAttachment != null) {
			try {
				FileItem item = (FileItem)newAttachment;
				String name = item.getName();
				String contentType = item.getContentType();
				byte[] content = item.get();

				logic.createAttachment(currentSyllabus, name, contentType, content);
			} catch(Exception e) {
				e.printStackTrace();
			}

			newAttachment = null;
		}

		itemSyllabi = wrapSyllabi(logic.getItemSyllabi(currentItem.getItem()));

		return "syllabusSaved";
	}

	public String processActionDelete() {
		FacesContext fc = FacesContext.getCurrentInstance();
		List<CourseArchiveItemWrapper> items = (List<CourseArchiveItemWrapper>)itemsModel.getWrappedData();
		int itemsRemoved = 0;

		for(CourseArchiveItemWrapper wrapper:items) {
			if(wrapper.isSelected()) {
				logic.removeItem(wrapper.getItem());
				itemsRemoved++;
			}
		}

		String message = "Removed " + itemsRemoved + " items";
		fc.addMessage("items", new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));

		processActionSearch();
		return "deletedItems";
	}

	public String processActionEdit() {
		if(currentItem == null) { loadCurrentItem(); }

		if(assignmentTypes == null) {
			assignmentTypes = new ArrayList<SelectItem>();
			List<CourseArchiveAssignmentType> types = logic.getAssignmentTypes();

			for(CourseArchiveAssignmentType type:types) {
				assignmentTypes.add(new SelectItem(type, type.getName()));
			}
		}

		return "editItem";
	}

	public String processActionList() {
		searchQuery = null;
		currentItem = null;
		hasMore     = true;
		return "listItems";
	}

	public String processActionSearch() {
		currentItem = null;

		if(searchQuery == null || searchQuery.equals("")) {
			itemsModel = wrapItems(logic.getUserItems());
			hasMore    = true;
		} else {
			itemsModel = wrapItems(logic.searchItems(searchQuery));
			hasMore    = false;
		}

		return "listItems";
	}

	public String processActionShow() {
		if(currentItem == null)
			loadCurrentItem();
		else
			reloadCurrentItem();

		itemAssignments = wrapAssignments(logic.getItemAssignments(currentItem.getItem()));
		itemSyllabi = wrapSyllabi(logic.getItemSyllabi(currentItem.getItem()));
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

	public String processActionShowSyllabus() {
		currentSyllabus = ((CourseArchiveWrapper<CourseArchiveSyllabus>)itemSyllabi.getRowData()).getItem();
		syllabusAttachments = new ListDataModel(logic.getSyllabusAttachments(currentSyllabus));
		return "showSyllabus";
	}

	public String processActionUpdate() {
		FacesContext fc = FacesContext.getCurrentInstance();

		if(itemCode != null && !itemCode.equals("") &&
		   itemName != null && !itemName.equals("") &&
		   itemTerm != null && !itemTerm.equals("")) {
			CourseArchiveItem item = currentItem.getItem();

			item.setCode(itemCode);
			item.setName(itemName);
			item.setTerm(itemTerm);
			item.setPrimaryInstructor(itemPrimaryInstructor);
			item.setOtherInstructors(itemOtherInstructors);
			item.setAssistants(itemAssistants);
			item.setComments(itemComments);
			item.setSyllabusURL(itemSyllabusURL);
			item.setDelegateId(externalLogic.getUserId(itemDelegateEid));
			item.setPublic(itemPublic);

			item.setA(itemA);
			item.setA_MINUS(itemA_MINUS);
			item.setB_PLUS(itemB_PLUS);
			item.setB(itemB);
			item.setB_MINUS(itemB_MINUS);
			item.setC_PLUS(itemC_PLUS);
			item.setC(itemC);
			item.setC_MINUS(itemC_MINUS);
			item.setD_PLUS(itemD_PLUS);
			item.setD(itemD);
			item.setF(itemF);

			logic.saveItem(item);

			List<CourseArchiveWrapper> assignments = (List<CourseArchiveWrapper>)itemAssignments.getWrappedData();
			for(CourseArchiveWrapper<CourseArchiveAssignment> wrapper:assignments) {
				CourseArchiveAssignment assignment = wrapper.getItem();

				if(wrapper.isSelected()) {
					Long id = assignment.getId();
					if(id != null && id != 0) {
						logic.removeAssignment(assignment);
					}
				} else {
					if(assignment.getType() != null) {
						logic.saveAssignment(assignment);
					}
				}
			}

			List<CourseArchiveWrapper> syllabi = (List<CourseArchiveWrapper>)itemSyllabi.getWrappedData();
			for(CourseArchiveWrapper<CourseArchiveSyllabus> wrapper:syllabi) {
				CourseArchiveSyllabus syllabus = wrapper.getItem();

				if(wrapper.isSelected()) {
					logic.removeSyllabus(syllabus);
				} else {
					String title = syllabus.getTitle();
					if(title != null && !title.equals("")) {
						logic.saveSyllabus(syllabus);
					}
				}
			}

			String message = "Updated item: " + item.getTitle();
			fc.addMessage("itemDetails", new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));

			reloadCurrentItem();
			itemAssignments = wrapAssignments(logic.getItemAssignments(currentItem.getItem()));
			itemSyllabi = wrapSyllabi(logic.getItemSyllabi(currentItem.getItem()));

			return "updatedItem";
		} else {
			String message = "Cannot save item without a code, name or term.";
			fc.addMessage("itemDetails", new FacesMessage(FacesMessage.SEVERITY_WARN, message, message));

			return "updateFailed";
		}
	}

	public String processActionMerge() {
		FacesContext fc = FacesContext.getCurrentInstance();
		List<CourseArchiveItemWrapper> items = (List<CourseArchiveItemWrapper>)itemsModel.getWrappedData();
		List<CourseArchiveItem> toMerge = new ArrayList<CourseArchiveItem>();

		for(CourseArchiveItemWrapper wrapper:items) {
			if(wrapper.isSelected()) {
				toMerge.add(wrapper.getItem());
			}
		}

		if(toMerge.size() < 2) {
			String message = "Select at least 2 items to merge.";
			fc.addMessage("items", new FacesMessage(FacesMessage.SEVERITY_WARN, message, message));
			return "mergedItems";
		}

		logic.mergeItems(toMerge);

		String message = "Merged " + toMerge.size() + " items";
		fc.addMessage("items", new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));

		processActionSearch();
		return "mergedItems";
	}

	public String processActionSelectSyllabi() {
		ArrayList siteSyllabiList = new ArrayList();
		Set<SyllabusData> siteSyllabiSet = logic.getSyllabiForSiteId(currentItem.getItem().getSiteId());

		for(SyllabusData syllabusData:siteSyllabiSet) {
			siteSyllabiList.add(new CourseArchiveWrapper<SyllabusData>(syllabusData));
		}

		siteSyllabi = new ListDataModel(siteSyllabiList);
		return "selectSyllabi";
	}

	public String processActionArchiveSyllabi() {
		int archivedCount = 0;

		List<CourseArchiveWrapper> syllabi = (List<CourseArchiveWrapper>)siteSyllabi.getWrappedData();
		for(CourseArchiveWrapper<SyllabusData> wrapper:syllabi) {
			if(wrapper.isSelected()) {
				logic.archiveSyllabi(currentItem.getItem(), wrapper.getItem());
				archivedCount++;
			}
		}

		itemSyllabi = wrapSyllabi(logic.getItemSyllabi(currentItem.getItem()));
		String message = "Archived " + archivedCount + " syllabus items.";

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage("itemDetails", new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));

		return "archivedSyllabi";
	}

	public String processUpload(ValueChangeEvent event) {
		newAttachment = event.getNewValue();
		return "";
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

	public DataModel wrapItems(List<CourseArchiveItem> items) {
		log.debug("wrapping items for JSF datatable...");
		List<CourseArchiveItemWrapper> wrappedItems = new ArrayList<CourseArchiveItemWrapper>();

		for(CourseArchiveItem item:items) {
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

	public DataModel wrapAssignments(List<CourseArchiveAssignment> assignments) {
		log.debug("wrapping assignments for JSF datatable...");
		List<CourseArchiveWrapper> wrappedAssignments = new ArrayList<CourseArchiveWrapper>();

		for(CourseArchiveAssignment item:assignments) {
			CourseArchiveWrapper<CourseArchiveAssignment> wrapper = new CourseArchiveWrapper<CourseArchiveAssignment>(item);
			wrappedAssignments.add(wrapper);
		}

		return new ListDataModel(wrappedAssignments);
	}

	public DataModel wrapSyllabi(List<CourseArchiveSyllabus> syllabi) {
		log.debug("wrapping syllabi for JSF datatable...");
		List<CourseArchiveWrapper> wrappedSyllabi = new ArrayList<CourseArchiveWrapper>();

		for(CourseArchiveSyllabus item:syllabi) {
			CourseArchiveWrapper<CourseArchiveSyllabus> wrapper = new CourseArchiveWrapper<CourseArchiveSyllabus>(item);
			wrappedSyllabi.add(wrapper);
		}

		return new ListDataModel(wrappedSyllabi);
	}

	private void loadCurrentItem() {
		currentItem = (CourseArchiveItemWrapper)itemsModel.getRowData();
		reloadCurrentItem();
	}

	private void reloadCurrentItem() {
		CourseArchiveItem item = currentItem.getItem();

		itemCode         = item.getCode();
		itemName         = item.getName();
		itemTerm         = item.getTerm();
		itemEnrollment   = item.getEnrollment();
		itemComments     = item.getComments();
		itemSyllabusURL  = item.getSyllabusURL();
		itemDelegateEid  = externalLogic.getUserEid(item.getDelegateId());
		itemDelegateName = externalLogic.getUserDisplayName(item.getDelegateId());
		itemPublic       = item.isPublic();

		itemPrimaryInstructor = item.getPrimaryInstructor();
		itemOtherInstructors  = item.getOtherInstructors();
		itemAssistants        = item.getAssistants();

		itemA       = item.getA();
		itemA_MINUS = item.getA_MINUS();
		itemB_PLUS  = item.getB_PLUS();
		itemB       = item.getB();
		itemB_MINUS = item.getB_MINUS();
		itemC_PLUS  = item.getC_PLUS();
		itemC       = item.getC();
		itemC_MINUS = item.getC_MINUS();
		itemD_PLUS  = item.getD_PLUS();
		itemD       = item.getD();
		itemF       = item.getF();

		itemCanEdit = currentItem.isCanEdit();
	}

	public CourseArchiveAssignmentType getAssignmentTypeById(Long id) {
		return logic.getAssignmentTypeById(id);
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
	public List<SelectItem> getAssignmentTypes() {
		return assignmentTypes;
	}
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
	public String getItemSyllabusURL() {
		return itemSyllabusURL;
	}
	public void setItemSyllabusURL(String itemSyllabusURL) {
		this.itemSyllabusURL = itemSyllabusURL;
	}
	public String getItemDelegateEid() {
		return itemDelegateEid;
	}
	public void setItemDelegateEid(String itemDelegateEid) {
		this.itemDelegateEid = itemDelegateEid;
	}
	public String getItemDelegateName() {
		return itemDelegateName;
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
	public DataModel getItemSyllabi() {
		return itemSyllabi;
	}
	public DataModel getSiteSyllabi() {
		return siteSyllabi;
	}
	public DataModel getSyllabusAttachments() {
		return syllabusAttachments;
	}
	public CourseArchiveSyllabus getCurrentSyllabus() {
		return currentSyllabus;
	}
	public boolean isHasMore() {
		return hasMore;
	}
	public int getItemA()       { return itemA; }
	public int getItemA_MINUS() { return itemA_MINUS; }
	public int getItemB_PLUS()  { return itemB_PLUS; }
	public int getItemB()       { return itemB; }
	public int getItemB_MINUS() { return itemB_MINUS; }
	public int getItemC_PLUS()  { return itemC_PLUS; }
	public int getItemC()       { return itemC; }
	public int getItemC_MINUS() { return itemC_MINUS; }
	public int getItemD_PLUS()  { return itemD_PLUS; }
	public int getItemD()       { return itemD; }
	public int getItemF()       { return itemF; }
	public void setItemA(int itemA)             { this.itemA = itemA; }
	public void setItemA_MINUS(int itemA_MINUS) { this.itemA_MINUS = itemA_MINUS; }
	public void setItemB_PLUS(int itemB_PLUS)   { this.itemB_PLUS = itemB_PLUS; }
	public void setItemB(int itemB)             { this.itemB = itemB; }
	public void setItemB_MINUS(int itemB_MINUS) { this.itemB_MINUS = itemB_MINUS; }
	public void setItemC_PLUS(int itemC_PLUS)   { this.itemC_PLUS = itemC_PLUS; }
	public void setItemC(int itemC)             { this.itemC = itemC; }
	public void setItemC_MINUS(int itemC_MINUS) { this.itemC_MINUS = itemC_MINUS; }
	public void setItemD_PLUS(int itemD_PLUS)   { this.itemD_PLUS = itemD_PLUS; }
	public void setItemD(int itemD)             { this.itemD = itemD; }
	public void setItemF(int itemF)             { this.itemF = itemF; }
}
