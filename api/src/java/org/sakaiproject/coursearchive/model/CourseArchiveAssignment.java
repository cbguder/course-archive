package org.sakaiproject.coursearchive.model;

import java.util.Date;

public class CourseArchiveAssignment {
	private Long id;
	private CourseArchiveAssignmentType type;
	private double maxGrade;
	private double meanGrade;
	private double medianGrade;
	private double standardDeviation;
	private double weight;
	private Date date;

	private CourseArchiveItem item;

	/**
	 * Default constructor
	 */
	public CourseArchiveAssignment() {
		this(null);
	}

	public CourseArchiveAssignment(CourseArchiveItem item) {
		this(item, null, 100.0, 0.0, 0.0, 0.0, 0.0, new Date());
	}

	/**
	 * Full constructor
	 */
	public CourseArchiveAssignment(CourseArchiveItem item, CourseArchiveAssignmentType type, double maxGrade, double meanGrade, double medianGrade, double standardDeviation, double weight, Date date) {
		this.item              = item;
		this.type              = type;
		this.maxGrade          = maxGrade;
		this.meanGrade         = meanGrade;
		this.medianGrade       = medianGrade;
		this.standardDeviation = standardDeviation;
		this.date              = date;
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
	public CourseArchiveAssignmentType getType() {
		return type;
	}
	public void setType(CourseArchiveAssignmentType type) {
		this.type = type;
	}
	public double getMaxGrade() {
		return maxGrade;
	}
	public void setMaxGrade(double maxGrade) {
		this.maxGrade = maxGrade;
	}
	public double getMeanGrade() {
		return meanGrade;
	}
	public void setMeanGrade(double meanGrade) {
		this.meanGrade = meanGrade;
	}
	public double getMedianGrade() {
		return medianGrade;
	}
	public void setMedianGrade(double medianGrade) {
		this.medianGrade = medianGrade;
	}
	public double getStandardDeviation() {
		return standardDeviation;
	}
	public void setStandardDeviation(double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public CourseArchiveItem getItem() {
		return item;
	}
	public void setItem(CourseArchiveItem item) {
		this.item = item;
	}
}
