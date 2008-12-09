package org.marketcetera.photon;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface for validating a broker id string.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface IBrokerIdValidator {

	/**
	 * Returns whether the provided string corresponds to a valid (and
	 * available) broker.
	 * 
	 * @param brokerId
	 *            broker ID to validate
	 * @return true if the provided id corresponds to an available broker
	 */
	boolean isValid(String brokerId);

}