package org.marketcetera.ors;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.unicode.UnicodeFileReader;
import org.marketcetera.util.file.CloseableRegistry;
import org.springframework.beans.factory.InitializingBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import com.google.common.collect.*;

/* $License$ */
/**
 * A class that provides a mapping between option roots and underlying
 * symbols based on the
 * <a href="http://www.optionsclearing.com/market/listed_products/default.jsp">
 * mapping</a> provided by the Options Clearing Corporation.
 * <p>
 * This class can be safely used concurrently from multiple threads.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class OptionRootUnderlyingMap implements InitializingBean {
    /**
     * Creates an instance.
     */
    public OptionRootUnderlyingMap() {
        sInstance = this;
    }

    /**
     * Gets the underlying symbol, given the option root symbol.
     *
     * @param inOptionRoot the option root symbol.
     *
     * @return the underlying symbol, if a mapping is found. null otherwise.
     */
    public String getUnderlying(String inOptionRoot) {
        if(inOptionRoot == null) {
            return null;
        }
        final Map<String, String> map = mRootToUnderlying;
        return map == null ? null : map.get(inOptionRoot);
    }

    /**
     * Returns the collection of option roots for the underlying symbol.
     *
     * @param inUnderlying the underlying symbol.
     *
     * @return the sorted collection of option roots for the underlying symbol.
     * If no mapping is found, a null value is returned. The returned collection
     * is not modifiable.
     */
    public Collection<String> getOptionRoots(String inUnderlying) {
        if(inUnderlying == null) {
            return null;
        }
        final Map<String, Collection<String>> map = mUnderlyingToRoots;
        return map == null ? null : map.get(inUnderlying);
    }

    /**
     * The name of the file from which the mappings should be read.
     *
     * @param inFilename the file name.
     */
    public void setFilename(String inFilename) {
        mFilename = inFilename;
    }

    /**
     * Sets the type of records to include.
     *
     * @param inIncludeTypes the type of records to include. Should not be null.
     */
    public void setIncludeTypes(String[] inIncludeTypes) {
        if(inIncludeTypes == null) {
            throw new NullPointerException();
        }
        mIncludeTypes = inIncludeTypes;
    }


    /**
     * Returns the singleton instance if initialized, null otherwise.
     *
     * @return the singleton instance.
     */
    public static OptionRootUnderlyingMap getInstance() {
        return sInstance;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        loadFromFile();
    }

    /**
     * Returns the number of option root mappings.
     * <p>
     * This method provided to help with unit testing purposes.
     *
     * @return number of option root mappings.
     */
    int getNumOptionRoots() {
        return mRootToUnderlying == null? 0: mRootToUnderlying.size();
    }

    /**
     * The number of underlying mappings.
     * <p>
     * This method provided to help with unit testing purposes.
     *
     * @return number of underlying mappings.
     */
    int getNumUnderlyings() {
        return mUnderlyingToRoots == null? 0: mUnderlyingToRoots.size();
    }

    /**
     * Loads the records from the file.
     */
    private void loadFromFile() {
        final String filename = mFilename;
        if(filename != null) {
            BufferedReader reader;
            Map<String,String> rootToUnderlying = null;
            SortedSetMultimap<String,String> underlyingToRoots = null;
            CloseableRegistry registry = new CloseableRegistry();
            try {
                reader = new BufferedReader(new UnicodeFileReader(filename));
                registry.register(reader);
                Set<String> includeTypes = new HashSet<String>(Arrays.asList(mIncludeTypes));
                String line, root, underlying, type;
                rootToUnderlying = new HashMap<String, String>();
                underlyingToRoots = TreeMultimap.create();
                while((line = reader.readLine()) != null) {
                    root = extract(line, ROOT_START_IDX, ROOT_END_IDX);
                    underlying = extract(line, UNDERLYING_START_IDX, UNDERLYING_END_IDX);
                    type = extract(line, TYPE_START_IDX, TYPE_END_IDX);
                    if(root != null && underlying != null && type != null && includeTypes.contains(type)) {
                        rootToUnderlying.put(root, underlying);
                        underlyingToRoots.put(underlying,  root);
                    }
                }
            } catch (IOException e) {
                Messages.ORUM_LOG_ERROR_LOADING_FILE.error(this, e, filename);
            } finally {
                registry.close();
            }
            //Assign the values to volatile variables after the maps have been
            //initialized to prevent concurrency issues.
            mRootToUnderlying = rootToUnderlying == null
                    ? null
                    : Collections.unmodifiableMap(rootToUnderlying);
            mUnderlyingToRoots = underlyingToRoots == null
                    ? null
                    : Multimaps.unmodifiableSortedSetMultimap(underlyingToRoots).asMap();
        } else {
            Messages.ORUM_LOG_SKIP_LOAD_FILE.info(this);
        }
    }

    /**
     * Extracts the field value from the supplied record.
     *
     * @param inValue the current record to extract the field from
     * @param inStartIdx the start index of the field
     * @param inEndIdx the end index of the field
     *
     * @return the field value, if the field was found in the record.
     */
    private String extract(String inValue, int inStartIdx, int inEndIdx) {
        if(inStartIdx >= inValue.length()) {
            return null;
        }
        inEndIdx = Math.min(inEndIdx, inValue.length());
        if(inEndIdx <= inStartIdx) {
            return null;
        }
        return inValue.substring(inStartIdx, inEndIdx).trim();
    }

    private volatile Map<String,String> mRootToUnderlying;
    private volatile Map<String,Collection<String>> mUnderlyingToRoots;
    private volatile String[] mIncludeTypes = {"EU", "EL"};   //$NON-NLS-1$ $NON-NLS-2$
    private volatile String mFilename;
    private static volatile OptionRootUnderlyingMap sInstance;
    private static final int ROOT_START_IDX = 0;
    private static final int ROOT_END_IDX = 6;
    private static final int UNDERLYING_START_IDX = 7;
    private static final int UNDERLYING_END_IDX = 13;
    private static final int TYPE_START_IDX = 80;
    private static final int TYPE_END_IDX = 82;
}
