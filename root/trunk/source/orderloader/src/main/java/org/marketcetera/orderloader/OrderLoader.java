package org.marketcetera.orderloader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.jms.JMSException;

import org.apache.activemq.Service;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import org.marketcetera.core.ApplicationBase;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.HttpDatabaseIDFactory;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.MessageBundleInfo;
import org.marketcetera.core.MessageKey;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.util.auth.StandardAuthentication;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.spring.SpringUtils;
import org.marketcetera.util.unicode.UnicodeInputStreamReader;
import org.marketcetera.util.unicode.DecodingStrategy;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.jms.core.JmsTemplate;

import quickfix.Field;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.ClOrdID;
import quickfix.field.HandlInst;
import quickfix.field.MsgType;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;

/* $License$ */

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
 * @since 0.5.0
 * $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OrderLoader 
    extends ApplicationBase
    implements Messages
{
    private static final String JMS_SENDER_NAME = "outgoingJmsTemplate"; //$NON-NLS-1$
    private static final String ID_FACTORY_URL_NAME = "idFactoryURL"; //$NON-NLS-1$
    private static final String POOLED_CONNECTION_FACTORY_NAME = "pooledConnectionFactory"; //$NON-NLS-1$

    private static final String CFG_BASE_FILE_NAME=
        "file:"+CONF_DIR+"orderloader_base.xml"; //$NON-NLS-1$ //$NON-NLS-2$

    private static StandardAuthentication authentication;

    protected static String MKT_PRICE = "MKT"; //$NON-NLS-1$
    protected static String TIME_LIMIT_DAY = "DAY"; //$NON-NLS-1$
    public static final String CFG_FILE_NAME = "orderloader.xml"; //$NON-NLS-1$

    private IDFactory idFactory;
    private JmsTemplate jmsQueueSender;

    protected int numProcessedOrders;
    protected int numBlankLines;
    protected int numComments;
    protected Vector<String> failedOrders;
    public static final String COMMENT_MARKER = "#"; //$NON-NLS-1$

    public OrderLoader
        (String username,
         String password) throws Exception
    {
        StaticApplicationContext parentContext=
            new StaticApplicationContext
            (new FileSystemXmlApplicationContext(CFG_BASE_FILE_NAME));
        SpringUtils.addStringBean(parentContext,USERNAME_BEAN_NAME,username);
        SpringUtils.addStringBean(parentContext,PASSWORD_BEAN_NAME,password);
        parentContext.refresh();

        numProcessedOrders = numComments = numBlankLines = 0;
        failedOrders = new Vector<String>();
        createApplicationContext
            (new String[] {getConfigName()},parentContext,true);
        URL idFactoryURL = new URL((String) getAppCtx().getBean(ID_FACTORY_URL_NAME));
        idFactory = new HttpDatabaseIDFactory(idFactoryURL);
        try {
            idFactory.getNext();
        } catch(NoMoreIDsException ex) {
            // don't print the entire stacktrace, just the message
            LoggerAdapter.warn(MessageKey.ERROR_DBFACTORY_FAILED_INIT.getLocalizedMessage(idFactoryURL, ex.getMessage()), this);
        }
    }

    protected void addDefaults(Message message) throws NoMoreIDsException
    {
        message.getHeader().setField(new MsgType(MsgType.ORDER_SINGLE));
        message.setField(new OrdType(OrdType.LIMIT));
        message.setField(new ClOrdID(idFactory.getNext()));
        message.setField(new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE));
        message.setField(new TransactTime()); //i18n_datetime
    }

    protected void sendMessage(Message message) throws JMSException {
        SLF4JLoggerProxy.debug(this,
                               "Sending message: {}", //$NON-NLS-1$
                               message);
        jmsQueueSender.convertAndSend(message);
    }

    /** Prints the usage information for how to start the command and quits
     */
    protected static void usage()
    {
        System.err.println(ERROR_USAGE.getText(OrderLoader.class.getName()));
        System.err.println(ERROR_EXAMPLE.getText());
        System.err.println(ERROR_AUTHENTICATION.getText());
        System.err.println();
        authentication.printUsage(System.err);
        System.exit(1);
    }

    protected List<MessageBundleInfo> getLocalMessageBundles() 
    {
        return new LinkedList<MessageBundleInfo>();
    }

    /**
     * @param args
     * Incoming CSV file format:
     * Symbol,Side,OrderQty,Price,TimeInForce,Account
     * IBM,B,100,12.1,DAY,123-ASDF-234
     */
    public static void main(String[] args) throws Exception
    {
        authentication=new StandardAuthentication(CFG_BASE_FILE_NAME,args);
        if (!authentication.setValues()) {
            usage();
        }

        args=authentication.getOtherArgs();
        if (args.length<1) {
            System.err.println(ERROR_MISSING_FILE.getText());
            usage();
        }
        if (args.length>1) {
            System.err.println(ERROR_TOO_MANY_ARGUMENTS.getText());
            usage();
        }
        String file=args[0];

        OrderLoader loader = new OrderLoader
            (authentication.getUser(),authentication.getPasswordAsString());
        loader.parseAndSendOrders(new FileInputStream(file));
        loader.printReport();
        ((Service) loader.getAppCtx().getBean(POOLED_CONNECTION_FACTORY_NAME)).stop();
        loader.getAppCtx().stop();
        loader.getAppCtx().close();
    }

    @SuppressWarnings("unchecked") //$NON-NLS-1$
    public void parseAndSendOrders(InputStream inputStream)
        throws Exception
    {
        String[][] rows = new CSVParser(new UnicodeInputStreamReader(
                inputStream, DecodingStrategy.SIG_REQ),
                CSVStrategy.EXCEL_STRATEGY).getAllValues();

        if(rows.length < 2) {
            System.err.println(ERROR_NO_ORDERS.getText());
            System.exit(1);
        }
        jmsQueueSender = (JmsTemplate) getAppCtx().getBean(JMS_SENDER_NAME);
        Vector<Field<?>> headerRow = null;
        String[] headerFields = null;
        for(String[] row : rows) {
            if(headerRow == null) {
                headerRow = getFieldOrder(row);
                headerFields = row;
            } else {
                sendOneOrder(headerRow, 
                             headerFields, 
                             row);
            }
        }
    }

    /** Prints the summary report of he send orders */
    private void printReport()
    {
        System.out.println(REPORT_SUMMARY.getText());
        System.out.println(REPORT_PROCESSED_LINES.getText(numProcessedOrders));
        System.out.println(REPORT_BLANK_LINES.getText(numBlankLines));
        if(!failedOrders.isEmpty()) {
            System.err.println(FAILED_MESSAGES.getText(failedOrders.size()));
            for(String row : failedOrders ) {
                System.err.println(row);
            }
        }
    }

    protected void sendOneOrder(Vector<Field<?>> inHeaderRow, String[] inHeaderNames, String[] inOrderRow)
        throws NoMoreIDsException
    {
        SLF4JLoggerProxy.debug(this,
                               "processing row {}", //$NON-NLS-1$
                               Arrays.toString(inOrderRow));
        Message message = msgFactory.newBasicOrder();
        // set defaults first b/c they may be overridden for MKT orders
        addDefaults(message);
        try {
            if(inHeaderRow.size() != inOrderRow.length) {
                //Blank lines might appear as a row with a single empty record
                if(inOrderRow.length == 0 ||
                        (inOrderRow.length == 1 && inOrderRow[0].trim().length() == 0)) {
                    numBlankLines++;
                    return;
                } else {
                    throw new OrderParsingException(PARSING_WRONG_NUM_FIELDS.getText());
                }
            } else if(inOrderRow[0].startsWith(COMMENT_MARKER)) {
                numComments++;
            } else {
                for(int i=0;i<inHeaderRow.size();i++)
                {
                    Field<?> theField = inHeaderRow.get(i);
                    String value = parseMessageValue(theField, inHeaderNames[i], inOrderRow[i], message);
                    if(value!=null) {
                        int fieldID = theField.getField();
                        if(fixDD.getDictionary().isMsgField(MsgType.ORDER_SINGLE, fieldID)) {
                            message.setField(new StringField(fieldID, value));
                        } else if(fixDD.getDictionary().isHeaderField(fieldID)) {
                            message.getHeader().setField(new StringField(fieldID, value));
                        } else if(fixDD.getDictionary().isTrailerField(fieldID)) {
                            message.getTrailer().setField(new StringField(fieldID, value));
                        } else {
                            // Format the fieldID so it doesn't get localized to 2,345 for example
                            NumberFormat formatter = NumberFormat.getIntegerInstance();
                            formatter.setGroupingUsed(false);
                            throw new MarketceteraException(PARSING_FIELD_NOT_IN_DICT.getText(formatter.format(fieldID), 
                                                                                                                                value));
                        }
                    }
                }

                fixDD.getDictionary().validate(message, true);

                sendMessage(message);
                numProcessedOrders++;
            }
        } catch (Exception e) {
            PARSING_ORDER_GEN_ERROR.error(this,
                                          e,
                                          Arrays.toString(inOrderRow),
                                          e.getMessage());
            SLF4JLoggerProxy.debug(this,
                                   e.getMessage(),
                                   e);
            failedOrders.add(new StringBuilder().append(Arrays.toString(inOrderRow)).append(": ").append(e.getMessage()).toString()); //$NON-NLS-1$
        }
    }

    /**
     * For some fields (day, side, etc) we do custom lookups since the orders may be "DAY", MKT (for price), etc
     * For all others, delegate to the basic field type lookup
     * @param inField  the field we are converting
     * @param inValue  string value
     * @return Translated data
     */
    protected String parseMessageValue(Field<?> inField, 
                                       String inFieldName, 
                                       String inValue,
                                       Message inMessage)
        throws OrderParsingException
    {
        if(inField instanceof CustomField) {
            return ((CustomField)inField).parseMessageValue(inValue).toString(); //i18n_number? BigDecimal.toString() might not give the right value
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
                        throw new OrderParsingException(PARSING_PRICE_VALID_NUM.getText(inValue), ex);
                    }
                    if(price.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new OrderParsingException(PARSING_PRICE_POSITIVE.getText(price));
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
                    throw new OrderParsingException(PARSING_QTY_INT.getText(inValue), ex);
                }
                if(qty <=0) {
                    throw new OrderParsingException(PARSING_QTY_POS_INT.getText(inValue));
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

    protected char getSide(String inValue)
     {
         if(inValue != null) {
             inValue = inValue.toUpperCase();
         }
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
         return Side.UNDISCLOSED;
     }
    /**
     * Parses a row of input to return an array of fields.
     *
     * @param inFirstRow a <code>String[]</code> value containing the values to interpret as fields
     * @return a <code>Vector&lt;Field&lt;&gt;&gt;</code> value or null if the passed values should not be interpreted as fields
     * @throws OrderParsingException if an error occurs while parsing the passed values
     */
    protected Vector<Field<?>> getFieldOrder(String[] inFirstRow)
        throws OrderParsingException
    {
        if(inFirstRow.length > 0 &&
           inFirstRow[0].startsWith(COMMENT_MARKER)) {
            return null;
        }
        Vector<Field<?>> result = new Vector<Field<?>>(inFirstRow.length);
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
    protected Field<?> getQuickFixFieldFromName(String fieldName)
        throws OrderParsingException
    {
        Field<?> theField = null;
        try {
            theField = (Field<?>) Class.forName("quickfix.field."+fieldName).newInstance(); //$NON-NLS-1$
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

    protected String getConfigName()    { return CFG_FILE_NAME; }
}
