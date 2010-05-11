package org.marketcetera.core.instruments;

import java.util.regex.Pattern;

import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.ExpirableInstrument;

import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.*;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class ExpirableInstrumentToMessage<I extends ExpirableInstrument>
        extends InstrumentToMessage<I>
{
    /**
     * Create a new ExpirableInstrumentToMessage instance.
     *
     * @param inInstrument
     */
    protected ExpirableInstrumentToMessage(Class<I> inInstrument)
    {
        super(inInstrument);
    }
    /**
     * 
     *
     *
     * @param inInstrument
     * @param inMsgType
     * @param inDictionary
     * @param inMessage
     */
    protected void setExpiry(I inInstrument,
                             String inMsgType,
                             DataDictionary inDictionary,
                             Message inMessage)
    {
        final String expiry = inInstrument.getExpiry();
        if(inDictionary.isMsgField(inMsgType,
                                   MaturityMonthYear.FIELD)) {
            inMessage.setField(new MaturityMonthYear(expiry));
        }
        if(inDictionary.isMsgField(inMsgType,
                                   MaturityDay.FIELD) &&
           isYYYYMMDD(expiry)) {
            inMessage.setField(new MaturityDay(expiry.substring(6)));
        }
        if(inDictionary.isMsgField(inMsgType,
                                   MaturityDate.FIELD) &&
           isYYYYMMDD(expiry)) {
            inMessage.setField(new MaturityDate(expiry));
        }
    }
    protected void setSecurityTypeAndExpiry(I inInstrument,
                                            String inBeginString,
                                            Message inMessage)
    {
        String expiry = inInstrument.getExpiry();
        switch(FIXVersion.getFIXVersion(inBeginString)){
            case FIX_SYSTEM: //fall through
            case FIX41: //fall through
            case FIX42:
                setSecurityType(inInstrument,
                                inBeginString,
                                inMessage);
                if(expiry.length() > 6) {
                    inMessage.setField(new MaturityDay(expiry.substring(6)));
                    expiry = expiry.substring(0,6);
                }
                inMessage.setField(new MaturityMonthYear(expiry));
                break;
            case FIX43:
                setCFICode(inMessage,
                           inInstrument);
                inMessage.setField(new MaturityDate(expiry));
                break;
            default:
                setCFICode(inMessage,
                           inInstrument);
                //set expiry month year
                inMessage.setField(new MaturityMonthYear(expiry));
                break;
        }
    }
    /**
     * Sets the CFI code for the option on the message.
     *
     * @param inMessage the message
     * @param inInstrument the option
     */
    protected void setCFICode(Message inMessage,
                              I inInstrument)
    {
        String cfiCode = CFICodeUtils.getCFICode(inInstrument);
        if(cfiCode != null) {
            inMessage.setField(new CFICode(cfiCode));
        }
    }
    /**
     * Returns true if the option expiry includes the day.
     *
     * @param inExpiry the expiry value to test
     *
     * @return true if the expiry includes the day
     */
    private static boolean isYYYYMMDD(String inExpiry)
    {
        return YYYYMMDD.matcher(inExpiry).matches();
    }
    /**
     * Pattern to figure out if the option expiry date includes the day.
     */
    private static final Pattern YYYYMMDD = Pattern.compile("^(\\d{4})((0[1-9])|(1[012]))((0[1-9])|([12]\\d)|(3[01]))$");  //$NON-NLS-1$
}
