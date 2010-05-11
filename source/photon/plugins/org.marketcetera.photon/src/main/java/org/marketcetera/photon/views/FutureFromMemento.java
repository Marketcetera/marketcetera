package org.marketcetera.photon.views;

import static org.marketcetera.photon.views.FutureToMemento.FUTURE_TAG;
import static org.marketcetera.photon.views.FutureToMemento.SYMBOL_ATTRIBUTE;
import static org.marketcetera.photon.views.OptionToMemento.EXPIRY_ATTRIBUTE;

import org.eclipse.ui.IMemento;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FutureFromMemento
        extends InstrumentFromMemento
{
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.InstrumentFromMemento#doRestore(org.eclipse.ui.IMemento)
     */
    @Override
    protected Instrument doRestore(IMemento inMemento)
    {
        IMemento future = inMemento.getChild(FUTURE_TAG);
        return new Future(future.getString(SYMBOL_ATTRIBUTE),
                          future.getString(EXPIRY_ATTRIBUTE));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.DynamicInstrumentHandler#isHandled(java.lang.Object)
     */
    @Override
    protected boolean isHandled(IMemento inValue)
    {
        return inValue.getChildren(FUTURE_TAG).length == 1;
    }
}
