package org.marketcetera.event;

import java.util.List;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Translates to and from {@link Event} format.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public interface EventTranslator
{
    /**
     * Translates from an unspecified format to an <code>Event</code> object.
     * 
     * <p>This method will attempt to translate the incoming object to a corresponding
     * subclass of <code>Event</code>.
     *
     * @param inData an <code>Object</code> value
     * @param inHandle a <code>String</code> value containing the handle indicating the request to which
     *   the response applies
     * @return a <code>List&lt;Event&gt;</code> value
     * @throws UnsupportedEventException if the object cannot be translated to an object
     *   of type <code>Event</code>
     * @throws CoreException if another error occurs
     */
    public List<Event> toEvent(Object inData,
                               String inHandle)
        throws CoreException;
    /**
     * Translates from <code>Event</code> format to a format specified by the implementer.
     *
     * <p>Implementers should specialize the return-type of this method to indicate the function
     * of the method.  For example, an implementer called <code>FooEventTranslator</code> implies
     * its function is to translate <code>Event</code> objects to and from the <code>foo</code>
     * format.  In this method, the signature for this method in <code>FooEventTranslator</code>
     * would be:
     * <pre>
     * public FooFormat translate(Event inEvent)
     * </pre>
     *
     * @param inEvent an <code>Event</code> value
     * @return an <code>Object</code> value
     * @throws CoreException
     */
    public Object fromEvent(Event inEvent)
        throws CoreException;
}
