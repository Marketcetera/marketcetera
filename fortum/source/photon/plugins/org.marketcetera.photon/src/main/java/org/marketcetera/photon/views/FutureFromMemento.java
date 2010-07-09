package org.marketcetera.photon.views;

import static org.marketcetera.photon.views.FutureToMemento.CUSTOMER_INFO_ATTRIBUTE;
import static org.marketcetera.photon.views.FutureToMemento.FUTURE_TAG;
import static org.marketcetera.photon.views.FutureToMemento.SYMBOL_ATTRIBUTE;

import org.eclipse.ui.IMemento;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Facilitates deserialization of a {@link Future} from an Eclipse
 * {@link IMemento} .
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
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
                          future.getString(CUSTOMER_INFO_ATTRIBUTE));
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
