package org.marketcetera.modules.csv;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.unicode.UnicodeInputStreamReader;
import org.marketcetera.util.unicode.DecodingStrategy;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.module.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;

/* $License$ */
/**
 * A module that emits data contained in a CSV file as a series of maps,
 * each instance containing a row of data. The first row of data is
 * assumed to contain the column names. Each Map instance has the
 * column name as the key and the value in the column as data.
 * Each map instance has as many entries as the number of header columns.
 * Rows that do not have any value for a column have an empty string
 * value in the emitted map.
 * <p>
 * All the keys and values in the map are of type string.
 * <p>
 * The module accepts request parameters of following types:
 * <ul>
 *      <li><b>{@link String}</b> : interpreted as a URL, if that fails, a
 *      file path from which the csv file can be read. If the string starts
 *      with 'r:', it's stripped from the string and rows are emitted in
 *      the reverse order of their appearance.</li>
 *      <li>{@link File}: path to the csv file.</li>
 *      <li>{@link URL}: the url of the csv file.</li>
 * </ul>
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class CSVEmitter extends Module implements DataEmitter {
    /**
     * Creates an instance.
     */
    protected CSVEmitter() {
        super(CSVEmitterFactory.INSTANCE_URN, true);
    }

    @Override
    protected void preStart() {
        mService = Executors.newCachedThreadPool();
    }

    @Override
    protected void preStop() {
        mService.shutdownNow();
    }

    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws UnsupportedRequestParameterType,
            IllegalRequestParameterValue {
        Object obj = inRequest.getData();
        if(obj == null) {
            throw new IllegalRequestParameterValue(getURN(), null);
        }
        URL csv = null;
        boolean isReverse = false;
        try {
            if(obj instanceof String) {
                String s = (String)obj;
                if(s.startsWith(PREFIX_REVERSE)) {
                    isReverse = true;
                    s = s.substring(PREFIX_REVERSE.length());
                }
                try {
                    csv = new URL(s);
                } catch(MalformedURLException ignore) {
                    csv = new File(s).toURI().toURL();
                }
            } else if (obj instanceof File) {
                csv = ((File)obj).toURI().toURL();
            } else if (obj instanceof URL) {
                csv = (URL) obj;
            } else {
                throw new UnsupportedRequestParameterType(getURN(), obj);
            }
            CSVReader reader = new CSVReader(csv, inSupport, isReverse);
            Future<Boolean> future = mService.submit(reader);
            mRequests.put(inSupport.getRequestID(), future);
        } catch (MalformedURLException e) {
            throw new IllegalRequestParameterValue(getURN(), obj, e);
        }

    }

    @Override
    public void cancel(DataFlowID inFlowID, RequestID inRequestID) {
        Future<Boolean> future = mRequests.get(inRequestID);
        if (future != null) {
            future.cancel(true);
        }
    }

    /**
     * This task reads the contents of a csv file and emits each row
     * of the csv file as a Map instance.
     */
    private static class CSVReader implements Callable<Boolean> {
        /**
         * Creates an instance.
         *
         * @param inSource The URL from which the csv file can be read.
         * @param inSupport the handle to emit data.
         * @param inReverse if the emitter should reverse the rows, emitting
         * the last row first.
         */
        private CSVReader(URL inSource, DataEmitterSupport inSupport,
                          boolean inReverse) {
            mSource = inSource;
            mSupport = inSupport;
            mReverse = inReverse;
        }

        @Override
        public Boolean call() throws Exception {
            InputStream is = null;
            try {
                is = mSource.openStream();
                String[][] rows = new CSVParser(new UnicodeInputStreamReader(is,
                        DecodingStrategy.SIG_REQ),
                        CSVStrategy.EXCEL_STRATEGY).getAllValues();
                //Expect at least two rows, the first row is headers.
                if(rows == null || rows.length < 2) {
                    mSupport.dataEmitError(new I18NBoundMessage1P(
                            Messages.INSUFFICIENT_DATA,
                            rows == null
                                    ? 0
                                    : rows.length),
                            true);
                    return false;
                }
                if (mReverse) {
                    for(int i = rows.length - 1; i > 0; i--) {
                        mSupport.send(createMap(rows[0], rows[i]));
                    }
                } else {
                    for(int i = 1; i < rows.length; i++) {
                        mSupport.send(createMap(rows[0], rows[i]));
                    }
                }
                //Terminate the data flow.
                mSupport.dataEmitError(Messages.NO_MORE_DATA,true);
            } catch (Throwable e) {
                //Terminate the data flow if there's any error.
                mSupport.dataEmitError(new I18NBoundMessage1P(
                        Messages.UNEXPECTED_ERROR, e.getLocalizedMessage()),
                        true);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ignore) {
                    }
                }
            }
            return true;
        }

        /**
         * Creates a map instance with the supplied key value arrays.
         *
         * @param inKeys The keys to be used in the map.
         * @param inValues The values to be used in the map.
         *
         * @return A map instance containing key value pairs extracted
         * from the supplied array. The map's size is the same size as
         * the length of the supplied inKeys array.
         */
        private Map createMap(String[] inKeys, String[] inValues) {
            HashMap<String, String> map = new HashMap<String, String>();
            for(int i = 0; i < inKeys.length; i++) {
                map.put(inKeys[i], i < inValues.length? inValues[i]: "");  //$NON-NLS-1$
            }
            return map;
        }

        private final URL mSource;
        private final boolean mReverse;
        private final DataEmitterSupport mSupport;
    }
    private ExecutorService mService;
    private final Map<RequestID, Future<Boolean>> mRequests =
            new Hashtable<RequestID, Future<Boolean>>();
    static final String PREFIX_REVERSE = "r:";  //$NON-NLS-1$
}
