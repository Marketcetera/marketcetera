package org.marketcetera.photon;

import java.lang.reflect.Field;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NMessage0P;

/* $License$ */

/**
 * Collection of enums used to convert FIX field names into 
 * more human readable names
 *
 * @author toli
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXFieldLocalizer
    implements FixMessages
{
    public static final String FIELD_NAME_PREFIX = "fix_field_"; //$NON-NLS-1$
    public static final String FIELD_VALUE_PREFIX = "fix_field_value_"; //$NON-NLS-1$

    public static String getLocalizedFIXFieldName(String fixFieldName)
    {
    	try {
    		return getMessageString(FIELD_NAME_PREFIX + fixFieldName);
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
