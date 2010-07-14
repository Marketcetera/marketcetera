package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The XSWT future order ticket.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface IFutureOrderTicket
        extends IOrderTicket
{
    /**
     * Gets the customer info widget.
     *
     * @return a <code>Combo</code> value
     */
    Combo getCustomerInfoCombo();
    Text getExpiryText();
}
