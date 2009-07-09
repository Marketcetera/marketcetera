package org.marketcetera.util.l10n;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Holder of meta-information about a properties file that maps
 * message keys to message text.
 *
 * @see org.marketcetera.util.log
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class PropertiesFileInfo
    implements MessageInfoProvider
{

    // CLASS DATA.

    private static final Pattern PARAM_PATTERN=
        Pattern.compile("\\{(\\d+)"); //$NON-NLS-1$


    // INSTANCE DATA.

    private I18NMessageProvider mProvider;
    private Locale mLocale;
    private PropertyMessageInfo[] mMessageInfo;


    // CONSTRUCTORS.

    /**
     * Creates a new meta-information holder for the properties file
     * associated with the given provider and locale.
     *
     * @param provider The provider.
     * @param locale The locale. Use {@link Locale#ROOT} for the
     * fallback properties file.
     *
     * @throws I18NException Thrown if there is an I/O error while
     * attempting to read the properties file.
     */

    public PropertiesFileInfo
        (I18NMessageProvider provider,
         Locale locale)
        throws I18NException
    {
        mProvider=provider;
        mLocale=locale;

        // Build file name.

        StringBuilder builder=new StringBuilder();
        builder.append(getProvider().getProviderId());
        builder.append(I18NMessageProvider.MESSAGE_FILE_EXTENSION);
        if (getLocale()!=Locale.ROOT) {
            builder.append('_'); //$NON-NLS-1$
            builder.append(getLocale().toString());
        }
        builder.append(".properties"); //$NON-NLS-1$
        String fileName=builder.toString();        

        // Load file as a resource.

        InputStream stream=getProvider().getClass().getClassLoader().
            getResourceAsStream(fileName);
        if (stream==null) {
            throw new I18NException(new I18NBoundMessage1P
                                    (Messages.NONEXISTENT_RESOURCE,fileName));
        }
        Properties properties=new Properties();
        try {
            properties.load(stream);
        } catch (IOException ex) {
            throw new I18NException
                (ex,new I18NBoundMessage1P(Messages.LOADING_FAILED,fileName));
        }

        // Analyze file entries.

        HashMap<String,PropertyMessageInfo> messageInfo=
            new HashMap<String,PropertyMessageInfo>();
        for (String key:properties.stringPropertyNames()) {
            String messageText=properties.getProperty(key);
            try {
                new MessageFormat(messageText,getLocale());
            } catch (IllegalArgumentException ex) {
                throw new I18NException
                    (ex,new I18NBoundMessage1P(Messages.BAD_TEXT,messageText));
            }
            Matcher matcher=PARAM_PATTERN.matcher(messageText);
            int maxIndex=-1;
            while (matcher.find()) {
                int index=Integer.valueOf(matcher.group(1));
                if (index>maxIndex) {
                    maxIndex=index;
                }
            }
            messageInfo.put
                (key,new PropertyMessageInfo(key,maxIndex+1,messageText));
        }
        mMessageInfo=messageInfo.values().toArray
            (PropertyMessageInfo.EMPTY_ARRAY);
    }

    /**
     * Creates a new meta-information holder for the fallback
     * properties file associated with the given provider.
     *
     * @param provider The provider.
     *
     * @throws I18NException Thrown if there is an I/O error while
     * attempting to read the properties file.
     */

    public PropertiesFileInfo
        (I18NMessageProvider provider)
        throws I18NException
    {
        this(provider,Locale.ROOT);
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's message provider.
     *
     * @return The provider.
     */

    public I18NMessageProvider getProvider()
    {
        return mProvider;
    }

    /**
     * Returns the receiver's locale.
     *
     * @return The locale. It is null for the fallback properties
     * file.
     */

    public Locale getLocale()
    {
        return mLocale;
    }


    // MessageInfoProvider.

    @Override
    public PropertyMessageInfo[] getMessageInfo()
    {
        return mMessageInfo;
    }
}
