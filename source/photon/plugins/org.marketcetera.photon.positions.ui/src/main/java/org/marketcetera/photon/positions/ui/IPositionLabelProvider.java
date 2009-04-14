package org.marketcetera.photon.positions.ui;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface to configure the way positions are labeled.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface IPositionLabelProvider {

	/**
	 * Returns the user-friendly/human-readable name for a given trader id.
	 * 
	 * @param traderId the trader id
	 * @return the trader name
	 */
	String getTraderName(String traderId);
}
