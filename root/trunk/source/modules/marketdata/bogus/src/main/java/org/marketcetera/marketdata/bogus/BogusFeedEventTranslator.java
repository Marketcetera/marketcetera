package org.marketcetera.marketdata.bogus;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTranslator;
import org.marketcetera.event.HasOption;
import org.marketcetera.event.UnsupportedEventException;
import org.marketcetera.options.OptionUtils;
import org.marketcetera.trade.Option;
import org.marketcetera.util.log.I18NBoundMessage1P;

/* $License$ */

/**
 * Bogus feed {@link EventTranslator} instance.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public class BogusFeedEventTranslator
        implements EventTranslator, Messages
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(java.lang.Object)
     */
    public List<Event> toEvent(Object inData,
                                   String inHandle)
            throws CoreException
    {
        if(!(inData instanceof Event)) {
            throw new UnsupportedEventException(new I18NBoundMessage1P(UNKNOWN_EVENT_TYPE,
                                                                       ObjectUtils.toString(inData,
                                                                                            null)));
        }
        Event event = (Event)inData;
        // if the event type is an option, then the symbol has to be specified in OSI format.  this is only
        //  fair as we are requiring that on the incoming side.  it's not the most efficient place to do this,
        //  but it needs to be done.
        if(event instanceof HasOption) {
            Option option = ((HasOption)event).getInstrument();
            try {
                OptionUtils.getOsiOptionFromString(option.getSymbol());
                // the symbol is already in OSI format, nothing to do 
            } catch (IllegalArgumentException e) {
                // the symbol is not in OSI format - this needs to be fixed
                try {
                    String symbol = OptionUtils.getOsiSymbolFromOption(option);
                    // need to rebuild the event with this symbol instead of the one that's there
                    // note this is awful and needs to be done only until the underlying architecture is fixed for
                    //  market data requests.  anyway, kids, don't try this at home
                    Field f = option.getClass().getDeclaredField("mSymbol"); //$NON-NLS-1$
                    f.setAccessible(true);
                    f.set(option,
                          symbol);
                } catch (IllegalArgumentException e2) {
                    // bah, give up, not sure what to do here, just let it fall through and return the event as is
                } catch (Exception e3) {
                    // something worse has happened
                    throw new CoreException(e3);
                }
            }
        }
        return Arrays.asList(new Event[] { event } );
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(org.marketcetera.event.EventBase)
     */
    public Object fromEvent(Event inEvent)
            throws CoreException
    {
        throw new UnsupportedOperationException();
    }
    /**
     * static instance of <code>BogusFeedEventTranslator</code>
     */
    private static final BogusFeedEventTranslator sInstance = new BogusFeedEventTranslator();
    /**
     * Gets a <code>BogusFeedEventTranslator</code> instance.
     * 
     * @return a <code>BogusFeedEventTranslator</code> instance
     */
    static BogusFeedEventTranslator getInstance()
    {
        return sInstance;
    }
}
