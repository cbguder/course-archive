package org.sakaiproject.coursearchive.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CourseArchiveItem {

	private Long id;
	private String ownerId; // Sakai userId
	private Date dateCreated;

	private String code;
	private String name;
	private String term;
	private String primaryInstructor;
	private String otherInstructors;
	private String assistants;
	private String comments;
	private long enrollment;
	private boolean _public;

	/**
	 * Default constructor
	 */
	public CourseArchiveItem() {
		this(null, null, null);
	}

	/**
	 * Minimal constructor
	 */
	public CourseArchiveItem(String title, String ownerId) {
		this(title, ownerId, null);
	}

	public CourseArchiveItem(String title, String ownerId, Date dateCreated) {
		this.ownerId           = ownerId;
		this.dateCreated       = dateCreated;

		this.code              = "";
		this.name              = "";
		this.term              = "";
		this.primaryInstructor = "";
		this.otherInstructors  = "";
		this.assistants        = "";
		this.comments          = "";
		this._public           = false;
	}

	public String getTitle() {
		return code + "-" + term;
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
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getPrimaryInstructor() {
		return primaryInstructor;
	}
	public void setPrimaryInstructor(String primaryInstructor) {
		this.primaryInstructor = primaryInstructor;
	}
	public String getOtherInstructors() {
		return otherInstructors;
	}
	public void setOtherInstructors(String otherInstructors) {
		this.otherInstructors = otherInstructors;
	}
	public String getAssistants() {
		return assistants;
	}
	public void setAssistants(String assistants) {
		this.assistants = assistants;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public long getEnrollment() {
		return enrollment;
	}
	public void setEnrollment(long enrollment) {
		this.enrollment = enrollment;
	}
	public boolean isPublic() {
		return _public;
	}
	public void setPublic(boolean _public) {
		this._public = _public;
	}
}
