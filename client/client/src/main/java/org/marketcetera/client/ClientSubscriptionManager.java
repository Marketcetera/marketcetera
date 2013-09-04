package org.marketcetera.client;

import java.beans.ExceptionListener;
import java.util.Deque;
import java.util.LinkedList;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderCancelReject;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
public class ClientSubscriptionManager
{
    public void addReportListener(ReportListener inListener)
    {
        synchronized(mReportListeners) {
            mReportListeners.addFirst(inListener);
        }
    }
    public void removeReportListener(ReportListener inListener)
    {
        synchronized(mReportListeners) {
            mReportListeners.removeFirstOccurrence(inListener);
        }
    }
    public void addBrokerStatusListener(BrokerStatusListener listener)
    {
        synchronized(mBrokerStatusListeners) {
            mBrokerStatusListeners.addFirst(listener);
        }
    }
    public void removeBrokerStatusListener(BrokerStatusListener listener)
    {
        synchronized(mBrokerStatusListeners) {
            mBrokerStatusListeners.removeFirstOccurrence(listener);
        }
    }
    public void addServerStatusListener(ServerStatusListener listener)
    {
        synchronized(mServerStatusListeners) {
            mServerStatusListeners.addFirst(listener);
        }
    }
    public void removeServerStatusListener(ServerStatusListener listener)
    {
        synchronized(mServerStatusListeners) {
            mServerStatusListeners.removeFirstOccurrence(listener);
        }
    }
    public void addExceptionListener(ExceptionListener inListener)
    {
        synchronized (mExceptionListeners) {
            mExceptionListeners.addFirst(inListener);
        }
    }
    public void removeExceptionListener(ExceptionListener inListener)
    {
        synchronized (mExceptionListeners) {
            mExceptionListeners.removeFirstOccurrence(inListener);
        }
    }
    public void notify(ExecutionReport inReport)
    {
        synchronized (mReportListeners) {
            for(ReportListener listener: mReportListeners) {
                try {
                    listener.receiveExecutionReport(inReport);
                } catch(Exception e) {
                    Messages.LOG_ERROR_RECEIVE_EXEC_REPORT.warn(this,
                                                                e,
                                                                ObjectUtils.toString(inReport));
                }
            }
        }
    }
    public void notify(OrderCancelReject inReject)
    {
        synchronized (mReportListeners) {
            for(ReportListener listener: mReportListeners) {
                try {
                    listener.receiveCancelReject(inReject);
                } catch(Exception e) {
                    Messages.LOG_ERROR_RECEIVE_CANCEL_REJECT.warn(this,
                                                                  e,
                                                                  ObjectUtils.toString(inReject));
                }
            }
        }
    }
    public void notify(BrokerStatus inBrokerStatus)
    {
        synchronized (mBrokerStatusListeners) {
            for (BrokerStatusListener listener:
                     mBrokerStatusListeners) {
                try {
                    listener.receiveBrokerStatus(inBrokerStatus);
                } catch(Exception e) {
                    Messages.LOG_ERROR_RECEIVE_BROKER_STATUS.warn(this,
                                                                  e,
                                                                  ObjectUtils.toString(inBrokerStatus));
                }
            }
        }
    }
    public void notify(ServerStatus inStatus)
    {
        synchronized (mServerStatusListeners) {
            for(ServerStatusListener listener : mServerStatusListeners) {
                try {
                    listener.receiveServerStatus(inStatus.isOnline());
                } catch(Exception e) {
                    Messages.LOG_ERROR_RECEIVE_SERVER_STATUS.warn(this,
                                                                  e,
                                                                  inStatus);
                }
            }
        }
    }
    public void notify(Exception inException)
    {
        synchronized (mExceptionListeners) {
            for(ExceptionListener listener : mExceptionListeners) {
                try {
                    listener.exceptionThrown(inException);
                } catch(Exception e) {
                    Messages.LOG_ERROR_NOTIFY_EXCEPTION.warn(this,
                                                             e,
                                                             ObjectUtils.toString(inException));
                }
            }
        }
    }
    public enum ServerStatus
    {
        ONLINE,
        OFFLINE;
        public static ServerStatus getFor(boolean inIsOnline)
        {
            return inIsOnline ? ONLINE : OFFLINE;
        }
        public boolean isOnline()
        {
            return equals(ONLINE);
        }
    }
    private final Deque<ReportListener> mReportListeners = new LinkedList<ReportListener>();
    private final Deque<BrokerStatusListener> mBrokerStatusListeners = new LinkedList<BrokerStatusListener>();
    private final Deque<ServerStatusListener> mServerStatusListeners = new LinkedList<ServerStatusListener>();
    private final Deque<ExceptionListener> mExceptionListeners = new LinkedList<ExceptionListener>();
}
