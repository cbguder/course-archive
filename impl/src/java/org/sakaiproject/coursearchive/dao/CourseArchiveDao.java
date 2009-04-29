/******************************************************************************
 * CourseArchiveDao.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2008 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.coursearchive.dao;

import java.util.List;

import org.sakaiproject.genericdao.api.GeneralGenericDao;

import org.sakaiproject.coursearchive.model.CourseArchiveItem;

/**
 * This is a specialized DAO that allows the developer to extend
 * the functionality of the generic dao package
 * @author Sakai App Builder -AZ
 */
public interface CourseArchiveDao extends GeneralGenericDao {
	public int updateItemId(Class<?> type, Long oldItemId, Long newItemId);
	public int deleteByItemId(Class<?> type, Long itemId);
	public int deleteBySyllabusId(Class<?> type, Long syllabusId);
	public List<CourseArchiveItem> getUserItems(String userId);
	public List<CourseArchiveItem> getUserItems(String userId, String term);
}
