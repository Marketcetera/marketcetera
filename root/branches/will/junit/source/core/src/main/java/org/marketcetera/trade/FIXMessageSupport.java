package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.event.HasFIXMessage;

import java.util.Map;
import java.io.Serializable;

/* $License$ */
/**
 * Interface that is implemented by types that wrap a FIX Message instance.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface FIXMessageSupport extends HasFIXMessage, Serializable {
    /**
     * Returns a map of all the fields on the FIX Message with
     * the FIX field tag numbers as keys and FIX Field values as values.
     * Do note that the returned map doesn't include header, trailer and
     * group field values or fields of that have binary data.
     * <p>
     * Do note that all field values are strings.
     * <p>
     * The returned map is not modifiable. 
     * <p>
     * It's likely that making these fields available as a map will
     * enable CEP processing engines to process them.
     *
     * @return a map of field tag numbers and field values.
     */
    Map<Integer, String> getFields();
}
