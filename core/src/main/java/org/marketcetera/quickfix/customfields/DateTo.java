package org.marketcetera.quickfix.customfields;

import java.util.Date;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.time.DateService;

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
        super(FIELD, DateService.toUtcDateTime(new Date()));
    }

    public DateTo(Date data) {
        super(FIELD, DateService.toUtcDateTime(data));
    }
    private static final long serialVersionUID = 5479866651113375958L;
}
