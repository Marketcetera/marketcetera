package org.marketcetera.util.l10n;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.misc.ReflectUtils;

/**
 * Holder of meta-information about a message container class, such as
 * {@link Messages}. Note that only static, non-null fields are
 * introspected.
 *
 * @see org.marketcetera.util.log
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class ContainerClassInfo
    implements MessageInfoProvider
{

    // INSTANCE DATA.

    private Class<?> mContainer;
    private I18NMessageProvider mProvider;
    private I18NMessageInfo[] mMessageInfo;


    // CONSTRUCTORS.

    /**
     * Creates a new meta-information holder for the given message
     * container class.
     *
     * @param container The class.
     *
     * @throws I18NException Thrown if introspection of the given
     * class fails, or the class does not contain a message provider.
     */

    public ContainerClassInfo
        (Class<?> container)
        throws I18NException
    {
        mContainer=container;
        HashMap<String,I18NMessageInfo> messageInfo=
            new HashMap<String,I18NMessageInfo>();
        try {
            for (Field field:ReflectUtils.getAllFields(getContainer())) {
                String name=field.getName();
                if ((field.getModifiers()&Modifier.STATIC)==0) {
                    Messages.NONSTATIC_FIELD_IGNORED.info(this,name);
                    continue;
                }
                Class<?> type=field.getType();
                if (I18NMessage.class.isAssignableFrom(type)) {
                    I18NMessage message=
                        ((I18NMessage)(field.get(getContainer())));
                    if (message==null) {
                        Messages.NULL_FIELD_IGNORED.info(this,name);
                        continue;
                    }
                    String key=message.getMessageId()+"."+ //$NON-NLS-1$
                        message.getEntryId();
                    messageInfo.put
                        (key,new I18NMessageInfo
                         (key,message.getParamCount(),message));
                }
                if (I18NMessageProvider.class.isAssignableFrom(type)) {
                    mProvider=((I18NMessageProvider)
                               (field.get(getContainer())));
                }
            }
        } catch (IllegalAccessException ex) {
            throw new I18NException
                (ex,new I18NBoundMessage1P
                 (Messages.INTROSPECTION_FAILED,getContainer().getName()));
        }
        if (getProvider()==null) {
            throw new I18NException
                (new I18NBoundMessage1P
                 (Messages.MISSING_PROVIDER,getContainer().getName()));
        }
        mMessageInfo=messageInfo.values().toArray
            (I18NMessageInfo.EMPTY_ARRAY);
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's container class.
     *
     * @return The class.
     */

    public Class<?> getContainer()
    {
        return mContainer;
    }

    /**
     * Returns the internationalized message provider declared in the
     * receiver's container class.
     *
     * @return The provider.
     */

    public I18NMessageProvider getProvider()
    {
        return mProvider;
    }


    // MessageInfoProvider.

    @Override
    public I18NMessageInfo[] getMessageInfo()
    {
        return mMessageInfo;
    }
}
