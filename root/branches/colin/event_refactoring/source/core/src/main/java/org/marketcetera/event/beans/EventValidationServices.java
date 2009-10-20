package org.marketcetera.event.beans;

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
    /**
     * 
     *
     *
     * @param inErrorMessage
     */
    static void error(I18NBoundMessage inErrorMessage)
    {
        throw new IllegalArgumentException(inErrorMessage.getText());
    }
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
