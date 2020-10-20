package org.marketcetera.core.notifications;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;
import org.nocrala.tools.texttablefmt.CellStyle.HorizontalAlign;

/* $License$ */

/**
 * Sends notifications to the console.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: EmailNotificationExecutorMethod.java 17879 2019-08-19 17:30:03Z colin $
 * @since $Release$
 */
public class ConsoleNotificationExecutorMethod
        extends AbstractNotificationExecutorMethod
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.AbstractNotificationExecutorMethod#doNotify(org.marketcetera.core.notifications.INotification)
     */
    @Override
    protected void doNotify(INotification inNotification)
            throws Exception
    {
        Table table = new Table(1,
                                BorderStyle.CLASSIC_COMPATIBLE_WIDE,
                                ShownBorders.ALL,
                                false);
        table.addCell(String.valueOf(inNotification),
                      cellStyle);
        String notification = table.render();
        SLF4JLoggerProxy.info(this,
                              "{}{}{}",
                              System.lineSeparator(),
                              notification,
                              System.lineSeparator());
        System.out.println(notification);
    }
    /**
     * describes the style of the table cell
     */
    private static final CellStyle cellStyle = new CellStyle(HorizontalAlign.center);
}
