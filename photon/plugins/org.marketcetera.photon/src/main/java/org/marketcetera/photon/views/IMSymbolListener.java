package org.marketcetera.photon.views;

/* $License$ */

/**
 * Listens for symbol changes.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface IMSymbolListener
{
    /**
     * Invoked when a new symbol is added.
     *
     * @param inSymbol a <code>String</code> value
     */
    void onAddSymbol(String inSymbol);
}
