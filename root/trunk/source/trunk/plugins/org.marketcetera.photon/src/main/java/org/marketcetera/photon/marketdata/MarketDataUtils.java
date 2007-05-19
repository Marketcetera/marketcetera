package org.marketcetera.photon.marketdata;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.marketcetera.core.MSymbol;
import org.marketcetera.marketdata.IMarketDataFeed;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Group;
import quickfix.Message;
import quickfix.field.MarketDepth;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntryTypes;
import quickfix.field.NoRelatedSym;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;

public class MarketDataUtils {

	static FIXMessageFactory messageFactory = FIXVersion.FIX44
			.getMessageFactory();

	public static Message newSubscribeLevel2(MSymbol symbol) {
		Message message = newSubscribeHelper(symbol);
		message.setField(new MarketDepth(0)); // full book

		return message;
	}

	public static Message newSubscribeBBO(MSymbol symbol) {
		Message message = newSubscribeHelper(symbol);
		message.setField(new MarketDepth(1)); // top-of-book
		return message;
	}

	private static Message newSubscribeHelper(MSymbol symbol) {
		Message message = messageFactory
				.createMessage(MsgType.MARKET_DATA_REQUEST);
		message.setField(new SubscriptionRequestType(
				SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES));
		message.setField(new NoMDEntryTypes(0));
		Group relatedSymGroup = messageFactory.createGroup(
				MsgType.MARKET_DATA_REQUEST, NoRelatedSym.FIELD);
		relatedSymGroup.setField(new Symbol(symbol.toString()));
		message.addGroup(relatedSymGroup);
		return message;
	}

	/**
	 * Perform a query using the default timeout with a callback on the UI thread.
	 * 
	 * @see org.marketcetera.photon.marketdata.MarketDataUtils#asyncMarketDataQuery(org.marketcetera.core.MSymbol,
            quickfix.Message, org.marketcetera.marketdata.IMarketDataFeed, IMarketDataListCallback, boolean, long)
	 */
	public static void asyncMarketDataQuery(final MSymbol symbolToQuery,
			final Message query, final IMarketDataFeed marketDataFeed,
			final IMarketDataListCallback callback) {
		final long SECURITY_LIST_TIMEOUT_MILLIS = 8000;
		asyncMarketDataQuery(symbolToQuery, query, marketDataFeed, callback, true, SECURITY_LIST_TIMEOUT_MILLIS);
	}

	/**
	 * Create a job that performs a query and invokes the callback when a result
	 * is available.
	 * 
	 * @param query
	 *            any query that can be executed with IMarketDataFeed.syncQuery,
	 *            such as those created via
	 *            OptionMarketDataUtils.newRelatedOptionsQuery
	 * @param callback
	 *            the callback will be invoked when the market data is available
	 *            or on failure. The List<Message> passed to the callback is
	 *            the market data list response.
	 * @param callbackOnUIThread
	 *            when true, use Display.asyncExec to perform the callback on
	 *            the UI thread. Otherwise the callback occurs in the Job
	 *            thread.
	 */
	public static void asyncMarketDataQuery(final MSymbol symbolToQuery,
			final Message query, final IMarketDataFeed marketDataFeed,
			final IMarketDataListCallback callback,
			final boolean callbackOnUIThread, final long timeoutMillis) {

		Job securityListJob = new Job("Getting market data for "
				+ symbolToQuery.getFullSymbol()) {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					final List<Message> messages = marketDataFeed.syncQuery(
							query, timeoutMillis, TimeUnit.MILLISECONDS);

					if (callbackOnUIThread) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								callback.onMarketDataListAvailable(messages);
							}
						});
					} else {
						callback.onMarketDataListAvailable(messages);
					}
				} catch (Exception anyException) {
					if (callbackOnUIThread) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								callback.onMarketDataFailure(symbolToQuery);
							}
						});
					} else {
						callback.onMarketDataFailure(symbolToQuery);
					}
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}
		};
		securityListJob.schedule();
	}
}
