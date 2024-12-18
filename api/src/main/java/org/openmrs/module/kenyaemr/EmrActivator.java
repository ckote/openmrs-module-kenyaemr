/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.openmrs.GlobalProperty;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.util.ServerInformation;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class EmrActivator implements ModuleActivator {

	protected static final Log log = LogFactory.getLog(EmrActivator.class);

	static {
		// Possibly bad practice but we really want to see these startup log messages
		LogManager.getLogger("org.openmrs.module.kenyacore").setLevel(Level.INFO);
		LogManager.getLogger("org.openmrs.module.kenyaemr").setLevel(Level.INFO);
	}

	/**
	 * @see ModuleActivator#willRefreshContext()
	 */
	public void willRefreshContext() {

		log.info("KenyaEMR context refreshing...");
		//TODO: Investigate what causes reports page to load slowly. This behavior started with platform 2.x upgrade
		Context.getAdministrationService().executeSQL("delete from reporting_report_request", false);
	}

	/**
	 * @see ModuleActivator#willStart()
	 */
	public void willStart() {
		log.info("KenyaEMR starting...");
	}

	/**
	 * @see ModuleActivator#contextRefreshed()
	 */
	public void contextRefreshed() {
		Configuration.configure();

		try {
			CoreContext.getInstance().refresh();
		}
		catch (Exception ex) {
			// If an error occurs during core refresh, we need KenyaEMR to still start so that the error can be
			// communicated to an admin user. So we catch exceptions, log them and alert super users.
			log.error("Unable to refresh core context", ex);

			// TODO re-enable once someone fixes TRUNK-4267
			//Context.getAlertService().notifySuperUsers("Unable to start KenyaEMR", ex);
		}
	}

	/**
	 * @see ModuleActivator#started()
	 */
	public void started() {
		log.info("KenyaEMR started");
		AdministrationService administrationService = Context.getAdministrationService();
		administrationService.executeSQL("UPDATE form SET published = 1 where retired = 0", false);

		Map<String, Object> kenyaemrInfo = ServerInformation.getKenyaemrInformation();
			String moduleVersion = (String) kenyaemrInfo.get("version");
		GlobalProperty gp = administrationService.getGlobalPropertyObject(CommonMetadata.GP_KENYAEMR_VERSION);
		if (gp == null) {
			gp = new GlobalProperty();
			gp.setProperty(CommonMetadata.GP_KENYAEMR_VERSION);
		}
		gp.setPropertyValue(moduleVersion);
		administrationService.saveGlobalProperty(gp);
	}

	/**
	 * @see ModuleActivator#willStop()
	 */
	public void willStop() {
		log.info("KenyaEMR stopping...");
	}

	/**
	 * @see ModuleActivator#stopped()
	 */
	public void stopped() {
		log.info("KenyaEMR stopped");
	}
}