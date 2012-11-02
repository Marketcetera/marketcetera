package org.marketcetera.photon.views;


import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;


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
     * @return a <code>Text</code> value
     */
	Text getNearTenorText();   
    /**
     * Gets the far Tenor widget.
     *
     * @return a <code>Text</code> value
     */
	Text getFarTenorText();
	
    /**
     * Gets the Near Tenor button widget.
     *
     * @return a <code>Button</code> value
     */
	Button getSelectNearTenorButton();
	
    /**
     * Gets the Far Tenor button widget.
     *
     * @return a <code>Button</code> value
     */
	Button getSelectFarTenorButton();
    
}
