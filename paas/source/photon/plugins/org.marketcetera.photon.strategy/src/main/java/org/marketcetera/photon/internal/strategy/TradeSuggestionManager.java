package org.marketcetera.photon.internal.strategy;

import java.util.Date;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.photon.module.ISinkDataHandler;
import org.marketcetera.photon.module.ISinkDataManager;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.trade.OrderSingleSuggestion;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Manages trade suggestions.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class TradeSuggestionManager implements ISinkDataHandler {

	/**
	 * Sink data manager
	 */
	private final ISinkDataManager mSinkDataManager;
	
	/**
	 * Returns the singleton instance for the currently running plug-in.
	 * 
	 * @return the singleton instance
	 */
	public static TradeSuggestionManager getCurrent() {
		return Activator.getDefault().getTradeSuggestionManager();
	}

	private final WritableList mSuggestions = WritableList
			.withElementType(TradeSuggestion.class);

	/**
	 * This object should only be constructed by {@link Activator}.
	 */
	TradeSuggestionManager() {
		mSinkDataManager = ModuleSupport.getSinkDataManager();
		mSinkDataManager.register(this, OrderSingleSuggestion.class);
	}

	/**
	 * Returns the collection of trade suggestions.
	 * 
	 * @return the trade suggestions
	 */
	public IObservableList getTradeSuggestions() {
		return Observables.unmodifiableObservableList(mSuggestions);
	}

	/**
	 * Adds a trade suggestion to the managed collection.
	 * 
	 * @param suggestion
	 *            new suggestion to add.
	 */
	void addSuggestion(final OrderSingleSuggestion suggestion) {
		final Date timestamp = new Date();
		// Ensure the update is performed in the main UI thread
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				mSuggestions.add(new TradeSuggestion(suggestion, timestamp));
			}
		});
	}

	/**
	 * Removes a trade suggestion from the managed collection.
	 * 
	 * @param suggestion
	 *            suggestion to remove
	 */
	void removeSuggestion(TradeSuggestion suggestion) {
		mSuggestions.remove(suggestion);
	}

	@Override
	public void receivedData(DataFlowID inFlowID, Object inData) {
		OrderSingleSuggestion suggestion = (OrderSingleSuggestion) inData;
		if (suggestion.getOrder() != null) {
			addSuggestion(suggestion);
		} else {
			Messages.TRADE_SUGGESTION_MANAGER_INVALID_DATA_NO_ORDER.error(this);
		}
	}

}
