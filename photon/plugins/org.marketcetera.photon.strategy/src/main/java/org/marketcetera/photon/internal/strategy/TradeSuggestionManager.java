package org.marketcetera.photon.internal.strategy;

import java.util.Date;
import java.util.Set;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.photon.module.IDataFlowLabelProvider;
import org.marketcetera.photon.module.ISinkDataHandler;
import org.marketcetera.photon.module.ISinkDataManager;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.trade.DeleteSuggestionAction;
import org.marketcetera.trade.RefreshSuggestionAction;
import org.marketcetera.trade.HasSuggestionAction;
import org.marketcetera.trade.OrderSingleSuggestion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Sets;

/* $License$ */

/**
 * Manages trade suggestions.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class TradeSuggestionManager
        implements ISinkDataHandler,ISubscriber
{
    /**
     * Returns the singleton instance for the currently running plug-in.
     * 
     * @return the singleton instance
     */
    public static TradeSuggestionManager getCurrent()
    {
        return Activator.getCurrent().getTradeSuggestionManager();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.ISubscriber#isInteresting(java.lang.Object)
     */
    @Override
    public boolean isInteresting(Object inData)
    {
        return inData instanceof StrategyEngine;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.ISubscriber#publishTo(java.lang.Object)
     */
    @Override
    public void publishTo(Object inData)
    {
        StrategyEngine strategyEngine = (StrategyEngine)inData;
        switch(strategyEngine.getConnectionState()) {
            case CONNECTED:
                synchronized(activeStrategyEngines) {
                    activeStrategyEngines.add(strategyEngine);
                }
                sendAction(strategyEngine,
                           RefreshSuggestionAction.instance);
                break;
            case DISCONNECTED:
                synchronized(activeStrategyEngines) {
                    activeStrategyEngines.remove(strategyEngine);
                }
                mSuggestions.clear();
                requestRefreshFromAll();
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }
    /**
     * This object should only be constructed by {@link Activator}.
     * 
     * @param inStrategyEngineMonitor a <code>StrategyEngineMonitor</code> value
     */
    TradeSuggestionManager(StrategyEngineMonitor inStrategyEngineMonitor)
    {
        mSinkDataManager = ModuleSupport.getSinkDataManager();
        strategyEngineMonitor = inStrategyEngineMonitor;
        strategyEngineMonitor.subscribe(this);
        mSinkDataManager.register(this,
                                  OrderSingleSuggestion.class);
        mSinkDataManager.register(this,
                                  HasSuggestionAction.class);
        IObservableList currentStrategyEngines = strategyEngineMonitor.getStrategyEngineList();
        if(currentStrategyEngines != null) {
            for(Object o : currentStrategyEngines) {
                publishTo(o);
            }
        }
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
     * @param source
     *            the source of the suggestion
     */
	public void addSuggestion(final OrderSingleSuggestion suggestion, final String source) {
		final Date timestamp = new Date();
		// Ensure the update is performed in the main UI thread
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				mSuggestions.add(new TradeSuggestion(suggestion, source, timestamp));
			}
		});
	}
	/**
	 * 
	 *
	 *
	 * @param inSuggestion
	 */
    public void suggestionOpened(TradeSuggestion inSuggestion)
    {
        // TODO remove suggestion from list
        // TODO send suggestion delete
        removeSuggestion(inSuggestion);
    }
    /**
     * 
     *
     *
     * @param inSuggestion
     */
    public void suggestionSent(TradeSuggestion inSuggestion)
    {
        // TODO remove suggestion from list
        // TODO send suggestion delete
        removeSuggestion(inSuggestion);
    }
    /**
     * 
     *
     *
     * @param inSuggestion
     */
    public void suggestionDeleted(TradeSuggestion inSuggestion)
    {
        sendToAll(new DeleteSuggestionAction(inSuggestion.getIdentifier()));
        removeSuggestion(inSuggestion);
    }
    /**
     * Remove the given trade suggestion from the managed collection.
     * 
     * @param inSuggestion a <code>TradeSuggestion</code> value
     */
    private void removeSuggestion(TradeSuggestion inSuggestion)
    {
        mSuggestions.remove(inSuggestion);
        requestRefreshFromAll();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.SinkDataListener#receivedData(org.marketcetera.module.DataFlowID, java.lang.Object)
     */
    @Override
    public void receivedData(DataFlowID inFlowID,
                             Object inData)
    {
        if(inData instanceof HasSuggestionAction) {
            switch(((HasSuggestionAction)inData).getSuggestionAction()) {
                case CLEAR:
                    PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                        @Override
                        public void run()
                        {
                            mSuggestions.clear();
                        }
                    });
                    break;
                case ADD:
                    // do nothing yet, allow the block below to catch this
                    break;
                case DELETE:
                    // TODO remove the trade suggestion from the list (this supports a suggestion being deleted by someone else)
                    break;
                case SEND:
                case REFRESH:
                default:
                    throw new UnsupportedOperationException();
            }
        }
        if(inData instanceof OrderSingleSuggestion) {
            OrderSingleSuggestion suggestion = (OrderSingleSuggestion)inData;
            if(suggestion.getOrder() != null) {
                addSuggestion(suggestion,
                              getLabel(inFlowID));
            } else {
                Messages.TRADE_SUGGESTION_MANAGER_INVALID_DATA_NO_ORDER.error(this);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * 
     *
     *
     * @param inSuggestionAction
     */
    private void sendToAll(HasSuggestionAction inSuggestionAction)
    {
        synchronized(activeStrategyEngines) {
            for(StrategyEngine strategyEngine : activeStrategyEngines) {
                sendAction(strategyEngine,
                           inSuggestionAction);
            }
        }
    }
    /**
     * 
     *
     *
     */
    private void requestRefreshFromAll()
    {
        // TODO we can't allow the server to push CLEAR because there may be multiple sources
        sendToAll(RefreshSuggestionAction.instance);
    }
    /**
     *
     *
     * @param inStrategyEngine
     * @param inAction
     */
    private void sendAction(StrategyEngine inStrategyEngine,
                            HasSuggestionAction inAction)
    {
        if(inStrategyEngine.getConnectionState() == ConnectionState.CONNECTED) {
            try {
                inStrategyEngine.getConnection().sendData(inAction);
            } catch (Exception e) {
                SLF4JLoggerProxy.error(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                       "Unable to send suggestion refresh request to {}",
                                       inStrategyEngine.getName());
                SLF4JLoggerProxy.error(this,
                                       e,
                                       "Unable to send suggestion refresh request to {}",
                                       inStrategyEngine.getName());
            }
        }
    }
    /**
     * Render a human-readable label from the given data flow id.
     *
     * @param inDataFlowId a <code>DateFlowID</code> value
     * @return a <code>String</code> value
     */
    private String getLabel(DataFlowID inDataFlowId)
    {
        IDataFlowLabelProvider labelProvider = ModuleSupport.getDataFlowLabelProvider();
        if(labelProvider != null) {
            String label = labelProvider.getLabel(inDataFlowId);
            if(label != null) {
                return label;
            }
        }
        return inDataFlowId.getValue();
    }
    /**
    /**
     * monitors strategy engine changes
     */
    private final StrategyEngineMonitor strategyEngineMonitor;
    /**
     * Sink data manager
     */
    private final ISinkDataManager mSinkDataManager;
    /**
     * holds trade suggestions
     */
    private final WritableList mSuggestions = WritableList.withElementType(TradeSuggestion.class);
    /**
     * tracks active strategy engines
     */
    private final Set<StrategyEngine> activeStrategyEngines = Sets.newHashSet();
}
