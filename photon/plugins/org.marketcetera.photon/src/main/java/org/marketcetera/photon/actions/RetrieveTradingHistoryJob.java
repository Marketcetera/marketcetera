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
import org.marketcetera.client.ClientManager;
import org.marketcetera.core.instruments.UnderlyingSymbolSupport;
import org.marketcetera.core.position.ImmutablePositionSupport;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionEngineFactory;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.marketdata.core.manager.NoMarketDataProvidersAvailable;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.photon.*;
import org.marketcetera.photon.marketdata.IMarketDataManager;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.ReportBaseImpl;
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
public class RetrieveTradingHistoryJob
        extends Job
{
	/**
	 * Constructor.
	 */
	public RetrieveTradingHistoryJob() {
		super(Messages.RETRIEVE_TRADING_HISTORY_JOB_NAME.getText());
	}
    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        try {
            String timeString = StringUtils.trimToNull(PhotonPlugin.getDefault().getPreferenceStore().getString(PhotonPreferences.TRADING_HISTORY_START_TIME));
            // this collection will hold the reports that we're going to return - the goal is to collect all reports
            //  since the lastOccurrence date plus any open orders that predate the lastOccurrence
            final Set<ReportBase> allReports = new LinkedHashSet<ReportBase>();
            TradeReportsHistory tradeReportsHistory = PhotonPlugin.getDefault().getTradeReportsHistory();
            Client client = ClientManager.getInstance();
            List<ReportBaseImpl> openReports = client.getOpenOrders();
            Date positionDate = new Date();
            if(timeString != null) {
                TimeOfDay time = TimeOfDay.create(timeString);
                if(time != null) {
                    // trade history is enabled, fetch reports since last occurrence
                    Date lastOccurrence = time.getLastOccurrence();
                    ReportBase[] reports = client.getReportsSince(lastOccurrence);
                    if(reports != null) {
                        allReports.addAll(Arrays.asList(reports));
                    }
                    // see if any of the openReports don't appear in the current list. if so, add them
                    if(openReports != null) {
                        for(ReportBase openReport : openReports) {
                            if(!allReports.contains(openReport)) {
                                allReports.add(openReport);
                            }
                        }
                    }
                    PhotonPlugin.getDefault().setSessionStartTime(lastOccurrence);
                    positionDate = lastOccurrence;
                }
            } else {
                if(openReports != null) {
                    // Photon trade history is disabled, but we still need open orders
                    allReports.addAll(openReports);
                }
            }
            tradeReportsHistory.resetMessages(new Callable<ReportBase[]>() {
                @Override
                public ReportBase[] call()
                        throws Exception
                {
                    return allReports.toArray(new ReportBase[0]);
                }
            });
            if(positionDate != null) {
                Map<PositionKey<?>,BigDecimal> positions = Maps.<PositionKey<?>,BigDecimal> newHashMap(ClientManager.getInstance().getAllEquityPositionsAsOf(positionDate));
                positions.putAll(ClientManager.getInstance().getAllOptionPositionsAsOf(positionDate));
                positions.putAll(ClientManager.getInstance().getAllFuturePositionsAsOf(positionDate));
                positions.putAll(ClientManager.getInstance().getAllCurrencyPositionsAsOf(positionDate));
                EventList<ReportHolder> messages = tradeReportsHistory.getAllMessagesList();
                ImmutablePositionSupport positionSupport = new ImmutablePositionSupport(positions);
                IMarketDataManager marketDataManager = PhotonPlugin.getDefault().getMarketDataManager();
                if(!marketDataManager.isRunning() && marketDataManager.isReconnecting()) {
                    long startTime = System.currentTimeMillis();
                    while(!marketDataManager.isRunning()) {
                        Thread.sleep(250);
                        if(System.currentTimeMillis()-60000>startTime) {
                            // forget it, I (kinda) quietly give up
                            break;
                        }
                    }
                }
                if(!marketDataManager.isRunning()) {
                    SLF4JLoggerProxy.warn(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                          "Position market data unavailable from the Market Data Nexus");
                    SLF4JLoggerProxy.warn(this,
                                          "Position market data unavailable from the Market Data Nexus");
                }
                PhotonPositionMarketData positionMarketData = new PhotonPositionMarketData(marketDataManager.getMarketData());
                UnderlyingSymbolSupport underlyingSymbolSupport = PhotonPlugin.getDefault().getUnderlyingSymbolSupport();
                PositionEngine engine = PositionEngineFactory.createFromReportHolders(messages,
                                                                                      positionSupport,
                                                                                      positionMarketData,
                                                                                      underlyingSymbolSupport);
                PhotonPlugin.getDefault().registerPositionEngine(engine);
            }
        } catch (Exception e) {
            Messages.RETRIEVE_TRADING_HISTORY_JOB_ERROR.error(org.marketcetera.core.Messages.USER_MSG_CATEGORY);
            Messages.RETRIEVE_TRADING_HISTORY_JOB_ERROR.error(this,
                                                              e);
            if(e instanceof NoMarketDataProvidersAvailable) {
                return Status.CANCEL_STATUS;
            }
        }
        return Status.OK_STATUS;
    }
}
