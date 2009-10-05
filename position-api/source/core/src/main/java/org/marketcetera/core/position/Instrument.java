package org.marketcetera.core.position;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A financial instrument in which you can take a position.
 * <p>
 * Clients should not implement this directly, but instead subclass
 * {@link AbstractInstrument} to ensure consistent notions of equality and
 * ordering.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
@Immutable
public interface Instrument extends Comparable<Instrument> {

    /**
     * Returns the underlying symbol for the instrument, if any.
     * 
     * @return the underlying symbol, never empty but null if there is none
     */
    @Nullable
    String getUnderlying();

    /**
     * An id that uniquely identifies the instrument type. All instrument
     * instances of the same Java class must have the same type id and no two
     * instrument classes may share the same id. This is used to impose a
     * natural ordering across all instruments. It is the responsibility of
     * integrators to ensure there is no id collisions among instruments used
     * with this package.
     * 
     * @return the instrument type id
     */
    int getTypeId();
}
