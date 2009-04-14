package org.sakaiproject.coursearchive.model;

import java.util.Date;

public class CourseArchiveSyllabus {
	private Long id;
	private String title;
	private String asset;

	private CourseArchiveItem item;

	/**
	 * Default constructor
	 */
	public CourseArchiveSyllabus() {
		this(null, null, null);
	}

	public CourseArchiveSyllabus(CourseArchiveItem item) {
		this(item, null, null);
	}

	/**
	 * Full constructor
	 */
	public CourseArchiveSyllabus(CourseArchiveItem item, String title, String asset) {
		this.item  = item;
		this.title = title;
		this.asset = asset;
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
	public String getAsset() {
		return asset;
	}
	public void setAsset(String asset) {
		this.asset = asset;
	}
	public CourseArchiveItem getItem() {
		return item;
	}
	public void setItem(CourseArchiveItem item) {
		this.item = item;
	}
}
