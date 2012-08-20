package org.marketcetera.core.quickfix.customfields;

import java.util.Date;

import org.marketcetera.api.attributes.ClassVersion;

import quickfix.UtcTimeStampField;

/**
 * Implementation of a custom field 9884: DateTo
 * @author toli
 * @version $Id: DateTo.java 16063 2012-01-31 18:21:55Z colin $
 */

@ClassVersion("$Id: DateTo.java 16063 2012-01-31 18:21:55Z colin $")
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
