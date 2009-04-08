package org.marketcetera.photon;

import java.math.BigDecimal;

import org.marketcetera.core.position.IncomingPositionSupport;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides incoming position data to the {@link PositionEngine} from the server.
 * 
 * TODO: implement once server support is in place
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ClientPositionSupport implements IncomingPositionSupport {

	@Override
	public BigDecimal getIncomingPositionFor(PositionKey key) {
		// TODO: implement once server support is in place
		return BigDecimal.ZERO;
	}

}
