/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.dmi.caseReport;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.dmi.casereport.DiagnosisCaseDateDataDefinition;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.data.visit.evaluator.VisitDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * Evaluates Visit type Data Definition
 */

@Handler(supports = DiagnosisCaseDateDataDefinition.class, order = 50)
public class DiagnosisCaseDateDataEvaluator implements VisitDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedVisitData evaluate(VisitDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedVisitData c = new EvaluatedVisitData(definition, context);

        String qry = "select e.visit_id,\n" +
                "       GROUP_CONCAT(CASE\n" +
                "                        WHEN d.diagnosis_coded IS NOT NULL\n" +
                "                            THEN CONCAT(COALESCE(n.name, '-'),':', date(d.date_created)) END ORDER BY\n" +
                "                    d.diagnosis_coded SEPARATOR ', ') as diagnosis_date\n" +
                "FROM kenyaemr_etl.etl_clinical_encounter e\n" +
                "         inner join openmrs.encounter_diagnosis d on e.encounter_id = d.encounter_id and d.voided = 0 and d.dx_rank = 2\n" +
                "         inner join openmrs.concept_name n\n" +
                "                    on d.diagnosis_coded = n.concept_id and n.locale = 'en'\n" +
                "where n.concept_name_type = 'FULLY_SPECIFIED'\n" +
                "  and visit_date between date(:startDate) and date(:endDate)\n" +
                "GROUP BY e.visit_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Date startDate = (Date) context.getParameterValue("startDate");
        Date endDate = (Date) context.getParameterValue("endDate");
        queryBuilder.addParameter("endDate", endDate);
        queryBuilder.addParameter("startDate", startDate);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
