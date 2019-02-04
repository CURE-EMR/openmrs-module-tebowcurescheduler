/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.tebowcurescheduler.api.dao;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("tebowcurescheduler.TebowCURESchedulerDao")
public class TebowCURESchedulerDao {
	
	/* Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf) {
		this.sessionFactory = sf;
	}
	
	private org.hibernate.Session getCurrentSession() {
		try {
			return sessionFactory.getCurrentSession();
		}
		catch (NoSuchMethodError ex) {
			try {
				Method method = sessionFactory.getClass().getMethod("getCurrentSession", null);
				return (org.hibernate.Session) method.invoke(sessionFactory, null);
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to get the current hibernate session", e);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Obs> getRecentClinicalCoverObs(int repeatInterval, Concept concept) {
		
		/*
		 * just go one minute back since the last time the scheduler run to make sure we
		 * don't miss any new clinical cover obs
		 */
		repeatInterval++;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -repeatInterval);
		Date minutesBack = cal.getTime();
		
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateSting = f.format(minutesBack);
		
		String sql = "SELECT * FROM obs WHERE concept_id = " + concept.getConceptId() + " AND date_created >= \'" + dateSting + "\';";
		SQLQuery query = getCurrentSession().createSQLQuery(sql);
		query.addEntity(Obs.class);
		List<Obs> obsList = query.list();
		return obsList;
	}
	
	public List<Obs> getObservations(Person person, Concept concept, Date dateCreated) {
		
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String dateSting = f.format(dateCreated);
		
		String sql = "SELECT * FROM obs WHERE person_id = " + person.getPersonId() + " AND value_coded = " + concept.getConceptId() + " AND date_created >= \'" + dateSting + "\';";
		SQLQuery query = getCurrentSession().createSQLQuery(sql);
		query.addEntity(Obs.class);
		List<Obs> obsList = query.list();
		return obsList;
	}
	
}
