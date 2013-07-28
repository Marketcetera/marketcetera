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
import org.marketcetera.client.ConnectionException;
import org.marketcetera.core.instruments.UnderlyingSymbolSupport;
import org.marketcetera.core.position.ImmutablePositionSupport;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionEngineFactory;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.photon.*;
import org.marketcetera.trade.OrderID;
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
								SLF4JLoggerProxy.debug(this,
								                       "Aborting history retrieval since server connection is not available"); //$NON-NLS-1$
								return new ReportBase[0];
							}
							try {
							    ReportBase[] oldReports = client.getReportsSince(lastOccurrence);
							    List<ReportBase> newReports = client.getOpenOrders();
							    Set<OrderID> newReportIds = new HashSet<OrderID>();
							    Set<OrderID> oldReportIds = new HashSet<OrderID>();
							    if(oldReports != null) {
	                                for(ReportBase report : oldReports) {
	                                    oldReportIds.add(report.getOrderID());
	                                }
							    }
							    if(newReports != null){
                                    for(ReportBase report : newReports) {
                                        newReportIds.add(report.getOrderID());
                                    }
							    }
							    System.out.println(lastOccurrence + " in RetrieveTradingHistoryJob.run, got " + oldReportIds + " and " + newReportIds);
//								return client.getReportsSince(lastOccurrence);
								return newReports == null ? new ReportBase[0] : newReports.toArray(new ReportBase[0]);
							} catch (ConnectionException e) {
                                e.printStackTrace();
								Messages.RETRIEVE_TRADING_HISTORY_JOB_ERROR.error(this, e);
								return new ReportBase[0];
							} catch (Exception e) {
							    e.printStackTrace();
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
