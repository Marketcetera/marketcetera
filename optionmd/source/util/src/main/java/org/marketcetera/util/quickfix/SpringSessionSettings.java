package org.marketcetera.util.quickfix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.except.I18NRuntimeException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.misc.ReflectUtils;
import org.marketcetera.util.spring.LazyBean;
import quickfix.ConfigError;
import quickfix.FileStoreFactory;
import quickfix.LogFactory;
import quickfix.MessageStoreFactory;
import quickfix.SLF4JLogFactory;
import quickfix.SessionSettings;

/**
 * A Spring-aware QuickFIX/J session settings. It also offers these
 * extensions:
 *
 * <ul>
 *
 * <li><p>The settings may be associated with zero or more session
 * descriptors {@link SpringSessionDescriptor}, which inherit default
 * session parameter values from the settings.</p></li>
 *
 * <li><p>The log factory class may be specified via the extension
 * parameter {@link #LOG_FACTORY_CLASS_PARAM}; if it is not
 * specified, a {@link SLF4JLogFactory} is used.</p></li>
 *
 * <li><p>The message store factory class may be specified via the
 * extension parameter {@link #MESSAGE_STORE_FACTORY_CLASS_PARAM}; if
 * it is not specified, a {@link FileStoreFactory} is used.</p></li>
 *
 * </ul>
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class SpringSessionSettings
    extends LazyBean
{

    // CLASS DATA.

    /**
     * The parameter name for the QuickFIX/J log factory class
     * extension parameter.
     */

    public static final String LOG_FACTORY_CLASS_PARAM=
        "metc.LogFactoryClass"; //$NON-NLS-1$

    /**
     * The parameter name for the QuickFIX/J message store factory
     * class extension parameter.
     */

    public static final String MESSAGE_STORE_FACTORY_CLASS_PARAM=
        "metc.MessageStoreFactoryClass"; //$NON-NLS-1$


    // INSTANCE DATA.

    private Map<Object,Object> mDefaults;
    private List<SpringSessionDescriptor> mDescriptors;
    private SessionSettings mQSettings;
    private LogFactory mQLogFactory;
    private MessageStoreFactory mQMessageStoreFactory;


    // INSTANCE METHODS.

    /**
     * Sets the receiver's defaults to the given ones. This is a
     * key-value map of QuickFIX/J session parameters, and all keys
     * and values are strings.
     *
     * @param defaults The defaults. It may be null.
     */

    public void setDefaults
        (Map<Object,Object> defaults)
    {
        assertNotProcessed();
        mDefaults=defaults;
    }

    /**
     * Returns the receiver's defaults. This is a key-value map of
     * QuickFIX/J session parameters, and all keys and values are
     * strings.
     *
     * @return The defaults. It may be null.
     */

    public Map<Object,Object> getDefaults()
    {
        return mDefaults;
    }

    /**
     * Sets the receiver's session descriptors to the given ones. This
     * method also sets the settings of each descriptor to the
     * receiver.
     *
     * @param descriptors The descriptors. It may be null.
     */

    public void setDescriptors
        (List<SpringSessionDescriptor> descriptors)
    {
        assertNotProcessed();
        mDescriptors=descriptors;
        if (getDescriptors()!=null) {
            for (SpringSessionDescriptor descriptor:getDescriptors()) {
                descriptor.setSettings(this);
            }
        }
    }

    /**
     * Returns the receiver's session descriptors.
     *
     * @return The descriptors. It may be null.
     */

    public List<SpringSessionDescriptor> getDescriptors()
    {
        return mDescriptors;
    }

    /**
     * Returns the receiver's QuickFIX/J session settings.
     *
     * @return The settings.
     */

    public SessionSettings getQSettings()
    {
        ensureProcessed();
        return mQSettings;        
    }

    /**
     * Returns the receiver's QuickFIX/J log factory.
     *
     * @return The factory.
     */

    public LogFactory getQLogFactory()
    {
        ensureProcessed();
        return mQLogFactory;
    }

    /**
     * Returns the receiver's QuickFIX/J message store factory.
     *
     * @return The factory.
     */

    public MessageStoreFactory getQMessageStoreFactory()
    {
        ensureProcessed();
        return mQMessageStoreFactory;
    }


    // LazyBean.

    @Override
    protected void process()
    {
        String logFactoryClass=null;
        String messageStoreFactoryClass=null;
        mQSettings=new SessionSettings();
        if (getDefaults()!=null) {
            HashMap<Object,Object> defaults=new HashMap<Object,Object>
                (getDefaults());
            logFactoryClass=(String)getDefaults().get
                (LOG_FACTORY_CLASS_PARAM);
            defaults.remove(LOG_FACTORY_CLASS_PARAM);
            messageStoreFactoryClass=(String)getDefaults().get
                (MESSAGE_STORE_FACTORY_CLASS_PARAM);
            defaults.remove(MESSAGE_STORE_FACTORY_CLASS_PARAM);
            mQSettings.set(defaults);
        }
        if (getDescriptors()!=null) {
            for (SpringSessionDescriptor descriptor:getDescriptors()) {
                try {
                    mQSettings.set(descriptor.getQSessionID(),
                                   descriptor.getQDictionary());
                } catch (ConfigError ex) {
                    throw new I18NRuntimeException(ex,Messages.CONFIG_ERROR);
                }
            }
        }
        if (logFactoryClass==null) {
            mQLogFactory=new SLF4JLogFactory(mQSettings);
        } else {
            try {
                mQLogFactory=(LogFactory)
                    ReflectUtils.getInstance
                    (logFactoryClass,
                     new Class[] {mQSettings.getClass()},
                     new Object[] {mQSettings});
            } catch (Exception ex) {
                ExceptUtils.interrupt(ex);
                throw new I18NRuntimeException
                    (ex,new I18NBoundMessage1P
                     (Messages.BAD_LOG_FACTORY,logFactoryClass));
            }
        }
        if (messageStoreFactoryClass==null) {
            mQMessageStoreFactory=new FileStoreFactory(mQSettings);
        } else {
            try {
                mQMessageStoreFactory=(MessageStoreFactory)
                    ReflectUtils.getInstance
                    (messageStoreFactoryClass,
                     new Class[] {mQSettings.getClass()},
                     new Object[] {mQSettings});
            } catch (Exception ex) {
                ExceptUtils.interrupt(ex);
                throw new I18NRuntimeException
                    (ex,new I18NBoundMessage1P
                     (Messages.BAD_MESSAGE_STORE_FACTORY,
                      messageStoreFactoryClass));
            }
        }
    }
}
