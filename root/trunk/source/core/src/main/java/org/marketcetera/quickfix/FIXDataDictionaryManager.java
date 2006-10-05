package org.marketcetera.quickfix;

import quickfix.ConfigError;
import quickfix.DataDictionary;

import java.io.InputStream;
import java.util.HashMap;

import org.marketcetera.core.ClassVersion;

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

    private final static String[][] dictNames = {
            {FIX_4_0_BEGIN_STRING, "FIX40.xml"},
            {FIX_4_1_BEGIN_STRING, "FIX41.xml"},
            {FIX_4_2_BEGIN_STRING, "FIX42.xml"},
            {FIX_4_3_BEGIN_STRING, "FIX43.xml"},
            {FIX_4_4_BEGIN_STRING, "FIX44.xml"}
    };

    private static DataDictionary sCurrent;
    private static HashMap<String, DataDictionary> dictionaryMap;

    private static void init() throws FIXFieldConverterNotAvailable {
        dictionaryMap = new HashMap<String, DataDictionary>(dictNames.length);
        for (String[] converterInfo : dictNames) {
            loadDictionary(converterInfo[1], false);
        }
    }



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

    public static DataDictionary getDictionary(){
        if(sCurrent == null) {
            sCurrent = getDataDictionary(FIX_4_2_BEGIN_STRING);
        }
        return sCurrent;
    }

    /**
     * Load a DataDictionary from the specified resource, optionally making it the default
     * dictionary.
     *
     * @param dictName The name of the resource from which to load a QuickFIX DataDictionary
     * @param makeDefault if true make the version of FIX specified in the data dictionary the default version
     * @throws FIXFieldConverterNotAvailable
     */
    public static void loadDictionary(String dictName, boolean makeDefault)
            throws  FIXFieldConverterNotAvailable
    {
        if (dictionaryMap == null){
            init();
        }
        DataDictionary theDict;
        InputStream input = FIXDataDictionaryManager.class.getClassLoader().getResourceAsStream(dictName);
        try {
            theDict = new DataDictionary(input);
        } catch (ConfigError configError) {
            throw new FIXFieldConverterNotAvailable(configError.getMessage(), configError);
        }
        dictionaryMap.put(theDict.getVersion(), theDict);
        if (makeDefault){
            setFIXVersion(theDict.getVersion());
        }
   }

    /**
     * Get the QuickFIX DataDictionary for the given version of FIX
     * @param version  The string representing the version of fix you're interested in.
     * @return the data dictionary
     */
    public static DataDictionary getDataDictionary(String version){
        if (dictionaryMap == null){
            try {
                init();
            } catch (FIXFieldConverterNotAvailable fixFieldConverterNotAvailable) {
                return null;
            }
        }
        return dictionaryMap.get(version);
    }

    /**
     * Set the default version of FIX to use in the rest of the methods on this class
     * @param inVersion  The string representing the new default version of fix.  See the constants defined in this class.
     * @throws FIXFieldConverterNotAvailable
     */
    public static void setFIXVersion(String inVersion) throws FIXFieldConverterNotAvailable
    {
        if (dictionaryMap == null){
           init();
        }
        DataDictionary dict = dictionaryMap.get(inVersion);
        if(dict == null) {
            throw new FIXFieldConverterNotAvailable("No converter for version "+inVersion);
        }
        sCurrent = dict;
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
