/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.moh717;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

@Component
public class Moh717IndicatorLibrary {

    private final Moh717CohortLibrary moh717CohortLibrary;

    @Autowired
    public Moh717IndicatorLibrary(Moh717CohortLibrary moh717CohortLibrary) {
        this.moh717CohortLibrary = moh717CohortLibrary;
    }

    public CohortIndicator getPatientsWithNewClinicalEncounterWithinReportingPeriod() {
        return cohortIndicator("All Patients with new clinical encounters with period", ReportUtils.map(moh717CohortLibrary.getPatientsWithNewClinicalEncounterWithinReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator getPatientsWithReturnClinicalEncounterWithinReportingPeriod() {
        return cohortIndicator("All Patients with return encounters with period", ReportUtils.map(moh717CohortLibrary.getPatientsWithReturnClinicalEncounterWithinReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator newANCVisits() {
        return cohortIndicator("All Patients with return encounters with period", ReportUtils.map(moh717CohortLibrary.newANCVisits(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator ancRevisits() {
        return cohortIndicator("All Patients with return encounters with period", ReportUtils.map(moh717CohortLibrary.ancRevisits(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator newCWCVisits() {
        return cohortIndicator("All Patients with return encounters with period", ReportUtils.map(moh717CohortLibrary.newCWCVisits(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator cwcRevisits() {
        return cohortIndicator("All Patients with return encounters with period", ReportUtils.map(moh717CohortLibrary.cwcRevisits(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator newPNCVisits() {
        return cohortIndicator("All Patients with return encounters with period", ReportUtils.map(moh717CohortLibrary.newPNCVisits(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator pncReVisits() {
        return cohortIndicator("All Patients with return encounters with period", ReportUtils.map(moh717CohortLibrary.pncReVisits(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator normalDeliveries() {
        return cohortIndicator("Normal Deliveries", ReportUtils.map(moh717CohortLibrary.normalDeliveries(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator caesareanSections() {
        return cohortIndicator("Caesarean Sections", ReportUtils.map(moh717CohortLibrary.caesareanSections(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator breechDeliveries() {
        return cohortIndicator("Breech Deliveries", ReportUtils.map(moh717CohortLibrary.breechDeliveries(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator assistedVaginalDelivery() {
        return cohortIndicator("Assisted Vaginal Delivery", ReportUtils.map(moh717CohortLibrary.assistedVaginalDelivery(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator bornBeforeArrival() {
        return cohortIndicator("Born Before Arrival", ReportUtils.map(moh717CohortLibrary.bornBeforeArrival(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator maternalDeaths() {
        return cohortIndicator("Maternal Deaths", ReportUtils.map(moh717CohortLibrary.maternalDeaths(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator maternalDeathsAudited() {
        return cohortIndicator("Maternal Deaths Audited", ReportUtils.map(moh717CohortLibrary.maternalDeathsAudited(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator liveBirths() {
        return cohortIndicator("Live Births", ReportUtils.map(moh717CohortLibrary.liveBirths(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator stillBirths() {
        return cohortIndicator("Still Births", ReportUtils.map(moh717CohortLibrary.stillBirths(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator lowBirthWeightBabies() {
        return cohortIndicator("Low Birth weight Babies", ReportUtils.map(moh717CohortLibrary.lowBirthWeightBabies(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator totalDischarges() {
        return cohortIndicator("Total Discharges", ReportUtils.map(moh717CohortLibrary.totalDischarges(), "startDate=${startDate},endDate=${endDate}"));
    }
}
