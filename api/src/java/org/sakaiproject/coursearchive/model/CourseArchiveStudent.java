package org.sakaiproject.coursearchive.model;

import java.util.Date;

public class CourseArchiveStudent {
	private Long id;
	private Long sid;
	private String uid;
	private String email;
	private String name;

	private CourseArchiveItem item;

	/**
	 * Default constructor
	 */
	public CourseArchiveStudent() {
		this(null, null, null, null);
	}

	/**
	 * Full constructor
	 */
	public CourseArchiveStudent(Long sid, String uid, String email, String name) {
		this.sid   = sid;
		this.uid   = uid;
		this.email = email;
		this.name  = name;
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
	public Long getSid() {
		return sid;
	}
	public void setSid(Long sid) {
		this.sid = sid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public CourseArchiveItem getItem() {
		return item;
	}
	public void setItem(CourseArchiveItem item) {
		this.item = item;
	}
}
