package org.sakaiproject.coursearchive.tool.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.sakaiproject.coursearchive.model.CourseArchiveAssignmentType;

public class AssignmentTypeConverter implements Converter {
	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		FacesContext currentContext = FacesContext.getCurrentInstance();
		CourseArchiveBean bean = (CourseArchiveBean)currentContext.getApplication().getVariableResolver().resolveVariable(currentContext, "CourseArchiveBean");
		return bean.getAssignmentTypeById(Long.parseLong(value));
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
		if(value == null || !(value instanceof CourseArchiveAssignmentType)) {
			return null;
		}

		return ((CourseArchiveAssignmentType)value).getId().toString();
	}
}
