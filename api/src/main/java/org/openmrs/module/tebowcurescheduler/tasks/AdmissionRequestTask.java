/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.tebowcurescheduler.tasks;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.tebowcurescheduler.api.TebowCURESchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A scheduled task for creating admission requests
 * 
 * @author Bailly RURANGIRWA
 */
public class AdmissionRequestTask extends AbstractTask {
	
	// Logger
	private static final Logger log = LoggerFactory.getLogger(AdmissionRequestTask.class);
	
	@Override
	public void execute() {
		try {
			TebowCURESchedulerService tcs = Context.getService(TebowCURESchedulerService.class);
			ConceptService cs = Context.getConceptService();
			TaskDefinition admissionRequestTask = Context.getSchedulerService().getTaskByName("Admission Request Task");
			Long repeatInterval = admissionRequestTask.getRepeatInterval();
			Concept clinicalCoverFormConcept = cs.getConceptByUuid("78052c25-5984-4f5d-87f6-78e9e55963ba");
			Concept admitPatientDispositionConcept = cs.getConceptByUuid("81cded5a-3f10-11e4-adec-0800271c1b75");
			
			if (repeatInterval != null) {
				
				List<Obs> recentClinicalCoverObs = Context.getService(TebowCURESchedulerService.class).getRecentClinicalCoverObs(repeatInterval.intValue(), clinicalCoverFormConcept);
				
				if (recentClinicalCoverObs != null && recentClinicalCoverObs.size() > 0) {
					for (Obs obs : recentClinicalCoverObs) {
						List<Obs> existingAdmissionRequestsForPerson = tcs.getObservations(obs.getPerson(), admitPatientDispositionConcept, new Date());
						if (CollectionUtils.isEmpty(existingAdmissionRequestsForPerson)) {
							tcs.createAdmissionRequestEncounter(obs);
						}
					}
				}
			}
			
		}
		catch (Exception e) {
			log.error("Failed to create admission request", e);
		}
	}
	
}
