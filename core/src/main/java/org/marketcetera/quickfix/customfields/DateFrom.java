package org.marketcetera.quickfix.customfields;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.time.DateService;

import quickfix.UtcTimeStampField;

import java.util.Date;

/**
 * Implementation of a custom field 9883: DateFrom
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class DateFrom extends UtcTimeStampField {
    public static final int FIELD = 9883;
    public DateFrom() {
        super(FIELD, DateService.toUtcDateTime(new Date()));
    }

    public DateFrom(Date date) {
        super(FIELD, DateService.toUtcDateTime(date));
    }
    private static final long serialVersionUID = -2169346763561325201L;
}
