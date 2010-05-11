package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface IFutureOrderTicket
        extends IOrderTicket
{
    /**
     * 
     *
     *
     * @return
     */
    Combo getExpirationMonthCombo();
    /**
     * 
     *
     *
     * @return
     */
    Text getExpirationYearText();
}
