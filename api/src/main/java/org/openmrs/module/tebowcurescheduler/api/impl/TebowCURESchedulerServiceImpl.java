/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.tebowcurescheduler.api.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.tebowcurescheduler.api.TebowCURESchedulerService;
import org.openmrs.module.tebowcurescheduler.api.dao.TebowCURESchedulerDao;

public class TebowCURESchedulerServiceImpl extends BaseOpenmrsService implements TebowCURESchedulerService {
	
	TebowCURESchedulerDao dao;
	
	UserService userService;
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setDao(TebowCURESchedulerDao dao) {
		this.dao = dao;
	}
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Override
	public Encounter createAdmissionRequestEncounter(Obs trigger) {
		
		Person p = trigger.getPerson();
		EncounterService es = Context.getEncounterService();
		
		ConceptService cs = Context.getService(ConceptService.class);
		
		Obs dispositionSet = new Obs();
		dispositionSet.setPerson(trigger.getPerson());
		dispositionSet.setObsDatetime(new Date());
		dispositionSet.setConcept(cs.getConcept(26));
		
		Obs disposition = new Obs();
		disposition.setConcept(cs.getConcept(25));
		disposition.setValueCoded(cs.getConcept(22));
		disposition.setPerson(trigger.getPerson());
		disposition.setObsDatetime(new Date());
		
		Obs dispositionNote = new Obs();
		dispositionNote.setConcept(cs.getConcept(27));
		dispositionNote.setValueText("Automated request for admission after " + trigger.getCreator().getDisplayString() + " filled out the clinicatl cover form for patient " + p.getFamilyName() + " " + p.getMiddleName() + " " + p.getGivenName());
		dispositionNote.setPerson(trigger.getPerson());
		dispositionNote.setObsDatetime(new Date());
		
		dispositionSet.addGroupMember(disposition);
		dispositionSet.addGroupMember(dispositionNote);
		
		Encounter e = new Encounter();
		e.addObs(dispositionSet);
		e.addObs(disposition);
		e.addObs(dispositionNote);
		
		e.setCreator(trigger.getCreator());
		e.setLocation(trigger.getLocation());
		e.setEncounterType(es.getEncounterType(1));
		
		e.setPatient(Context.getPatientService().getPatient(trigger.getPersonId()));
		e.setEncounterDatetime(new Date());
		
		es.saveEncounter(e);
		return e;
	}
	
	@Override
	public List<Obs> getRecentClinicalCoverObs(int repeatInterval) {
		List<Obs> newObs = new ArrayList<Obs>();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -repeatInterval);
		Date minutesBack = cal.getTime();
		
		List<Obs> recentClinicalCoverObs = Context.getObsService().getObservationsByPersonAndConcept(null, Context.getConceptService().getConcept(3820));
		for (Obs obs : recentClinicalCoverObs) {
			if (obs.getDateCreated().after(minutesBack)) {
				newObs.add(obs);
			}
		}
		
		return newObs;
	}
}
