/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.tebowcurescheduler.api;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.api.OpenmrsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The main service of this module, which is exposed for other modules. See
 * moduleApplicationContext.xml on how it is wired up.
 */
@Service("tebowCURESchedulerService")
public interface TebowCURESchedulerService extends OpenmrsService {
	
	@Transactional
	Encounter createAdmissionRequestEncounter(Obs trigger);
	
	@Transactional
	List<Obs> getRecentClinicalCoverObs(int repeatInterval, Concept concept);
	
	@Transactional
	List<Visit> getToAdmitVisitsList();
	
	@Transactional
	List<Visit> getAdmittedVisitsList();
	
}
