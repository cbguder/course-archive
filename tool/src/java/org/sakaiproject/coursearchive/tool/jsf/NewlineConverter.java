package org.sakaiproject.coursearchive.tool.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

public class NewlineConverter implements Converter {
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) throws ConverterException {
		return null;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) throws ConverterException {
		String orig = arg2.toString();
		orig = orig.replaceAll("\r\n", "<br/>");
		orig = orig.replaceAll("\n", "<br/>");
		orig = orig.replaceAll("\r", "<br/>");
		return orig;
	}
}
