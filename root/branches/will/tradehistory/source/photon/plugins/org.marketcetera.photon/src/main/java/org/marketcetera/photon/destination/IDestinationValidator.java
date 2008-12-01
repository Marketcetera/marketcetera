package org.marketcetera.photon.destination;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface for validating a order destination ID string.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface IDestinationValidator {

	/**
	 * Returns whether the provided string corresponds to a valid (and
	 * available) destination ID
	 * 
	 * @param destination
	 *            destination ID to validate
	 * @return true if the provided destination is valid and available
	 */
	boolean isValid(String destination);

}