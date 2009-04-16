package org.sakaiproject.coursearchive.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CourseArchiveItem {

	private Long id;
	private String ownerId;    // Sakai userId
	private String delegateId; // Sakai userId
	private Date dateCreated;
	private String siteId;

	private String code;
	private String name;
	private String term;
	private String primaryInstructor;
	private String otherInstructors;
	private String assistants;
	private String comments;
	private String syllabusURL;
	private long enrollment;
	private boolean _public;

	private int A;
	private int A_MINUS;
	private int B_PLUS;
	private int B;
	private int B_MINUS;
	private int C_PLUS;
	private int C;
	private int C_MINUS;
	private int D_PLUS;
	private int D;
	private int F;

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
		this.syllabusURL       = "";
		this.delegateId        = "";
		this._public           = false;
	}

	/**
	 * Copy constructor
	 */
	public CourseArchiveItem(CourseArchiveItem item) {
		this.ownerId = item.ownerId;

		this.code = item.code;
		this.name = item.name;
		this.term = item.term;

		this.primaryInstructor = item.primaryInstructor;
		this.otherInstructors  = item.otherInstructors;

		this.assistants = item.assistants;
		this.comments   = item.comments;
		this.enrollment = item.enrollment;
		this._public    = item._public;

		this.A       = item.A;
		this.A_MINUS = item.A_MINUS;
		this.B_PLUS  = item.B_PLUS;
		this.B       = item.B;
		this.B_MINUS = item.B_MINUS;
		this.C_PLUS  = item.C_PLUS;
		this.C       = item.C;
		this.C_MINUS = item.C_MINUS;
		this.D_PLUS  = item.D_PLUS;
		this.D       = item.D;
		this.F       = item.F;
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
	public String getDelegateId() {
		return delegateId;
	}
	public void setDelegateId(String delegateId) {
		this.delegateId = delegateId;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
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
	public String getSyllabusURL() {
		return syllabusURL;
	}
	public void setSyllabusURL(String syllabusURL) {
		this.syllabusURL = syllabusURL;
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
	public int getA()       { return A; }
	public int getA_MINUS() { return A_MINUS; }
	public int getB_PLUS()  { return B_PLUS; }
	public int getB()       { return B; }
	public int getB_MINUS() { return B_MINUS; }
	public int getC_PLUS()  { return C_PLUS; }
	public int getC()       { return C; }
	public int getC_MINUS() { return C_MINUS; }
	public int getD_PLUS()  { return D_PLUS; }
	public int getD()       { return D; }
	public int getF()       { return F; }
	public void setA(int A)             { this.A = A; }
	public void setA_MINUS(int A_MINUS) { this.A_MINUS = A_MINUS; }
	public void setB_PLUS(int B_PLUS)   { this.B_PLUS = B_PLUS; }
	public void setB(int B)             { this.B = B; }
	public void setB_MINUS(int B_MINUS) { this.B_MINUS = B_MINUS; }
	public void setC_PLUS(int C_PLUS)   { this.C_PLUS = C_PLUS; }
	public void setC(int C)             { this.C = C; }
	public void setC_MINUS(int C_MINUS) { this.C_MINUS = C_MINUS; }
	public void setD_PLUS(int D_PLUS)   { this.D_PLUS = D_PLUS; }
	public void setD(int D)             { this.D = D; }
	public void setF(int F)             { this.F = F; }
}
