package org.sakaiproject.coursearchive.tool.jsf;

import org.sakaiproject.api.app.syllabus.SyllabusData;

public class SyllabusDataWrapper {
	private SyllabusData data;
	private boolean isSelected;

	public SyllabusDataWrapper(SyllabusData data) {
		this.data = data;
	}

	public SyllabusData getData() {
		return data;
	}
	public void setData(SyllabusData data) {
		this.data = data;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
