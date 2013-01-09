package org.marketcetera.orderloader.fix;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.FIXOrder;
import static org.marketcetera.orderloader.Messages.*;
import org.marketcetera.orderloader.RowProcessor;
import org.marketcetera.orderloader.OrderProcessor;
import org.marketcetera.orderloader.OrderParsingException;
import org.marketcetera.orderloader.Messages;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.FIXFieldConverterNotAvailable;
import quickfix.*;
import quickfix.field.*;

import java.util.Vector;
import java.math.BigDecimal;

/* $License$ */
/**
 * The processor that parses rows into FIX Messages.
 *
 * @author gmiller
 * @author toli
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class FIXProcessor extends RowProcessor {
    /**
     * Creates an instance.
     *
     * @param inProcessor the processor.
     * @param inBrokerID the brokerID.
     * @param inFIXVersion the FIX Version to use for parsing, constructing
     * and validating messages.
     *
     * @throws org.marketcetera.orderloader.OrderParsingException if the
     * supplied FIX Version cannot be supported or
     * if brokerID was not supplied.
     */
    public FIXProcessor(OrderProcessor inProcessor,
                        BrokerID inBrokerID,
                        FIXVersion inFIXVersion)
    throws OrderParsingException {
        super(inProcessor, inBrokerID);
        mMsgFactory = inFIXVersion.getMessageFactory();
        try {
            mDictionary = new FIXDataDictionary(inFIXVersion.getDataDictionaryURL());
        } catch (FIXFieldConverterNotAvailable e) {
            throw new OrderParsingException(e, new I18NBoundMessage1P(
                    Messages.ERROR_PROCESS_FIX_VERSION,
                    inFIXVersion.toString()));
        }
        if(inBrokerID == null) {
            throw new OrderParsingException(Messages.BROKER_ID_REQUIRED);
        }
    }

    @Override
    public void setHeaders(String[] inHeaders) throws OrderParsingException {
        mHeaderNames = inHeaders;
        mHeaderFields = new Vector<Field<?>>(inHeaders.length);
        for(String field : inHeaders) {
            mHeaderFields.add(getQuickFixFieldFromName(field));
        }

    }

    @Override
    protected Order parseOrder(String[] inRow) throws OrderParsingException {
        Message message = mMsgFactory.newBasicOrder();
        // set defaults first b/c they may be overridden for MKT orders
        addDefaults(message);
        for(int i=0;i<mHeaderFields.size();i++)
        {
            Field<?> theField = mHeaderFields.get(i);
            String value = parseMessageValue(theField, mHeaderNames[i],
                    inRow[i], message);
            if(value!=null) {
                int fieldID = theField.getField();
                if(mDictionary.getDictionary().isMsgField(
                        MsgType.ORDER_SINGLE, fieldID)) {
                    message.setField(new StringField(fieldID, value));
                } else if(mDictionary.getDictionary().isHeaderField(fieldID)) {
                    message.getHeader().setField(new StringField(fieldID, value));
                } else if(mDictionary.getDictionary().isTrailerField(fieldID)) {
                    message.getTrailer().setField(new StringField(fieldID, value));
                } else {
                    throw new OrderParsingException(new I18NBoundMessage2P(
                            PARSING_FIELD_NOT_IN_DICT, String.valueOf(fieldID),
                            value));
                }
            }
        }

        try {
            //Create the order to have the ClOrdID assigned.
            FIXOrder order = Factory.getInstance().createOrder(message,
                    geBrokerID());
            mDictionary.getDictionary().validate(order.getMessage(), true);
            return order;
        } catch (Exception e) {
            throw new OrderParsingException(e, new I18NBoundMessage1P(
                    Messages.PARSED_MESSAGE_FAILED_VALIDATION,
                    message.toString()));
        }
    }
    /** Translate the incoming field name from String to a FIX standard
     * using reflection. the quickfix.field package has all of these defined
     * as quickfix.field.<Name> so we just need to create a class for each
     * english string.
     *
     * If the field is not found, it could be a "undertermined" field in
     * which case we check to see if it parses out to an integer.
     * If it does, we store the int as the field value
     * Otherwise, we throw an error.
     *
     * @param inFieldName the field name.
     *
     * @return the field instance represeting that field
     *
     * @throws OrderParsingException if there were failures
     */
    protected Field<?> getQuickFixFieldFromName(String inFieldName)
        throws OrderParsingException
    {
        Field<?> theField = null;
        try {
            theField = (Field<?>) Class.forName("quickfix.field." +   //$NON-NLS-1$ 
                    inFieldName).newInstance();
        } catch(ClassNotFoundException ex) {
            // check to see if this is non-predetermined value (just an int header)
            return CustomField.getCustomField(inFieldName);
        }catch(Exception ex) {
            throw new OrderParsingException(ex, new I18NBoundMessage1P(
                    Messages.ERROR_PARSING_UNKNOWN, inFieldName));
        }
        return theField;
    }
    /**
     * For some fields (day, side, etc) we do custom lookups since
     *  the orders may be "DAY", MKT (for price), etc
     * For all others, delegate to the basic field type lookup
     * 
     * @param inField  the field we are converting
     * @param inFieldName the name of the field being processed.
     * @param inValue  string value
     * @param inMessage the message to add the processed field to.
     *
     * @return Translated data
     *
     * @throws OrderParsingException if there were errors parsing the field.
     */
    protected String parseMessageValue(Field<?> inField,
                                       String inFieldName,
                                       String inValue,
                                       Message inMessage)
        throws OrderParsingException
    {
        if(inField instanceof CustomField) {
            return ((CustomField)inField).parseMessageValue(
                    inValue).toString(); //i18n_number? BigDecimal.toString() might not give the right value
        }

        switch(inField.getField()) {
            case Side.FIELD:
                return getSide(inValue)+""; //$NON-NLS-1$
            case Price.FIELD:
                // price must be positive but can be MKT
                if(MKT_PRICE.equals(inValue)) {
                    inMessage.setField(new OrdType(OrdType.MARKET));
                    return null;
                } else {
                    BigDecimal price =  null;
                    try {
                        price = new BigDecimal(inValue);//i18n_currency
                    } catch(NumberFormatException ex) {
                        throw new OrderParsingException(ex,
                                new I18NBoundMessage1P(PARSING_PRICE_VALID_NUM,
                                        inValue));
                    }
                    if(price.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new OrderParsingException(new I18NBoundMessage1P(
                                PARSING_PRICE_POSITIVE, price));
                    }
                    // just return the original string
                    return inValue;
                }
            case OrderQty.FIELD:
                // quantity must be a positive integer
                Integer qty= null;
                try {
                    qty = Integer.parseInt(inValue);//i18n_number
                } catch(NumberFormatException ex) {
                    throw new OrderParsingException(ex,
                            new I18NBoundMessage1P(PARSING_QTY_INT, inValue));
                }
                if(qty <=0) {
                    throw new OrderParsingException(new I18NBoundMessage1P(
                            PARSING_QTY_POS_INT, inValue));
                }
                // just return the original string
                return inValue;
            case TimeInForce.FIELD:
                try {
                    java.lang.reflect.Field theField = TimeInForce.class.
                            getField(inValue);
                    return theField.get(null).toString();
                } catch (Exception ex) {
                    throw new OrderParsingException(ex, inFieldName, inValue);
                }
            default:
                return inValue;
        }
    }
    protected static char getSide(String inValue)
     {
         if(inValue != null) {
             inValue = inValue.toUpperCase();
             if("".equals(inValue)) { //$NON-NLS-1$
                 return Side.UNDISCLOSED;
             }
             if("B".equals(inValue)) { //$NON-NLS-1$
                 return Side.BUY;
             }
             if("S".equals(inValue)) { //$NON-NLS-1$
                 return Side.SELL;
             }
             if("SS".equals(inValue)) { //$NON-NLS-1$
                 return Side.SELL_SHORT;
             }
             if("SSE".equals(inValue)) { //$NON-NLS-1$
                 return Side.SELL_SHORT_EXEMPT;
             }
         }
         return Side.UNDISCLOSED;
     }
    protected static void addDefaults(Message message)
    {
        message.getHeader().setField(new MsgType(MsgType.ORDER_SINGLE));
        message.setField(new OrdType(OrdType.LIMIT));
        message.setField(new HandlInst(
                HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE));
        message.setField(new TransactTime()); //i18n_datetime
    }

    /**
     * Implementation artifact, exposed for unit testing.
     *
     * @return the fix fields parsed from the header.
     */
    Vector<Field<?>> getHeaderFields() {
        return mHeaderFields;
    }

    /**
     * Implementation artifact, exposed for unit testing.
     *
     * @return the names of the header columns.
     */
    String[] getHeaderNames() {
        return mHeaderNames;
    }

    protected static String MKT_PRICE = "MKT"; //$NON-NLS-1$
    private String[] mHeaderNames;
    private Vector<Field<?>> mHeaderFields;
    private final FIXMessageFactory mMsgFactory;
    private final FIXDataDictionary mDictionary;
}
