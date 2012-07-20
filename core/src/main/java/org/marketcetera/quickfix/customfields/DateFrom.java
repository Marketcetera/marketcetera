package org.marketcetera.quickfix.customfields;

import java.util.Date;

import org.marketcetera.util.misc.ClassVersion;

import quickfix.UtcTimeStampField;

/**
 * Implementation of a custom field 9883: DateFrom
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class DateFrom extends UtcTimeStampField {
    public static final int FIELD = 9883;
    public DateFrom() {
        super(FIELD, new Date()); //non-i18n
    }

    public DateFrom(Date date) {
        super(FIELD, date);
    }
    private static final long serialVersionUID = 1L;
}
