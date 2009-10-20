package org.marketcetera.event.impl;

import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.marketdata.MarketDataRequestException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
final class EventValidationServices
{
    static void validateDate(String inDate,
                             I18NBoundMessage inErrorMessage)
    {
        try {
            DateUtils.stringToDate(inDate,
                                   DateUtils.DAYS);
        } catch (MarketDataRequestException e) {
            try {
                DateUtils.stringToDate(inDate,
                                       DateUtils.MONTHS);
            } catch (MarketDataRequestException e2) {
                // TODO
//                throw EventValidationException.error(inErrorMessage);
            }
        }
    }
}
