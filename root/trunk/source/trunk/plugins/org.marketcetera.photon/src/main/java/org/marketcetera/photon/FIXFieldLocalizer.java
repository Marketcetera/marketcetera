package org.marketcetera.photon;

import java.util.Locale;

import org.apache.commons.i18n.MessageManager;
import org.marketcetera.core.ClassVersion;

/**
 * Collection of enums used to convert FIX field names into 
 * more human readable names
 *
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class FIXFieldLocalizer {
    public static final String FIELD_NAME_PREFIX = "fix.field.";
    public static final String FIELD_VALUE_PREFIX = "fix.field.value.";


    public static String getLocalizedFIXFieldName(String fixFieldName)
    {
    	try {
    		return getMessageString(FIELD_NAME_PREFIX + fixFieldName);
    	}catch (Throwable t) {
		}
    	return fixFieldName;
    }

    public static String getLocalizedFIXValueName(String fixFieldName, String fixFieldValueName){
    	try {
    		return getMessageString(FIELD_VALUE_PREFIX + fixFieldName+"."+fixFieldValueName);
    	}catch (Throwable t) {
		}
    	return fixFieldValueName;
    	
    }
    /**
     * Corresponds to the suffix in the message bundle file. Currently, we are not distinguishing between different
     * kids of entries (title, summary, detail, etc) so we just use 'msg'.
     */
    private static String MESSAGE_BUNDLE_ENTRY = "msg";

    private static String getMessageString(String inKey)
    {
        return MessageManager.getText(inKey, MESSAGE_BUNDLE_ENTRY,  null, Locale.getDefault());
    }

}


