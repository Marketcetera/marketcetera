package org.marketcetera.orderloader;

import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.core.*;
import org.skife.csv.CSVReader;
import org.skife.csv.SimpleReader;
import org.springframework.jms.core.JmsTemplate;
import org.apache.activemq.Service;
import quickfix.Field;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.*;

import javax.jms.JMSException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.math.BigDecimal;
import java.net.URL;

/**
 *  Simple class to read a CSV file containing orders and load them into the
 * JMS queue of orders
 * * File format is data-driven: the first line of the file determines the field order for the entire file.
 * Example:
 *     Symbol,Side,OrderQty,Price,TimeInForce,Account
 *     IBM,B,100,12.1,DAY,123-ASDF-234
 *
 * Special Cases:
 * 1. Price (positive)
 * 2. Quantity (positive integer)
 * @author gmiller
 * @author toli
 * $Id$
 */
@ClassVersion("$Id$")
public class OrderLoader extends ApplicationBase
{
    private static final String JMS_SENDER_NAME = "outgoingJmsTemplate";
    private static final String ID_FACTORY_URL_NAME = "idFactoryURL";
    private static final String POOLED_CONNECTION_FACTORY_NAME = "pooledConnectionFactory";

    protected static String MKT_PRICE = "MKT";
    protected static String TIME_LIMIT_DAY = "DAY";
    public static final String CFG_FILE_NAME = "orderloader.xml";
    public static final MessageBundleInfo OL_MESSAGE_BUNDLE_INFO = new MessageBundleInfo("orderloader", "orderloader_messages");

    private IDFactory idFactory;
    private JmsTemplate jmsQueueSender;

    protected int numProcessedOrders;
    protected int numBlankLines;
    protected int numComments;
    protected Vector<String> failedOrders;
    public static final String COMMENT_MARKER = "#";

    public OrderLoader() throws Exception
    {
        numProcessedOrders = numComments = numBlankLines = 0;
        failedOrders = new Vector<String>();
        createApplicationContext(new String[] {CFG_FILE_NAME}, true);
        URL idFactoryURL = new URL((String) getAppCtx().getBean(ID_FACTORY_URL_NAME));
        idFactory = new HttpDatabaseIDFactory(idFactoryURL);
        try {
            idFactory.getNext();
        } catch(NoMoreIDsException ex) {
            // don't print the entire stacktrace, just the message
            LoggerAdapter.error(MessageKey.ERROR_DBFACTORY_FAILED_INIT.getLocalizedMessage(idFactoryURL, ex.getMessage()), this);
        }
    }

    protected void addDefaults(Message message) throws NoMoreIDsException
    {
        message.getHeader().setField(new MsgType(MsgType.ORDER_SINGLE));
        message.setField(new OrdType(OrdType.LIMIT));
        message.setField(new ClOrdID(idFactory.getNext()));
        message.setField(new HandlInst(HandlInst.MANUAL_ORDER));
        message.setField(new TransactTime());
    }

    protected void sendMessage(Message message) throws JMSException {
        if(LoggerAdapter.isDebugEnabled(this)) {
            LoggerAdapter.debug("Sending message: "+message, this);
        }
        jmsQueueSender.convertAndSend(message);
    }

    /** Prints the usage information for how to start the command and quits
     */
    protected static void usage()
    {
        System.out.println("Usage: java OrderLoader <CSV input file>");
        System.out.println("Example file format should be: Symbol,Side,OrderQty,Price,TimeInForce,Account");
        System.exit(1);
    }

    protected List<MessageBundleInfo> getLocalMessageBundles() {
        return new LinkedList<MessageBundleInfo>(Arrays.asList(OL_MESSAGE_BUNDLE_INFO));
    }

