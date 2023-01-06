package org.marketcetera.quickfix.customfields;

import org.marketcetera.core.ClassVersion;
import quickfix.UtcTimeStampField;

import java.util.Date;

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
    private static final long serialVersionUID = 5479866651113375958L;
}
