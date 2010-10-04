package org.marketcetera.photon.views;

import static org.marketcetera.photon.views.FutureToMemento.EXPIRATION_MONTH_ATTRIBUTE;
import static org.marketcetera.photon.views.FutureToMemento.EXPIRATION_YEAR_ATTRIBUTE;
import static org.marketcetera.photon.views.FutureToMemento.FUTURE_TAG;
import static org.marketcetera.photon.views.FutureToMemento.SYMBOL_ATTRIBUTE;

import org.eclipse.ui.IMemento;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.FutureExpirationMonth;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Facilitates deserialization of a {@link Future} from an Eclipse
 * {@link IMemento} .
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.0
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
                          FutureExpirationMonth.valueOf(FutureExpirationMonth.class,
                                                        future.getString(EXPIRATION_MONTH_ATTRIBUTE)),
                          future.getInteger(EXPIRATION_YEAR_ATTRIBUTE));
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