    /**
     * @param args
     * Incoming CSV file format:
     * Symbol,Side,OrderQty,Price,TimeInForce,Account
     * IBM,B,100,12.1,DAY,123-ASDF-234
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length < 1) {
            usage();
        }
        OrderLoader loader = new OrderLoader();
        loader.parseAndSendOrders(new FileInputStream(args[0]));
        loader.printReport();
        ((Service) loader.getAppCtx().getBean(POOLED_CONNECTION_FACTORY_NAME)).stop();
        loader.getAppCtx().stop();
        loader.getAppCtx().close();
    }


    @SuppressWarnings("unchecked")
    public void parseAndSendOrders(InputStream inputStream)
        throws Exception
    {
        CSVReader reader = new SimpleReader();

        List<String[]> allRows = reader.parse(inputStream);
        if(allRows.size() < 2) {
            System.out.println("Need more orders than just the header");
            System.exit(1);
        }
        Vector<Field> headerRow = getFieldOrder(allRows.get(0));
        jmsQueueSender = (JmsTemplate) getAppCtx().getBean(JMS_SENDER_NAME);


        String[] headerFields = allRows.get(0);
        for (int i=1;i<allRows.size(); i++) {
            String[] oneRow = allRows.get(i);
            sendOneOrder(headerRow, headerFields, oneRow);
        }
    }

    /** Prints the summary report of he send orders */
    private void printReport()
    {
        System.out.println(">>>OrderLoading report<<<<");
        System.out.println(">>> OrderLoader sent " +numProcessedOrders + " orders successfully");
        System.out.println(">>> There were " +numBlankLines + " blank lines");
        if(failedOrders.size() > 0) {
            System.out.println(">>> "+failedOrders.size() +" orders failed to parse:");
            for(String row : failedOrders ) {
                System.out.println("failed row: "+row);
            }
        }
    }

    protected void sendOneOrder(Vector<Field> inHeaderRow, String[] inHeaderNames, String[] inOrderRow)
        throws NoMoreIDsException
    {
        if(LoggerAdapter.isDebugEnabled(this)) {
            LoggerAdapter.debug("processing row "+Util.getStringFromArray(inOrderRow), this);
        }
        Message message = msgFactory.createNewMessage();
        // set defaults first b/c they may be overridden for MKT orders
        addDefaults(message);
        try {
            if(inHeaderRow.size() != inOrderRow.length) {
                if(inOrderRow.length == 0) {
                    numBlankLines++;
                    return;
                }else {
                    throw new OrderParsingException(OrderLoaderMessageKey.PARSING_WRONG_NUM_FIELDS.getLocalizedMessage());
                }
            } else if(inOrderRow[0].startsWith(COMMENT_MARKER)) {
                numComments++;
            } else {
                for(int i=0;i<inHeaderRow.size();i++)
                {
                    Field theField = inHeaderRow.get(i);
                    String value = parseMessageValue(theField, inHeaderNames[i], inOrderRow[i], message);
                    if(value!=null) {
                        message.setField(new StringField(theField.getField(), value));
                    }
                }

                //FIXDataDictionaryManager.getDictionary().validate(message, true);
                FIXDataDictionaryManager.getDictionary().validate(message);

                sendMessage(message);
                numProcessedOrders++;
            }
        } catch (Exception e) {
            LoggerAdapter.error(OrderLoaderMessageKey.PARSING_ORDER_GEN_ERROR.getLocalizedMessage(
                    Util.getStringFromArray(inOrderRow),e.getMessage()), this);
            failedOrders.add(Util.getStringFromArray(inOrderRow) + ": " + e.getMessage());
        }
    }

