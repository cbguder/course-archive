/******************************************************************************
 * CourseArchiveItem.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2008 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.coursearchive.model;

import java.util.Date;

/**
 * This is a sample POJO (data storage object) 
 * @author Sakai App Builder -AZ
 */
public class CourseArchiveItem {
	
	private Long id;
	private String title;
	private String ownerId; // Sakai userId
	private Date dateCreated;

	/**
	 * Default constructor
	 */
	public CourseArchiveItem() {
	}

	/**
	 * Minimal constructor
	 */
	public CourseArchiveItem(String title, String ownerId) {
		this.title = title;
		this.ownerId = ownerId;
	}

	/**
	 * Full constructor
	 */
	public CourseArchiveItem(String title, String ownerId, Date dateCreated) {
		this.title = title;
		this.ownerId = ownerId;
		this.dateCreated = dateCreated;
	}

	/**
	 * Getters and Setters
	 */
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
}
