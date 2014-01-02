package org.marketcetera.photon.module;

import org.marketcetera.module.SinkDataListener;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface that must be implemented by sink data handlers. Clients should not implement this
 * interface directly, but instead extend the abstract {@link SinkDataHandler}.
 * 
 * Currently, this is just a marker interface, reserved for future extension.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface ISinkDataHandler extends SinkDataListener {
}
