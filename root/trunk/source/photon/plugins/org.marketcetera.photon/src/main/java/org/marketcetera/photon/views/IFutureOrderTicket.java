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
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public interface IFutureOrderTicket
        extends IOrderTicket
{
    /**
     * Gets the expiration month widget.
     *
     * @return a <code>Combo</code> value
     */
    Combo getExpirationMonthCombo();
    /**
     * Gets the expiration year widget.
     *
     * @return a <code>Text</code> value
     */
    Text getExpirationYearText();
}
