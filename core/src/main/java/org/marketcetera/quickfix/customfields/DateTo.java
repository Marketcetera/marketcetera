package org.marketcetera.quickfix.customfields;

import java.util.Date;

import org.marketcetera.util.misc.ClassVersion;

import quickfix.UtcTimeStampField;

/**
 * Implementation of a custom field 9884: DateTo
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class DateTo extends UtcTimeStampField {
    public static final int FIELD = 9884;

    public DateTo() {
        super(FIELD, new Date()); //non-i18n
    }

    public DateTo(Date data) {
        super(FIELD, data);
    }
    private static final long serialVersionUID = 1L;
}
