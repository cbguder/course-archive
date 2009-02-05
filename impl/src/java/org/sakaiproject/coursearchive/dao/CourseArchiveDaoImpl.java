/******************************************************************************
 * CourseArchiveDaoImpl.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2008 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.coursearchive.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.genericdao.hibernate.HibernateGeneralGenericDao;

import org.sakaiproject.coursearchive.dao.CourseArchiveDao;

/**
 * Implementations of any specialized DAO methods from the specialized DAO 
 * that allows the developer to extend the functionality of the generic dao package
 * @author Sakai App Builder -AZ
 */
public class CourseArchiveDaoImpl 
   extends HibernateGeneralGenericDao 
      implements CourseArchiveDao {

   private static Log log = LogFactory.getLog(CourseArchiveDaoImpl.class);

   public void init() {
      log.debug("init");
   }

}
