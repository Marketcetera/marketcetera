package org.marketcetera.photon.actions;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.marketcetera.client.Client;
import org.marketcetera.client.ClientInitException;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.PhotonPreferences;
import org.marketcetera.photon.TimeOfDay;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Job that retrieves trading history from ORS.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class RetrieveTradingHistoryJob extends Job {

	/**
	 * Constructor.
	 */
	public RetrieveTradingHistoryJob() {
		super(Messages.RETRIEVE_TRADING_HISTORY_JOB_NAME.getText());
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		Client client;
		try {
			client = ClientManager.getInstance();
		} catch (ClientInitException e) {
			// no longer connected
			SLF4JLoggerProxy.debug(this,
					"Aborting history retrieval since server connection is not available"); //$NON-NLS-1$
			return Status.CANCEL_STATUS;
		}
		String timeString = PhotonPlugin.getDefault().getPreferenceStore().getString(
				PhotonPreferences.TRADING_HISTORY_START_TIME);
		if (StringUtils.isNotEmpty(timeString)) {
			TimeOfDay time = TimeOfDay.create(timeString);
			if (time != null) {
				try {
					ReportBase[] reports = client.getReportsSince(time.getLastOccurrence());
					PhotonPlugin.getDefault().getTradeReportsHistory().resetMessages(reports);
				} catch (ConnectionException e) {
					Messages.RETRIEVE_TRADING_HISTORY_JOB_ERROR.error(this, e);
					return Status.CANCEL_STATUS;
				}
			}
		}
		return Status.OK_STATUS;
	}

}
