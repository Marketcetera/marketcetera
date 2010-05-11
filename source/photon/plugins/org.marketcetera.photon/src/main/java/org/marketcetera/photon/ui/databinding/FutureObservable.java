package org.marketcetera.photon.ui.databinding;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.photon.commons.databinding.ITypedObservableValue;
import org.marketcetera.trade.Future;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class FutureObservable
        extends ExpirableInstrumentObservableManager<Future>
{
    /**
     * Create a new FutureObservable instance.
     *
     * @param inParent
     */
    public FutureObservable(ITypedObservableValue<Future> inParent)
    {
        super(inParent);
        init(ImmutableList.of(getSymbol(),
                              getExpiry()));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.ui.databinding.CompoundObservableManager#updateParent()
     */
    @Override
    protected void updateParent()
    {
        String symbol = getSymbol().getTypedValue();
        String expiry = getExpiry().getTypedValue();
        Future newValue = null;
        if(StringUtils.isNotBlank(symbol) &&
           StringUtils.isNotBlank(expiry)) {
            newValue = new Future(symbol,
                                  expiry);
        }
        ITypedObservableValue<Future> future = getParent();
        setIfChanged(future,
                     newValue);
    }
}
