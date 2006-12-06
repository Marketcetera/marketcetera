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
    public static final String PREFIX = "fix.field.";


    public static String getLocalizedMessage(String fixFieldName)
    {
    	try {
    		return getMessageString(PREFIX + fixFieldName.toLowerCase());
    	}catch (Throwable t) {
		}
    	return fixFieldName;
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


