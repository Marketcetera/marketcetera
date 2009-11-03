package org.marketcetera.core.instruments;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.quickfix.FIXVersion;
import quickfix.Message;
import quickfix.DataDictionary;
import quickfix.field.*;

import java.util.regex.Pattern;

/* $License$ */
/**
 * Adds the appropriate fields for an option instrument to a FIX Message.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class OptionToMessage extends InstrumentToMessage<Option> {
    /**
     * Creates an instance that handles options.
     */
    public OptionToMessage() {
        super(Option.class);
    }

    @Override
    public void set(Instrument inInstrument, String inBeginString, Message inMessage) {
        if(FIXVersion.FIX40.equals(FIXVersion.getFIXVersion(inBeginString))) {
            throw new IllegalArgumentException(
                    Messages.OPTION_NOT_SUPPORTED_FOR_FIX_VERSION.getText(inBeginString));
        }
        inMessage.setField(new Symbol(inInstrument.getSymbol()));
        
        Option option = (Option) inInstrument;
        inMessage.setField(new StrikePrice(option.getStrikePrice()));

        String expiry = option.getExpiry();
        switch(FIXVersion.getFIXVersion(inBeginString)){
            case FIX_SYSTEM: //fall through
            case FIX41: //fall through
            case FIX42:
                setSecurityType(inInstrument, inBeginString, inMessage);
                inMessage.setField(new PutOrCall(option.getType().getFIXValue()));
                if(expiry.length() > 6) {
                    inMessage.setField(new MaturityDay(expiry.substring(6)));
                    expiry = expiry.substring(0,6);
                }
                inMessage.setField(new MaturityMonthYear(expiry));
                break;
            case FIX43:
                setCFICode(inMessage, option);
                inMessage.setField(new MaturityDate(expiry));
                break;
            default:
                setCFICode(inMessage, option);
                //set maturity month year
                inMessage.setField(new MaturityMonthYear(expiry));
                break;
        }
    }

    @Override
    public boolean isSupported(DataDictionary inDictionary, String inMsgType) {
        //if dictionary supports means to specify the 5 attributes of an option
        return
                //symbol
                (inDictionary.isMsgField(inMsgType,Symbol.FIELD)) &&
                //strike
                (inDictionary.isMsgField(inMsgType,StrikePrice.FIELD)) &&
                //security and option type
                ((inDictionary.isMsgField(inMsgType,SecurityType.FIELD) &&
                        inDictionary.isMsgField(inMsgType,PutOrCall.FIELD)) ||
                        inDictionary.isMsgField(inMsgType,CFICode.FIELD)) &&
                //expiry
                (inDictionary.isMsgField(inMsgType,MaturityDate.FIELD) ||
                        inDictionary.isMsgField(inMsgType,MaturityMonthYear.FIELD));
    }

    @Override
    public void set(Instrument inInstrument, DataDictionary inDictionary,
                    String inMsgType, Message inMessage) {
        setSecurityType(inInstrument, inDictionary, inMsgType, inMessage);
        setSymbol(inInstrument, inDictionary, inMsgType, inMessage);
        Option option = (Option) inInstrument;
        //set as many fields as are available in the dictionary.
        if(inDictionary.isMsgField(inMsgType, CFICode.FIELD)) {
            setCFICode(inMessage, option);
        }
        if(inDictionary.isMsgField(inMsgType, PutOrCall.FIELD)) {
            inMessage.setField(new PutOrCall(option.getType().getFIXValue()));
        }
        if(inDictionary.isMsgField(inMsgType, StrikePrice.FIELD)) {
            inMessage.setField(new StrikePrice(option.getStrikePrice()));
        }
        final String expiry = option.getExpiry();
        if(inDictionary.isMsgField(inMsgType, MaturityMonthYear.FIELD)) {
            inMessage.setField(new MaturityMonthYear(expiry));
        }
        if(inDictionary.isMsgField(inMsgType, MaturityDay.FIELD)&& isYYYYMMDD(expiry)) {
            inMessage.setField(new MaturityDay(expiry.substring(6)));
        }
        if(inDictionary.isMsgField(inMsgType, MaturityDate.FIELD) && isYYYYMMDD(expiry)) {
            inMessage.setField(new MaturityDate(expiry));
        }
    }

    /**
     * Sets the CFI code for the option on the message.
     *
     * @param inMessage the message
     * @param inOption the option
     */
    private static void setCFICode(Message inMessage, Option inOption) {
        String cfiCode = CFICodeUtils.getOptionCFICode(inOption.getType());
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
    private static boolean isYYYYMMDD(String inExpiry) {
        return YYYYMMDD.matcher(inExpiry).matches();
    }

    /**
     * Pattern to figure out if the option expiry date includes the day.
     */
    private static final Pattern YYYYMMDD =
            Pattern.compile("^(\\d{4})((0[1-9])|(1[012]))((0[1-9])|([12]\\d)|(3[01]))$");  //$NON-NLS-1$
}