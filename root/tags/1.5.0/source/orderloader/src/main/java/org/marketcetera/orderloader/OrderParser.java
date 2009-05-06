package org.marketcetera.orderloader;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import org.marketcetera.util.unicode.UnicodeInputStreamReader;
import org.marketcetera.util.unicode.DecodingStrategy;
import org.marketcetera.util.misc.ClassVersion;
import static org.marketcetera.orderloader.Messages.*;


/* $License$ */
/**
 * Processes a CSV file and generates trade objects from it.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class OrderParser {

    /**
     * Creates a parser instance that parses the orders and processes
     * them using the supplied delegate.
     *
     * @param inProcessor the delegate to use for creating orders from
     * each row and processing them. Cannot be null
     */
    public OrderParser(RowProcessor inProcessor) {
        if(inProcessor == null) {
            throw new NullPointerException();
        }
        mProcessor = inProcessor;
    }

    /**
     * Returns the total number of lines processed.
     *
     * @return the total number of lines processed.
     */
    public int getNumLines() {
        return mNumLines;
    }

    /**
     * Returns the total number of blank lines.
     *
     * @return the total number of blank lines.
     */
    public int getNumBlankLines() {
        return mNumBlankLines;
    }

    /**
     * Returns the total number of lines with comments.
     *
     * @return the total number of lines with comments.
     */
    public int getNumComments() {
        return mNumComments;
    }

    /**
     * Parses rows out of the supplied file and uses the processors to
     * process them.
     *
     * @param inStream the input stream with csv input containing orders.
     * The stream is closed when this method returns.
     *
     * @throws IOException if there was an error opening the supplied file or
     * if the file had no orders to send.
     * @throws OrderParsingException if the file didn't have the
     * column headers specified correctly or if the file didn't have any orders.
     */
    public void parseOrders(InputStream inStream)
            throws IOException, OrderParsingException {
        UnicodeInputStreamReader reader = null;
        try {
            reader = new UnicodeInputStreamReader(
                    inStream, DecodingStrategy.SIG_REQ);
            String[][] rows = new CSVParser(reader,
                    CSVStrategy.EXCEL_STRATEGY).getAllValues();

            boolean isProcessorInit = false;
            if (rows != null) {
                for(String[] row : rows) {
                    mNumLines++;
                    //Ignore empty lines.
                    if(row.length == 0 || row.length == 1 &&
                            row[0].trim().isEmpty()) {
                        mNumBlankLines++;
                    } else if (row[0].startsWith(COMMENT_MARKER)) {
                        mNumComments++;
                    } else {
                        if(isProcessorInit) {
                            getProcessor().processOrder(mNumLines, row);
                        } else {
                            getProcessor().initialize(row);
                            isProcessorInit = true;
                        }
                    }
                }
            }
            if(getProcessor().getTotal() < 1) {
                throw new OrderParsingException(ERROR_NO_ORDERS);
            }
        } finally {
            if(reader != null) {
                reader.close();
            }
            if(inStream != null) {
                inStream.close();
            }
        }
    }

    /**
     * Returns the processor being used for processing rows.
     *
     * @return the processor being used for processing rows.
     */
    RowProcessor getProcessor() {
        return mProcessor;
    }
    public static final String COMMENT_MARKER = "#"; //$NON-NLS-1$
    private int mNumLines = 0;
    private int mNumBlankLines = 0;
    private int mNumComments = 0;
    private final RowProcessor mProcessor;


}