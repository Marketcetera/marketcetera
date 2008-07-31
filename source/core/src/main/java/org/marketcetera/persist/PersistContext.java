package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

import java.io.Serializable;

/* $License$ */
/**
 * A context that might be used to send extra information
 * as a part persist operations from client-side to
 * server-side. This abstraction is not used by the persistence
 * infrastructure. And is purely meant to be used by code using
 * the persistence infrastructure, in case they need to supply
 * extra data between the client-end and the server-end when
 * invoking various persistence operations.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface PersistContext extends Serializable {
}
