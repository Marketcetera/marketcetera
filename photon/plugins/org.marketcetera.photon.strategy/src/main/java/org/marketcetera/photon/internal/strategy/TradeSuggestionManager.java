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
import org.marketcetera.trade.HasSuggestionAction;
import org.marketcetera.trade.OrderSingleSuggestion;
import org.marketcetera.trade.RefreshSuggestionAction;
import org.marketcetera.trade.SuggestionAction;
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
                clearAndRequestRefreshFromAll();
                break;
            default:
                throw new UnsupportedOperationException();
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
     * Indicates that the given suggestion was opened.
     *
     * @param inSuggestion a <code>TradeSuggestion</code> value
     */
    public void suggestionOpened(TradeSuggestion inSuggestion)
    {
        suggestionSent(inSuggestion);
    }
    /**
     * Indicates that the given suggestion was sent.
     *
     * @param inSuggestion a <code>TradeSuggestion</code> value
     */
    public void suggestionSent(TradeSuggestion inSuggestion)
    {
        OrderSingleSuggestion underlyingSuggestion = inSuggestion.getSuggestion();
        if(underlyingSuggestion instanceof HasSuggestionAction) {
            removeSuggestion(inSuggestion);
            ((HasSuggestionAction)underlyingSuggestion).setSuggestionAction(SuggestionAction.SEND);
            sendToAll((HasSuggestionAction)underlyingSuggestion);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Indicates that the given suggestion was deleted.
     *
     * @param inSuggestion a <code>TradeSuggestion</code> value
     */
    public void suggestionDeleted(TradeSuggestion inSuggestion)
    {
        OrderSingleSuggestion underlyingSuggestion = inSuggestion.getSuggestion();
        if(underlyingSuggestion instanceof HasSuggestionAction) {
            removeSuggestion(inSuggestion);
            ((HasSuggestionAction)underlyingSuggestion).setSuggestionAction(SuggestionAction.DELETE);
            sendToAll((HasSuggestionAction)underlyingSuggestion);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.SinkDataListener#receivedData(org.marketcetera.module.DataFlowID, java.lang.Object)
     */
    @Override
    public void receivedData(DataFlowID inFlowID,
                             Object inData)
    {
        TradeSuggestion photonTradeSuggestion;
        if(inData instanceof OrderSingleSuggestion) {
            OrderSingleSuggestion suggestion = (OrderSingleSuggestion)inData;
            if(suggestion.getOrder() != null) {
                photonTradeSuggestion = new TradeSuggestion(suggestion,
                                                            getLabel(inFlowID),
                                                            new Date());
            } else {
                Messages.TRADE_SUGGESTION_MANAGER_INVALID_DATA_NO_ORDER.error(this);
                return;
            }
        } else {
            throw new UnsupportedOperationException();
        }
        if(inData instanceof HasSuggestionAction) {
            switch(((HasSuggestionAction)inData).getSuggestionAction()) {
                case ADD:
                    addSuggestion(photonTradeSuggestion);
                    break;
                case DELETE:
                    removeSuggestion(photonTradeSuggestion);
                    break;
                case REFRESH:
                    // this is a client-to-server action, so cannot be processed here
                default:
                    throw new UnsupportedOperationException();
            }
        } else {
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
        inStrategyEngineMonitor.subscribe(this);
        mSinkDataManager.register(this,
                                  OrderSingleSuggestion.class);
        IObservableList currentStrategyEngines = inStrategyEngineMonitor.getStrategyEngineList();
        if(currentStrategyEngines != null) {
            for(Object o : currentStrategyEngines) {
                publishTo(o);
            }
        }
    }
    /**
     * Adds a trade suggestion to the managed collection.
     *
     * @param inSuggestion a <code>TradeSuggestion</code> value
     */
    private void addSuggestion(final TradeSuggestion inSuggestion)
    {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                mSuggestions.add(inSuggestion);
            }
        });
    }
    /**
     * Remove the given trade suggestion from the managed collection.
     * 
     * @param inSuggestion a <code>TradeSuggestion</code> value
     */
    private void removeSuggestion(final TradeSuggestion inSuggestion)
    {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            @Override
            public void run()
            {
                mSuggestions.remove(inSuggestion);
            }
        });
    }
    /**
     * Send the given action to all active strategy engines.
     *
     * @param inSuggestionAction a <code>HasSuggestionAction</code> value
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
     * Clear current suggestions and request each engine to send all active suggestions.
     */
    private void clearAndRequestRefreshFromAll()
    {
        clearSuggestions();
        sendToAll(RefreshSuggestionAction.instance);
    }
    /**
     * Clear suggestions from the local store.
     */
    private void clearSuggestions()
    {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            @Override
            public void run()
            {
                mSuggestions.clear();
            }
        });
    }
    /**
     * Send the given action to the given engine if the engine if appropriate.
     *
     * @param inStrategyEngine a <code>StrategyEngine</code> value
     * @param inAction a <code>HasSuggestionAction</code> value
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
