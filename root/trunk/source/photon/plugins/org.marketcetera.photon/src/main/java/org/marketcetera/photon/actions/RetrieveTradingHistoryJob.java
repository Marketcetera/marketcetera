package org.marketcetera.photon.actions;

import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.marketcetera.client.Client;
import org.marketcetera.client.ClientInitException;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.core.position.ImmutablePositionSupport;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionEngineFactory;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.PhotonPositionMarketData;
import org.marketcetera.photon.PhotonPreferences;
import org.marketcetera.photon.TimeOfDay;
import org.marketcetera.photon.marketdata.MarketDataManager;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Job that retrieves trading history from ORS.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
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
		String timeString = PhotonPlugin.getDefault().getPreferenceStore().getString(
				PhotonPreferences.TRADING_HISTORY_START_TIME);
		if (StringUtils.isNotEmpty(timeString)) {
			final TimeOfDay time = TimeOfDay.create(timeString);
			if (time != null) {
				TradeReportsHistory tradeReportsHistory = PhotonPlugin.getDefault()
						.getTradeReportsHistory();
				final Date lastOccurrence = time.getLastOccurrence();
				try {
					tradeReportsHistory.resetMessages(new Callable<ReportBase[]>() {

						@Override
						public ReportBase[] call() {
							Client client;
							try {
								client = ClientManager.getInstance();
							} catch (ClientInitException e) {
								// no longer connected
								SLF4JLoggerProxy
										.debug(this,
												"Aborting history retrieval since server connection is not available"); //$NON-NLS-1$
								return new ReportBase[0];
							}
							try {
								return client.getReportsSince(lastOccurrence);
							} catch (ConnectionException e) {
								Messages.RETRIEVE_TRADING_HISTORY_JOB_ERROR.error(this, e);
								return new ReportBase[0];
							}
						}
					});
					PositionEngine engine = PositionEngineFactory.createFromReportHolders(
							tradeReportsHistory.getAllMessagesList(), new ImmutablePositionSupport(
									ClientManager.getInstance().getPositionsAsOf(lastOccurrence)),
							new PhotonPositionMarketData(MarketDataManager.getCurrent()
									.getMarketData()));
					PhotonPlugin.getDefault().registerPositionEngine(engine);
				} catch (Exception e) {
					if (e instanceof RuntimeException) {
						throw (RuntimeException) e;
					} else {
						// The callable above doesn't throw checked exceptions
						assert false;
						throw new RuntimeException(e);
					}
				}
				PhotonPlugin.getDefault().setSessionStartTime(lastOccurrence);
			}
		}
		return Status.OK_STATUS;
	}

}
