package org.sakaiproject.coursearchive.model;

import java.util.Date;

public class CourseArchiveAssignment {
	private Long id;
	private String title;
	private Double points;
	private Double meanGrade;
	private Date dueDate;

	private CourseArchiveItem item;

	/**
	 * Default constructor
	 */
	public CourseArchiveAssignment() {
		this(null, 0.0, 0.0, null);
	}

	/**
	 * Full constructor
	 */
	public CourseArchiveAssignment(String title, Double points, Double meanGrade, Date dueDate) {
		this.title     = title;
		this.points    = points;
		this.meanGrade = meanGrade;
		this.dueDate   = dueDate;
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
	public Double getPoints() {
		return points;
	}
	public void setPoints(Double points) {
		this.points = points;
	}
	public Double getMeanGrade() {
		return meanGrade;
	}
	public void setMeanGrade(Double meanGrade) {
		this.meanGrade = meanGrade;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public CourseArchiveItem getItem() {
		return item;
	}
	public void setItem(CourseArchiveItem item) {
		this.item = item;
	}
}
