/******************************************************************************
 * PreloadDataImpl.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2008 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.coursearchive.dao;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.coursearchive.dao.CourseArchiveDao;
import org.sakaiproject.coursearchive.model.CourseArchiveItem;

/**
 * This checks and preloads any data that is needed for this app
 * @author Sakai App Builder -AZ
 */
public class PreloadDataImpl {

	private static Log log = LogFactory.getLog(PreloadDataImpl.class);

	private CourseArchiveDao dao;
	public void setDao(CourseArchiveDao dao) {
		this.dao = dao;
	}

	public void init() {
		preloadItems();
	}

	/**
	 * Preload some items into the database
	 */
	public void preloadItems() {

		// check if there are any items present, load some if not
		if(dao.findAll(CourseArchiveItem.class).isEmpty()){

			// use the dao to preload some data here
			dao.save( new CourseArchiveItem("Preload Title", "Preload Owner", new Date()) );

			log.info("Preloaded " + dao.countAll(CourseArchiveItem.class) + " items");
		}
	}
}
