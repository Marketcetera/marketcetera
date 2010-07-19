package org.marketcetera.event;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates the meta-type of the corresponding event.
 * 
 * <p>Market data events come either as initial snapshots or as atomic updates.
 * Atomic update events can be processed on their own, but a stream of snapshot
 * events should be completely processed before beginning any analysis.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public enum EventType
{
    /**
     * indicates that the corresponding event was part of a snapshot
     */
    SNAPSHOT_PART,
    /**
     * indicates that the corresponding event was the last event of an initial
     * snapshot
     */
    SNAPSHOT_FINAL,
    /**
     * indicates that the corresponding event was part of an update
     */
    UPDATE_PART,
    /**
     * indicates that the corresponding event was the last event of an atomic update
     */
    UPDATE_FINAL,
    /**
     * indicates that the event type is not known
     */
    UNKNOWN;
}
