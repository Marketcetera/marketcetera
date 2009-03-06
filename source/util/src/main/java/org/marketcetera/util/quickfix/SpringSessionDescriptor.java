package org.marketcetera.util.quickfix;

import java.util.HashMap;
import java.util.Map;
import org.marketcetera.util.except.I18NRuntimeException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.spring.LazyBean;
import quickfix.Dictionary;
import quickfix.FixVersions;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;

/**
 * A Spring-aware QuickFIX/J session descriptor. It also offers these
 * extensions:
 *
 * <ul>
 *
 * <li><p>The descriptor may be associated with overall session
 * settings {@link SpringSessionSettings}, which contain default
 * session parameter values used by the descriptor when its own
 * dictionary does not override them.</p></li>
 *
 * <li><p>The descriptor's data dictionary parameter can be set to
 * {@link #DEFAULT_DATA_DICTIONARY}, in which case validation will
 * take place using the default data dictionary appropriate for the
 * FIX version implied by the descriptor's begin string.</p></li>
 *
 * </ul>
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class SpringSessionDescriptor
    extends LazyBean
{

    // CLASS DATA.

    /**
     * The sentinel value for the data dictionary parameter which
     * forces validation using the default data dictionary appropriate
     * for the FIX version implied by the descriptor's begin string.
     */

    public static final String DEFAULT_DATA_DICTIONARY=
        "metc.DefaultDataDictionary"; //$NON-NLS-1$

    /**
     * The map of begin strings to default data dictionaries.
     */

    private static final Map<String,String> DATA_DICTIONARY_MAP=
        new HashMap<String,String>();


    // INSTANCE DATA.

    private Map<String,String> mDictionary;
    private SpringSessionSettings mSettings;
    private Dictionary mQDictionary;
    private SessionID mQSessionID;


    // CLASS METHODS.

    /**
     * Initialization.
     */ 

    static {
        DATA_DICTIONARY_MAP.put
            (FixVersions.BEGINSTRING_FIX40,"FIX40.xml"); //$NON-NLS-1$
        DATA_DICTIONARY_MAP.put
            (FixVersions.BEGINSTRING_FIX41,"FIX41.xml"); //$NON-NLS-1$
        DATA_DICTIONARY_MAP.put
            (FixVersions.BEGINSTRING_FIX42,"FIX42.xml"); //$NON-NLS-1$
        DATA_DICTIONARY_MAP.put
            (FixVersions.BEGINSTRING_FIX43,"FIX43.xml"); //$NON-NLS-1$
        DATA_DICTIONARY_MAP.put
            (FixVersions.BEGINSTRING_FIX44,"FIX44.xml"); //$NON-NLS-1$
    }


    // INSTANCE METHODS.

    /**
     * Sets the receiver's dictionary to the given one. This
     * dictionary is a key-value map of QuickFIX/J session parameters;
     * it is not the data dictionary.
     *
     * @param dictionary The dictionary. It may be null.
     */

    public void setDictionary
        (Map<String,String> dictionary)
    {
        assertNotProcessed();
        mDictionary=dictionary;
    }

    /**
     * Returns the receiver's dictionary. This dictionary is a
     * key-value map of QuickFIX/J session parameters; it is not the
     * data dictionary.
     *
     * @return The dictionary. It may be null.
     */

    public Map<String,String> getDictionary()
    {
        return mDictionary;
    }

    /**
     * Sets the receiver's session settings to the given ones.
     *
     * @param settings The settings. It may be null.
     */

    public void setSettings
        (SpringSessionSettings settings)
    {
        assertNotProcessed();
        mSettings=settings;
    }

    /**
     * Returns the receiver's session settings.
     *
     * @return The settings. It may be null.
     */

    public SpringSessionSettings getSettings()
    {
        return mSettings;
    }

    /**
     * Returns the receiver's QuickFIX/J dictionary.
     *
     * @return The dictionary.
     */

    public Dictionary getQDictionary()
    {
        ensureProcessed();
        return mQDictionary;        
    }

    /**
     * Returns the receiver's QuickFIX/J session ID.
     *
     * @return The ID.
     */

    public SessionID getQSessionID()
    {
        ensureProcessed();
        return mQSessionID;
    }

    /**
     * Returns the value in the receiver's dictionary for the given
     * key. If the dictionary has no value, and the receiver has valid
     * session settings, the value stored within those settings is
     * returned.
     *
     * @param key The key.
     *
     * @return The value. It may be null.
     */

    private String getKeyValue
        (String key)
    {
        if ((getDictionary()!=null) &&
            getDictionary().containsKey(key)) {
            return getDictionary().get(key);
        }
        if ((getSettings()!=null) &&
            (getSettings().getDefaults()!=null) &&
            getSettings().getDefaults().containsKey(key)) {
            return (String)(getSettings().getDefaults().get(key));
        }
        return null;
    }

    /**
     * Asserts that the given key has a non-null value in the
     * receiver's dictionary.
     *
     * @param key The key.
     * @param value The value.
     *
     * @throws I18NRuntimeException Thrown if the assertion is false.
     */

    private void assertValueSet
        (String key,
         String value)
        throws I18NRuntimeException
    {
        if (value==null) {
            throw new I18NRuntimeException
                (new I18NBoundMessage1P(Messages.FIELD_REQUIRED,key));
        }
    }


    // LazyBean.

    @Override
    protected void process()
    {
        if (getSettings()==null) {
            Messages.MISSING_SETTINGS.warn(this);
        }
        String beginString=getKeyValue(SessionSettings.BEGINSTRING);
        assertValueSet(SessionSettings.BEGINSTRING,beginString);
        String senderCompID=getKeyValue(SessionSettings.SENDERCOMPID);
        assertValueSet(SessionSettings.SENDERCOMPID,senderCompID);
        String targetCompID=getKeyValue(SessionSettings.TARGETCOMPID);
        assertValueSet(SessionSettings.TARGETCOMPID,targetCompID);
        mQDictionary=new Dictionary();
        if (getDictionary()!=null) {
            for (Map.Entry<String,String> entry:getDictionary().entrySet()) {
                mQDictionary.setString(entry.getKey(),entry.getValue());
            }
        }
        mQSessionID=new SessionID
            (beginString,
             senderCompID,
             getKeyValue(SessionSettings.SENDERSUBID),
             getKeyValue(SessionSettings.SENDERLOCID), 
             targetCompID,
             getKeyValue(SessionSettings.TARGETSUBID), 
             getKeyValue(SessionSettings.TARGETLOCID), 
             getKeyValue(SessionSettings.SESSION_QUALIFIER));
        String dataDictionary=getKeyValue(Session.SETTING_DATA_DICTIONARY);
        if (DEFAULT_DATA_DICTIONARY.equals(dataDictionary)) {
            if (!DATA_DICTIONARY_MAP.containsKey(beginString)) {
                throw new I18NRuntimeException
                    (new I18NBoundMessage1P
                     (Messages.NO_DEFAULT_DATA_DICTIONARY,beginString));
            }
            mQDictionary.setString
                (Session.SETTING_DATA_DICTIONARY,
                 DATA_DICTIONARY_MAP.get(beginString));
        }
    }
}
