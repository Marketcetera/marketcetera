package org.marketcetera.photon;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NMessage0P;

/* $License$ */

/**
 * Converts FIX field names into more human readable names
 *
 * @author toli
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXFieldLocalizer
    implements FixMessages
{
    /**
     * caches field names retrieved from the FIX message catalog
     */
    private static Map<String,String> sFieldNames = new HashMap<String,String>();
    public static final String FIELD_NAME_PREFIX = "fix_field_"; //$NON-NLS-1$
    public static final String FIELD_VALUE_PREFIX = "fix_field_value_"; //$NON-NLS-1$

    /**
     * Gets the <code>FIX</code> field name from the localized name.
     *
     * <p>Note that this method retrieves information from the FIX lookup cache.  Therefore,
     * the reverse-lookup will not be available until the cache is populated.  If there is no
     * match in the cache, the name supplied as a parameter will be returned.
     * 
     * @param inLocalizedFIXFieldName a <code>String</code> value containing the localized <code>FIX</code> field name
     * @return a <code>String</code> value containing the <code>FIX</code> field name if one exists in the cache.
     */
    public static String readFIXFieldNameFromCache(String inLocalizedFIXFieldName)
    {
        // this method is kinda kludgey, but it's the most performant way to avoid the O(n)*m cost of
        //  searching m message catalogs with n entries each to find the original FIX field name
        synchronized(sFieldNames) {
            String fixFieldName = sFieldNames.get(inLocalizedFIXFieldName);
            if(fixFieldName == null) {
                return inLocalizedFIXFieldName;
            }
            return fixFieldName;
        }
    }
    public static String getLocalizedFIXFieldName(String fixFieldName)
    {
    	try {
    	    String fieldName = getMessageString(FIELD_NAME_PREFIX + fixFieldName);
    	    if(fieldName != null &&
    	       !fieldName.equals(fixFieldName)) {
    	        synchronized(sFieldNames) {
    	            sFieldNames.put(fieldName,
    	                            fixFieldName);
    	        }
    	    }
    		return fieldName;
    	} catch (Throwable t) {
		}
    	return fixFieldName;
    }

    public static String getLocalizedFIXValueName(String fixFieldName,
                                                  String fixFieldValueName)
   {
    	try {
    		return getMessageString(FIELD_VALUE_PREFIX + fixFieldName + "_" + fixFieldValueName); //$NON-NLS-1$
    	} catch (Throwable t) {
		}
    	return fixFieldValueName;
    }
    private static final FIXFieldLocalizer sInstance = new FIXFieldLocalizer();

    private static String getMessageString(String inKey)
        throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        inKey = inKey.replace(" ", //$NON-NLS-1$
                              "_"); //$NON-NLS-1$
        Class<FixMessages> messagesClass = FixMessages.class;
        Field message = messagesClass.getField(inKey);
        String text = PROVIDER.getText((I18NMessage0P)message.get(sInstance));
        return text;
    }
}
