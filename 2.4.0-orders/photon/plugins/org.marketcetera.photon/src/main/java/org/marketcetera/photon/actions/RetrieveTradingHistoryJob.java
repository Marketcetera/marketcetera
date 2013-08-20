package org.marketcetera.photon.actions;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.marketcetera.client.Client;
import org.marketcetera.client.ClientInitException;
import org.marketcetera.client.ClientManager;
import org.marketcetera.core.instruments.UnderlyingSymbolSupport;
import org.marketcetera.core.position.ImmutablePositionSupport;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionEngineFactory;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.photon.*;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;

import com.google.common.collect.Maps;

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
		String timeString = PhotonPlugin.getDefault().getPreferenceStore().getString(PhotonPreferences.TRADING_HISTORY_START_TIME);
		if(StringUtils.isNotEmpty(timeString)) {
			final TimeOfDay time = TimeOfDay.create(timeString);
			if(time != null) {
			    TradeReportsHistory tradeReportsHistory = PhotonPlugin.getDefault().getTradeReportsHistory();
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
								SLF4JLoggerProxy.debug(this,
								                       "Aborting history retrieval since server connection is not available"); //$NON-NLS-1$
								return new ReportBase[0];
							}
							try {
							    // this collection will hold the reports that we're going to return - the goal is to collect all reports
							    //  since the lastOccurrence date plus any open orders that predate the lastOccurrence
							    Set<ReportBase> allReports = new LinkedHashSet<ReportBase>();
							    // this list of reports may be truncated by the lastOccurrence date
							    ReportBase[] reports = client.getReportsSince(lastOccurrence);
							    if(reports != null) {
							        allReports.addAll(Arrays.asList(reports));
							    }
							    // allReports now contains the reports since the lastOccurrence - check for open orders
							    List<ReportBase> openReports = client.getOpenOrders();
							    // see if any of the openReports don't appear in the current list. if so, add them
							    for(ReportBase openReport : openReports) {
							        if(!allReports.contains(openReport)) {
							            allReports.add(openReport);
							        }
							    }
								return allReports.toArray(new ReportBase[0]);
							} catch (Exception e) {
                                Messages.RETRIEVE_TRADING_HISTORY_JOB_ERROR.error(this, e);
                                return new ReportBase[0];
							}
						}
					});
                    Map<PositionKey<?>, BigDecimal> positions = Maps
                            .<PositionKey<?>, BigDecimal> newHashMap(ClientManager
                                    .getInstance().getAllEquityPositionsAsOf(
                                            lastOccurrence));
                    positions.putAll(ClientManager.getInstance()
                            .getAllOptionPositionsAsOf(lastOccurrence));
                    positions.putAll(ClientManager.getInstance().getAllFuturePositionsAsOf(lastOccurrence));
                    positions.putAll(ClientManager.getInstance().getAllCurrencyPositionsAsOf(lastOccurrence));
                    EventList<ReportHolder> messages = tradeReportsHistory.getAllMessagesList();
                    ImmutablePositionSupport positionSupport = new ImmutablePositionSupport(positions);
                    PhotonPositionMarketData positionMarketData = new PhotonPositionMarketData(PhotonPlugin.getDefault().getMarketDataManager().getMarketData());
                    UnderlyingSymbolSupport underlyingSymbolSupport = PhotonPlugin.getDefault().getUnderlyingSymbolSupport();
                    PositionEngine engine = PositionEngineFactory.createFromReportHolders(messages,
                                                                                          positionSupport,
                                                                                          positionMarketData,
                                                                                          underlyingSymbolSupport);
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
