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
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Market data view model object.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id")
public class MarketDataViewItem implements ISymbolProvider {

	private static final MessageFormat FORMAT = new MessageFormat(
			"{0}\t{1}\t{2}\t{3}\t{4}\t{5}\t{6}\t{7}\t{8}\t{9}\t{10}\t{11}"); //$NON-NLS-1$

	private Instrument mInstrument;
	private IMarketDataReference<MDLatestTick> mLatestTick;
	private IMarketDataReference<MDTopOfBook> mTopOfBook;
	private IMarketDataReference<MDMarketstat> mMarketStat;
	
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	private IMarketData mMarketData;

	/**
	 * Constructor. Will throw an exception if symbol is null.
	 * 
	 * @param inMarketData an <code>IMarketData</code> value
	 * @param inInstrument an <code>Instrument</code> value
	 */
    public MarketDataViewItem(IMarketData inMarketData,
                              Instrument inInstrument)
    {
        Validate.noNullElements(new Object[] { inMarketData, inInstrument });
        mInstrument = inInstrument;
        mMarketData = inMarketData;
        init();
    }

	@Override
	public Instrument getInstrument() {
		return mInstrument;
	}

    public String getSymbol() {
    	return mInstrument.getFullSymbol();
    }

	public MDLatestTick getLatestTick() {
		return mLatestTick.get();
	}

	public MDTopOfBook getTopOfBook() {
		return mTopOfBook.get();
	}

	public MDMarketstat getMarketStat(){
		return mMarketStat.get();
	}

	/**
     * Changes the underlying instrument of this item. All data will be reset if
     * the instrument changes.
	 * 
	 * @param instrument
	 *            the new instrument, cannot be null
	 */
	public void setInstrument(Instrument instrument) {
		Validate.notNull(instrument);
		String oldSymbol = getSymbol();
		if (!mInstrument.equals(instrument)) {
			MDLatestTick oldLatestTick = getLatestTick();
			MDTopOfBook oldTopOfBook = getTopOfBook();
			MDMarketstat oldMarketStat = getMarketStat();
			dispose();
			mInstrument = instrument;
			init();
			propertyChangeSupport.firePropertyChange("symbol", oldSymbol, getSymbol()); //$NON-NLS-1$
			propertyChangeSupport.firePropertyChange("latestTick", oldLatestTick, getLatestTick()); //$NON-NLS-1$
			propertyChangeSupport.firePropertyChange("topOfBook", oldTopOfBook, getTopOfBook()); //$NON-NLS-1$
			propertyChangeSupport.firePropertyChange("marketStat", oldMarketStat, getMarketStat()); //$NON-NLS-1$
		}
	}

	public void dispose() {
		mLatestTick.dispose();
		mTopOfBook.dispose();
		mMarketStat.dispose();
	}

	private void init() {
		mLatestTick = mMarketData.getLatestTick(mInstrument);
		mTopOfBook = mMarketData.getTopOfBook(mInstrument);
		mMarketStat = mMarketData.getMarketstat(mInstrument);
	}

	/**
	 * @see PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * @see PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * This implementation returns a string representation of the item's properties separated by
	 * tabs.
	 */
	@Override
	public String toString() {
		return FORMAT.format(new Object[] { getInstrument(), getLatestTick().getPrice(),
				getLatestTick().getSize(), getTopOfBook().getBidSize(),
				getTopOfBook().getBidPrice(), getTopOfBook().getAskPrice(),
				getTopOfBook().getAskSize(), getMarketStat().getPreviousClosePrice(),
				getMarketStat().getPreviousCloseDate(), getMarketStat().getCloseDate(),getMarketStat().getClosePrice(),
				getMarketStat().getOpenPrice(), getMarketStat().getHighPrice(), getMarketStat().getLowPrice(), 
				getMarketStat().getVolumeTraded()});
	}
}
