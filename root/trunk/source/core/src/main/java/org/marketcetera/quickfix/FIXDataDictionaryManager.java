package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
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
@ClassVersion("$Id$")
public class FIXDataDictionaryManager {
    public static final String FIX_4_0_BEGIN_STRING = "FIX.4.0";
    public static final String FIX_4_1_BEGIN_STRING = "FIX.4.1";
    public static final String FIX_4_2_BEGIN_STRING = "FIX.4.2";
    public static final String FIX_4_3_BEGIN_STRING = "FIX.4.3";
    public static final String FIX_4_4_BEGIN_STRING = "FIX.4.4";

    private static DataDictionary sCurrent;

    public static String getHumanFieldName(int fieldNumber)
    {
        return sCurrent.getFieldName(fieldNumber);
    }

    /** Send in the field number and field value you want to translate
     * Example: Side.FIELD and Side.BUY results in "BUY"
     * Replaces all the _ with a space
     * @param fieldNumber
     * @param value
     * @return human-readable conversion of a FIX constant, or NULL if the value was not found
     */
    public static String getHumanFieldValue(int fieldNumber, String value)
    {
        String result = sCurrent.getValueName(fieldNumber, value);
        return (result == null) ? result : result.replace('_', ' ');
    }

    public static DataDictionary getDictionary() {
        return sCurrent;
    }

    public static DataDictionary getDataDictionary(FIXVersion version) throws FIXFieldConverterNotAvailable {
        return loadDictionary(version.getDataDictionaryURL(), false);
    }

    /**
     * Set the default version of FIX to use in the rest of the methods on this class
     * @param fixDataDictionaryPath  Path to the location of the data dictionary file
     * @throws FIXFieldConverterNotAvailable
     */
    public static void setDataDictionary(String fixDataDictionaryPath) throws FIXFieldConverterNotAvailable
    {
        loadDictionary(fixDataDictionaryPath, true);
    }

        /**
     * Load a DataDictionary from the specified resource, optionally making it the default
     * dictionary.
     *
     * @param fixDataDictionaryPath  Path to the location of the data dictionary file
     * @param makeDefault if true make the version of FIX specified in the data dictionary the default version
     * @throws FIXFieldConverterNotAvailable
     */
    public static DataDictionary loadDictionary(String fixDataDictionaryPath, boolean makeDefault)
            throws  FIXFieldConverterNotAvailable
    {
        DataDictionary theDict;
        try {
            theDict = new DataDictionary(fixDataDictionaryPath);
        } catch (DataDictionary.Exception ddex) {
            InputStream input = FIXDataDictionaryManager.class.getClassLoader().getResourceAsStream(fixDataDictionaryPath);
            try {
                theDict = new DataDictionary(input);
            } catch (ConfigError configError1) {
                throw new FIXFieldConverterNotAvailable(ddex.getMessage(), ddex);
            }
        } catch (ConfigError configError) {
            throw new FIXFieldConverterNotAvailable(configError.getMessage(), configError);
        }

        if (makeDefault){
            sCurrent = theDict;
        }
        return theDict;
   }

    public static boolean isAdminMessageType42(String msgType) {
        if (msgType.length() == 1) {
            switch (msgType.charAt(0)){
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case 'A':
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }
}
