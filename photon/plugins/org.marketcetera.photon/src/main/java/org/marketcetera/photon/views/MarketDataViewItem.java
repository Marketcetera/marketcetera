package org.marketcetera.photon.views;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.MessageFormat;

import org.apache.commons.lang.Validate;
import org.marketcetera.photon.marketdata.IMarketData;
import org.marketcetera.photon.marketdata.IMarketDataReference;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.MDMarketstat;
import org.marketcetera.photon.model.marketdata.MDTopOfBook;
import org.marketcetera.photon.ui.ISymbolProvider;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * Market data view model object.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class MarketDataViewItem
        implements ISymbolProvider
{
    /**
     * Create a new MarketDataViewItem instance.
     *
     * @param inMarketData an <code>IMarketData</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inExchange a <code>String</code> value
     * @param inSymbolPattern a <code>String</code> value
     */
    public MarketDataViewItem(IMarketData inMarketData,
                              Instrument inInstrument,
                              String inExchange,
                              String inSymbolPattern)
    {
        Validate.noNullElements(new Object[] { inMarketData, inInstrument, inSymbolPattern });
        instrument = inInstrument;
        mMarketData = inMarketData;
        exchange = inExchange;
        symbolPattern = inSymbolPattern;
        init();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.ui.ISymbolProvider#getInstrument()
     */
    @Override
    public Instrument getInstrument()
    {
        return instrument;
    }
    /**
     * Get the symbol pattern used to create this item.
     *
     * @return a <code>String</code> value
     */
    public String getSymbolPattern()
    {
    	return symbolPattern;
    }
    /**
     * Get the latest tick value.
     *
     * @return an <code>MDLatestTick</code> value or <code>null</code>
     */
    public MDLatestTick getLatestTick()
    {
        return latestTickSource.get();
    }
    /**
     * Get the top-of-book value.
     *
     * @return an <code>MDTopOfBook</code> value or <code>null</code>
     */
    public MDTopOfBook getTopOfBook()
    {
        return topOfBookSource.get();
    }
    /**
     * Get the market stat value.
     *
     * @return an <code>MDMarketstat</code> value or <code>null</code>
     */
    public MDMarketstat getMarketStat()
    {
        return marketStatSource.get();
    }
    /**
     * Dispose of the resources for this item.
     */
    public void dispose()
    {
        latestTickSource.dispose();
        topOfBookSource.dispose();
        marketStatSource.dispose();
    }
    /**
     * Add the given property change listener.
     *
     * @param inPropertyName a <code>String</code> value
     * @param inListener a <code>PropertyChangeListener</code> value
     * @see PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)
     */
    public void addPropertyChangeListener(String inPropertyName,
                                          PropertyChangeListener inListener)
    {
        propertyChangeSupport.addPropertyChangeListener(inPropertyName,
                                                        inListener);
    }
    /**
     * Remove the given property change listener.
     *
     * @param inPropertyName a <code>String</code> value
     * @param inListener a <code>PropertyChangeListener</code> value
     * @see PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)
     */
    public void removePropertyChangeListener(String inPropertyName,
                                             PropertyChangeListener inListener)
    {
        propertyChangeSupport.removePropertyChangeListener(inPropertyName,
                                                           inListener);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return FORMAT.format(new Object[] { getInstrument(), getLatestTick().getPrice(),
                getLatestTick().getSize(), getTopOfBook().getBidSize(),
                getTopOfBook().getBidPrice(), getTopOfBook().getAskPrice(),
                getTopOfBook().getAskSize(), getMarketStat().getPreviousClosePrice(),
                getMarketStat().getPreviousCloseDate(), getMarketStat().getCloseDate(),getMarketStat().getClosePrice(),
                getMarketStat().getOpenPrice(), getMarketStat().getHighPrice(), getMarketStat().getLowPrice(), 
                getMarketStat().getVolumeTraded()});
	}
    /**
     * Init the resources for this item.
     */
    private void init()
    {
        latestTickSource = mMarketData.getLatestTick(instrument,
                                                     exchange);
        topOfBookSource = mMarketData.getTopOfBook(instrument,
                                                   exchange);
        marketStatSource = mMarketData.getMarketstat(instrument,
                                                     exchange);
    }
    /**
     * provides the format for the human-readable version of this object
     */
    private static final MessageFormat FORMAT = new MessageFormat("{0}\t{1}\t{2}\t{3}\t{4}\t{5}\t{6}\t{7}\t{8}\t{9}\t{10}\t{11}"); //$NON-NLS-1$
    /**
     * requested instrument
     */
    private Instrument instrument;
    /**
     * requested exchange, may be <code>null</code>
     */
    private final String exchange;
    /**
     * symbol pattern originally requested for this item
     */
    private final String symbolPattern;
    /**
     * source of latest tick market data
     */
    private IMarketDataReference<MDLatestTick> latestTickSource;
    /**
     * source of top-of-book market data
     */
    private IMarketDataReference<MDTopOfBook> topOfBookSource;
    /**
     * source of marketstat market data
     */
    private IMarketDataReference<MDMarketstat> marketStatSource;
    /**
     * provides property change observation services
     */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    /**
     * provides access to market data
     */
    private IMarketData mMarketData;
}
