package org.marketcetera.event;

import java.util.List;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Translates to and from {@link EventBase} format.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface EventTranslator
{
    /**
     * Translates from an unspecified format to a <code>EventBase</code> object.
     * 
     * <p>This method will attempt to translate the incoming object to a corresponding
     * subclass of <code>EventBase</code>.
     *
     * @param inData an <code>Object</code> value
     * @param inHandle a <code>String</code> value containing the handle indicating the request to which
     *   the response applies
     * @return a <code>List&lt;EventBase&gt;</code> value
     * @throws UnsupportedEventException if the object cannot be translated to an object
     *   of type <code>EventBase</code>
     * @throws CoreException if another error occurs
     */
    public List<EventBase> toEvent(Object inData,
                                   String inHandle)
        throws CoreException;
    /**
     * Translates from <code>EventBase</code> format to a format specified by the implementer.
     *
     * <p>Implementers should specialize the return-type of this method to indicate the function
     * of the method.  For example, an implementer called <code>FooEventTranslator</code> implies
     * its function is to translate <code>EventBase</code> objects to and from the <code>foo</code>
     * format.  In this method, the signature for this method in <code>FooEventTranslator</code>
     * would be:
     * <pre>
     * public FooFormat translate(EventBase inEvent)
     * </pre>
     *
     * @param inEvent an <code>EventBase</code> value
     * @return an <code>Object</code> value
     * @throws CoreException
     */
    public Object fromEvent(EventBase inEvent)
        throws CoreException;
}
