package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Combo;


/* $License$ */

/**
 * The XSWT currency order ticket.
 *
 */
public interface ICurrencyOrderTicket
        extends IOrderTicket
{
    /**
     * Gets the near Tenor widget.
     *
     * @return a <code>Combo</code> value
     */
    Combo getNearTenorCombo();    
    /**
     * Gets the far Tenor widget.
     *
     * @return a <code>Combo</code> value
     */
    Combo getFarTenorCombo();
}
