package org.marketcetera.orderloader;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.orderloader.system.SystemProcessor;
import org.marketcetera.orderloader.fix.FIXProcessor;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.BrokerID;

import java.util.Set;
import java.util.EnumSet;
import java.util.List;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;

/* $License$ */
/**
 * An order loader that reads orders from a supplied csv input file
 * and processes them. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class OrderLoader {
    /**
     * Creates a new instance that processes orders from a csv input file.
     *
     * @param inMode the mode. Can be <code>sys</code> or a FIX version value.
     * If null, defaults to <code>sys</code>.
     * @param inBrokerID the broker's ID to which the orders should be sent.
     * Can be null, if the mode is <code>sys</code>.
     * @param inOrderProcessor the processor that should process all the orders
     * parsed out by the order loader. Cannot be null.
     * @param inFile the csv file that contains orders that need to be parsed.
     * Cannot be null. 
     *
     * @throws OrderParsingException if there were errors
     * @throws java.io.IOException if there were errors reading data from the
     * supplied file.
     */
    public OrderLoader(String inMode,
                       BrokerID inBrokerID,
                       OrderProcessor inOrderProcessor,
                       File inFile)
            throws OrderParsingException, IOException {
        if(inOrderProcessor == null) {
            throw new NullPointerException();
        }
        if(inFile == null) {
            throw new NullPointerException();
        }
        if(inMode == null || inMode.equals(MODE_SYSTEM)) {
            mRowProcessor = new SystemProcessor(inOrderProcessor, inBrokerID);
        } else {
            Set<FIXVersion> supportedValues = EnumSet.allOf(FIXVersion.class);
            supportedValues.remove(FIXVersion.FIX_SYSTEM);
            FIXVersion fixVersion;
            try {
                fixVersion = FIXVersion.getFIXVersion(inMode);
            } catch (IllegalArgumentException e) {
                throw new OrderParsingException(e, new I18NBoundMessage2P(
                        Messages.INVALID_FIX_VERSION, inMode,
                        supportedValues.toString()));
            }
            if(!supportedValues.contains(fixVersion)) {
                throw new OrderParsingException(new I18NBoundMessage2P(
                        Messages.INVALID_FIX_VERSION, inMode,
                        supportedValues.toString()));
            }
            mRowProcessor = new FIXProcessor(inOrderProcessor,
                    inBrokerID, fixVersion);
        }
        mParser = new OrderParser(mRowProcessor);
        mParser.parseOrders(new FileInputStream(inFile));
    }

    /**
     * Number of lines of input processed.
     *
     * @return number of lines processed.
     */
    public int getNumLines() {
        return mParser.getNumLines();
    }

    /**
     * Number of blank lines.
     *
     * @return number of blank lines.
     */
    public int getNumBlankLines() {
        return mParser.getNumBlankLines();
    }

    /**
     * Number of lines with comments.
     *
     * @return number of lines with comments.
     */
    public int getNumComments() {
        return mParser.getNumComments();
    }

    /**
     * Number of orders successfully processed.
     *
     * @return number of orders processed.
     */
    public int getNumSuccess() {
        return mRowProcessor.getNumSuccess();
    }

    /**
     * Number of orders that failed to process.
     *
     * @return number of orders failed.
     */
    public int getNumFailed() {
        return mRowProcessor.getNumFailed();
    }

    /**
     * Details on failed orders.
     *
     * @return details on failed orders.
     */
    public List<FailedOrderInfo> getFailedOrders() {
        return mRowProcessor.getFailedOrders();
    }

    private final RowProcessor mRowProcessor;
    private final OrderParser mParser;
    public static final String MODE_SYSTEM = "sys";  //$NON-NLS-1$
}
