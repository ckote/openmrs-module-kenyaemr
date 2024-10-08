/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAddressCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.EncounterProviderConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RDQACalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.CCCDefaulterTracingRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.MissedAppointmentReasonsConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.HIVTestOneDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLLastVisitDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.*;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterProviderDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Builds({"kenyaemr.hiv.report.cccDefaulterTracingRegister"})
public class CCCDefaulterTracingRegisterReportBuilder extends AbstractReportBuilder {
    public static final String ENC_DATE_FORMAT = "yyyy/MM/dd";
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    @Override
    protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
        return Arrays.asList(
                new Parameter("startDate", "Start Date", Date.class),
                new Parameter("endDate", "End Date", Date.class),
                new Parameter("dateBasedReporting", "", String.class)
        );
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor reportDescriptor, ReportDefinition reportDefinition) {
        return Arrays.asList(
                ReportUtils.map(datasetColumns(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    protected DataSetDefinition datasetColumns() {
        EncounterDataSetDefinition dsd = new EncounterDataSetDefinition();
        dsd.setName("CCCDefaulterTracing");
        dsd.setDescription("Defaulter Tracing Dataset");
        dsd.addSortCriteria("Date of Tracing", SortCriteria.SortDirection.ASC);
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String paramMapping = "startDate=${startDate},endDate=${endDate}";

        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName} {middleName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);

        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
		PatientIdentifierType natID = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_ID);
		PatientIdentifierType nupi = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_UNIQUE_PATIENT_IDENTIFIER);
		DataDefinition nupiDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(nupi.getName(), nupi), identifierFormatter);
		DataDefinition natIdDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(natID.getName(), natID), identifierFormatter);
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

        EncounterProviderDataDefinition providerDataDefinition = new EncounterProviderDataDefinition();
        providerDataDefinition.setSingleProvider(true);
        PersonAttributeType phoneNumber = MetadataUtils.existing(PersonAttributeType.class, CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT);

        String dateParams = "startDate=${startDate},endDate=${endDate}";

        DateMissedAppointmentBeforeTracingDataDefinition dateMissedAppointment = new DateMissedAppointmentBeforeTracingDataDefinition();
        dateMissedAppointment.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dateMissedAppointment.addParameter(new Parameter("endDate", "End Date", Date.class));

        HonouredAppointmentDateDataDefinition dateHonouredAppointment = new HonouredAppointmentDateDataDefinition();
        dateHonouredAppointment.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dateHonouredAppointment.addParameter(new Parameter("endDate", "End Date", Date.class));

        LastVisitDateBeforeMissedAppointmentDataDefinition lastVisitBeforeMissedApp = new LastVisitDateBeforeMissedAppointmentDataDefinition();
        lastVisitBeforeMissedApp.addParameter(new Parameter("startDate", "Start Date", Date.class));
        lastVisitBeforeMissedApp.addParameter(new Parameter("endDate", "End Date", Date.class));

		FinalOutcomeDataDefinition finalOutcomeDataDefinition = new FinalOutcomeDataDefinition();
		finalOutcomeDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		finalOutcomeDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));

		DateOfFinalOutcomeDataDefinition dateOfFinalOutcomeDataDefinition = new DateOfFinalOutcomeDataDefinition();
		dateOfFinalOutcomeDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dateOfFinalOutcomeDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));

		dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("id", new PatientIdDataDefinition(), "");
		dsd.addColumn("National ID", natIdDef, "");
		dsd.addColumn("NUPI", nupiDef, "");
        dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
        dsd.addColumn("Age", new AgeDataDefinition(), "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("Telephone No", new PersonAttributeDataDefinition(phoneNumber), "");
        dsd.addColumn("Unique Patient Number", identifierDef, null);
        dsd.addColumn("Village_Estate_Landmark", new CalculationDataDefinition("Village/Estate/Landmark", new PersonAddressCalculation()), "", new RDQACalculationResultConverter());
        dsd.addColumn("Date of Tracing", new EncounterDatetimeDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT));
        dsd.addColumn("Tracing Type", new TracingTypeDataDefinition(),"");
        dsd.addColumn("Outcome", new TracingOutcomeDataDefinition(),"");
        dsd.addColumn("Tracing attempt No", new TracingNumberDataDefinition(),"");
        dsd.addColumn("Final Outcome", finalOutcomeDataDefinition,dateParams);
        dsd.addColumn("Date of Outcome",  dateOfFinalOutcomeDataDefinition,dateParams, new DateConverter(DATE_FORMAT));
        dsd.addColumn("Last Visit Date", lastVisitBeforeMissedApp,dateParams, new DateConverter(DATE_FORMAT));
        dsd.addColumn("Date of missed appointment", dateMissedAppointment, dateParams, new DateConverter(DATE_FORMAT));
        dsd.addColumn("Reason for missed appointment", new ReasonForMissedAppointmentDataDefinition(),"", new MissedAppointmentReasonsConverter());
        dsd.addColumn("Date promised to come", new ReturnToCareDateDataDefinition(),"", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Honoured appointment date", dateHonouredAppointment,dateParams, new DateConverter(DATE_FORMAT));
        dsd.addColumn("Comments", new ProviderCommentsDataDefinition(),"");
        dsd.addColumn("Provider", providerDataDefinition,"", new EncounterProviderConverter());


        CCCDefaulterTracingRegisterCohortDefinition cd = new CCCDefaulterTracingRegisterCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.addRowFilter(cd, paramMapping);
        return dsd;

    }
}
