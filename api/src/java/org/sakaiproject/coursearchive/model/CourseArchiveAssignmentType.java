package org.sakaiproject.coursearchive.model;

public class CourseArchiveAssignmentType {
	private Long id;
	private String name;

	public CourseArchiveAssignmentType() {
		this("");
	}

	public CourseArchiveAssignmentType(String name) {
		this.name = name;
	}

	public boolean equals(Object obj) {
		if(!(obj instanceof CourseArchiveAssignmentType))
			return false;

		return id.equals(((CourseArchiveAssignmentType)obj).getId());
	}

	public int hashCode() {
		return id.hashCode();
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
