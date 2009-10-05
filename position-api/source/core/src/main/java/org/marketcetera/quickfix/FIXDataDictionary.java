package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import quickfix.ConfigError;
import quickfix.DataDictionary;

import java.io.InputStream;

/**
 * Converts the standard FIX field (integers) to their english names
 * This is mostly for better output/debugging purposes since we don't
 * want to memorize field numbers
 *
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXDataDictionary {
    public static final String FIX_SYSTEM_BEGIN_STRING = "FIX.0.0"; //$NON-NLS-1$
    public static final String FIX_4_0_BEGIN_STRING = "FIX.4.0"; //$NON-NLS-1$
    public static final String FIX_4_1_BEGIN_STRING = "FIX.4.1"; //$NON-NLS-1$
    public static final String FIX_4_2_BEGIN_STRING = "FIX.4.2"; //$NON-NLS-1$
    public static final String FIX_4_3_BEGIN_STRING = "FIX.4.3"; //$NON-NLS-1$
    public static final String FIX_4_4_BEGIN_STRING = "FIX.4.4"; //$NON-NLS-1$

    private final DataDictionary mDictionary;

    public FIXDataDictionary(DataDictionary dictionary)
    {
        mDictionary=dictionary;
    }

    /**
     * Load a {@link DataDictionary} from the specified resource
     *
     * @param fixDataDictionaryPath  Path to the location of the data dictionary file
     * @throws FIXFieldConverterNotAvailable
     */
    public FIXDataDictionary(String fixDataDictionaryPath) throws FIXFieldConverterNotAvailable
    {
        DataDictionary theDict;
        try {
            theDict = new DataDictionary(fixDataDictionaryPath);
        } catch (DataDictionary.Exception ddex) {
            InputStream input = FIXDataDictionary.class.getClassLoader().getResourceAsStream(fixDataDictionaryPath);
            try {
                theDict = new DataDictionary(input);
            } catch (ConfigError configError1) {
                throw new FIXFieldConverterNotAvailable(ddex, Messages.ERROR_COULD_NOT_CREATE_FIX_DATA_DICTIONARY);
            }
        } catch (ConfigError configError) {
            throw new FIXFieldConverterNotAvailable(configError, Messages.ERROR_COULD_NOT_CREATE_FIX_DATA_DICTIONARY);
        }

        mDictionary = theDict;
    }

    public String getHumanFieldName(int fieldNumber)
    {
        return mDictionary.getFieldName(fieldNumber);
    }

    /** Send in the field number and field value you want to translate
     * Example: Side.FIELD and Side.BUY results in "BUY"
     * Replaces all the _ with a space
     * @param fieldNumber
     * @param value
     * @return human-readable conversion of a FIX constant, or NULL if the value was not found
     */
    public String getHumanFieldValue(int fieldNumber, String value)
    {
        String result = mDictionary.getValueName(fieldNumber, value);
        return (result == null) ? result : result.replace('_', ' ');
    }

    public DataDictionary getDictionary() {
        return mDictionary;
    }

    /**
     * Set the default version of FIX to use in the rest of the methods on this class
     * @param fixDataDictionaryPath  Path to the location of the data dictionary file
     * @throws FIXFieldConverterNotAvailable
     */
    public static FIXDataDictionary initializeDataDictionary(String fixDataDictionaryPath) throws CoreException
    {
        return new FIXDataDictionary(fixDataDictionaryPath);
    }
}
