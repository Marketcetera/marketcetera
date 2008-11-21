package org.marketcetera.photon.ui;

import org.marketcetera.core.MSymbol;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface for objects that provide a financial ticker symbol.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public interface ISymbolProvider {

	/**
	 * @return a string ticker symbol, should not be null
	 */
	MSymbol getSymbol();

}