    /**
     * For some fields (day, side, etc) we do custom lookups since the orders may be "DAY", MKT (for price), etc
     * For all others, delegate to the basic field type lookup
     * @param inField  the field we are converting
     * @param inValue  string value
     * @return Translated data
     */
    protected String parseMessageValue(Field inField, String inFieldName, String inValue,
                                       Message inMessage)
        throws OrderParsingException
    {
        if(inField instanceof CustomField) {
            return ((CustomField) inField).parseMessageValue(inValue).toString();
        }

        switch(inField.getField()) {
            case Side.FIELD:
                return getSide(inValue).toString();
            case Price.FIELD:
                // price must be positive but can be MKT
                if(MKT_PRICE.equals(inValue)) {
                    inMessage.setField(new OrdType(OrdType.MARKET));
                    return null;
                } else {
                    BigDecimal price =  null;
                    try {
                        price = new BigDecimal(inValue);
                    } catch(NumberFormatException ex) {
                        throw new OrderParsingException(OrderLoaderMessageKey.PARSING_PRICE_VALID_NUM.getLocalizedMessage(inValue), ex);
                    }
                    if(price.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new OrderParsingException(OrderLoaderMessageKey.PARSING_PRICE_POSITIVE.getLocalizedMessage(price));
                    }
                    // just return the original string
                    return inValue;
                }
            case OrderQty.FIELD:
                // quantity must be a positive integer
                Integer qty= null;
                try {
                    qty = Integer.parseInt(inValue);
                } catch(NumberFormatException ex) {
                    throw new OrderParsingException(OrderLoaderMessageKey.PARSING_QTY_INT.getLocalizedMessage(inValue), ex);
                }
                if(qty <=0) {
                    throw new OrderParsingException(OrderLoaderMessageKey.PARSING_QTY_POS_INT.getLocalizedMessage(inValue));
                }
                // just return the original string
                return inValue;
            case TimeInForce.FIELD:
                try {
                    java.lang.reflect.Field theField = TimeInForce.class.getField(inValue);
                    return theField.get(null).toString();
                } catch (Exception ex) {
                    throw new OrderParsingException(inFieldName, inValue, ex);
                }
            default:
                return inValue;
        }
    }

    protected Character getSide(String inValue)
     {
         if(inValue != null) {
             inValue = inValue.toUpperCase();
         }
         if("".equals(inValue)) {
             return Side.UNDISCLOSED;
         }
         if("B".equals(inValue)) {
             return Side.BUY;
         }
         if("S".equals(inValue)) {
             return Side.SELL;
         }
         if("SS".equals(inValue)) {
             return Side.SELL_SHORT;
         }
         if("SSE".equals(inValue)) {
             return Side.SELL_SHORT_EXEMPT;
         }
         return Side.UNDISCLOSED;
     }

    // parses a row of input to return an array of fields
    protected Vector<Field> getFieldOrder(String[] inFirstRow)
        throws OrderParsingException
    {
        Vector<Field> result = new Vector<Field>(inFirstRow.length);
        for(String field : inFirstRow) {
            result.add(getQuickFixFieldFromName(field));
        }
        return result;
    }

    /** Translate the incoming field name from String to a FIX standard
     * using reflection. the quickfix.field package has all of these defined
     * as quickfix.field.<Name> so we just need to create a class for each english string
     *
     * If the field is not found, it could be a "undertermined" field in which case we check
     * to see if it parses out to an integer. If it does, we store the int as the field value
     * Otherwise, we throw an error.
     *
     * @param fieldName
     * @return quickfix object of that type
     * @throws OrderParsingException
     */
    protected Field getQuickFixFieldFromName(String fieldName)
        throws OrderParsingException
    {
        Field theField = null;
        try {
            theField = (Field) Class.forName("quickfix.field."+fieldName).newInstance();
        } catch(ClassNotFoundException ex) {
            // check to see if this is non-predetermined value (just an int header)
            return CustomField.getCustomField(fieldName);
            //throw new OrderParsingException(fieldName, ex);
        }catch(Exception ex) {
            throw new OrderParsingException(fieldName, ex);
        }
        return theField;
    }

    /** Returns the values of the number of transactions processed */
    public int getNumProcessedOrders()
    {
        return numProcessedOrders;
    }

    public Vector<String> getFailedOrders()
    {
        return failedOrders;
    }
}
