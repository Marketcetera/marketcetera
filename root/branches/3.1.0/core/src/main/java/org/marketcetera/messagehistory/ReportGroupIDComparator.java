package org.marketcetera.messagehistory;

import org.marketcetera.trade.OrderID;
import org.marketcetera.util.misc.ClassVersion;

import java.util.Comparator;

/* $License$ */
/**
 * Compares Report holders based on their OrderID values.
 *
 * @author anshul@marketcetera.com
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ReportGroupIDComparator implements Comparator<ReportHolder> {

    public int compare(ReportHolder inHolder1, ReportHolder inHolder2) {
        OrderID id1 = inHolder1 == null
                ? null
                : inHolder1.getGroupID();
        OrderID id2 = inHolder2 == null
                ? null
                : inHolder2.getGroupID();
        if (id1 != null && id2 != null){
            return id1.getValue().compareTo(id2.getValue());
        } else if (id1 == null && id2 == null){
            return 0;
        } else {
            return id1 == null ? -1 : 1;
        }
    }

}