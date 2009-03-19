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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.Query;

import org.sakaiproject.genericdao.hibernate.HibernateGeneralGenericDao;

import org.sakaiproject.coursearchive.dao.CourseArchiveDao;
import org.sakaiproject.coursearchive.model.CourseArchiveItem;

/**
 * Implementations of any specialized DAO methods from the specialized DAO 
 * that allows the developer to extend the functionality of the generic dao package
 * @author Sakai App Builder -AZ
 */
public class CourseArchiveDaoImpl extends HibernateGeneralGenericDao implements CourseArchiveDao {

	private static Log log = LogFactory.getLog(CourseArchiveDaoImpl.class);

	public void init() {
		log.debug("init");
	}

	public int updateItemId(Class<?> type, Long oldItemId, Long newItemId) {
		String hql = "update " + type.getName() + " set itemId = :newItemId where itemId = :oldItemId";

		Query query = getSession().createQuery(hql);
		query.setLong("newItemId", newItemId);
		query.setLong("oldItemId", oldItemId);

		return query.executeUpdate();
	}

	public int deleteByItemId(Class<?> type, Long itemId) {
		String hql = "delete " + type.getName() + " where itemId = :itemId";

		Query query = getSession().createQuery(hql);
		query.setLong("itemId", itemId);

		return query.executeUpdate();
	}

	public List<CourseArchiveItem> getUserItems(String userId) {
		return getUserItems(userId, null);
	}

	public List<CourseArchiveItem> getUserItems(String userId, String term) {
		String hql = "from " + CourseArchiveItem.class.getName()
		           + " where (ownerId = :userId or delegateId = :userId)";

		if(term != null) {
			hql += " and term > :term";
		}

		hql += " order by term desc";

		Query query = getSession().createQuery(hql);
		query.setString("userId", userId);

		if(term != null) {
			query.setString("term", term);
		}

		return query.list();
	}
}
