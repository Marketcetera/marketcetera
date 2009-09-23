package org.marketcetera.util.log;

import java.io.Serializable;
import java.util.Locale;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A bound message implementation, representing the combination of an
 * {@link I18NMessage} and its parameters, if any.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NBoundMessageBase<T extends I18NMessage>
    implements I18NBoundMessage
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;

    /**
     * The logging proxy name.
     */

    private final static String SELF_PROXY=
        I18NBoundMessageBase.class.getName();


    // INSTANCE DATA.

    private T mMessage;
    private Serializable[] mParams;


    // CONSTRUCTORS.

    /**
     * Creates a new bound message with the given message and
     * parameters.
     *
     * @param message The message.
     * @param params The parameters. If null, {@link #EMPTY_PARAMS} is
     * used instead.
     */

    I18NBoundMessageBase
        (T message,
         Serializable... params)
    {
        mMessage=message;
        if ((params==null) || (params.length==0)) {
            mParams=EMPTY_PARAMS;
        } else {
            mParams=params;
        }
    }


    // I18NBoundMessage.

    @Override
    public I18NLoggerProxy getLoggerProxy()
    {
        return getMessage().getLoggerProxy();
    }

    @Override
    public I18NMessageProvider getMessageProvider()
    {
        return getMessage().getMessageProvider();
    }

    @Override
    public T getMessage()
    {
        return mMessage;
    }

    @Override
    public Serializable[] getParams()
    {
        return mParams;
    }

    @Override
    public Object[] getParamsAsObjects()
    {
        return getParams();
    }

    @Override
    public String getText
        (Locale locale)
    {
        return getMessageProvider().getText
            (locale,getMessage(),getParamsAsObjects());
    }

    @Override
    public String getText()
    {
        return getMessageProvider().getText
            (getMessage(),getParamsAsObjects());
    }

    @Override
    public void error
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().errorProxy
            (SELF_PROXY,category,throwable,getMessage(),getParamsAsObjects());
    }

    @Override
    public void error
        (Object category)
    {
        getLoggerProxy().errorProxy
            (SELF_PROXY,category,getMessage(),getParamsAsObjects());
    }

    @Override
    public void warn
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().warnProxy
            (SELF_PROXY,category,throwable,getMessage(),getParamsAsObjects());
    }
    
    @Override
    public void warn
        (Object category)
    {
        getLoggerProxy().warnProxy
            (SELF_PROXY,category,getMessage(),getParamsAsObjects());
    }

    @Override
    public void info
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().infoProxy
            (SELF_PROXY,category,throwable,getMessage(),getParamsAsObjects());
    }
    
    @Override
    public void info
        (Object category)
    {
        getLoggerProxy().infoProxy
            (SELF_PROXY,category,getMessage(),getParamsAsObjects());
    }

    @Override
    public void debug
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().debugProxy
            (SELF_PROXY,category,throwable,getMessage(),getParamsAsObjects());
    }
    
    @Override
    public void debug
        (Object category)
    {
        getLoggerProxy().debugProxy
            (SELF_PROXY,category,getMessage(),getParamsAsObjects());
    }

    @Override
    public void trace
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().traceProxy
            (SELF_PROXY,category,throwable,getMessage(),getParamsAsObjects());
    }

    @Override
    public void trace
        (Object category)
    {
        getLoggerProxy().traceProxy
            (SELF_PROXY,category,getMessage(),getParamsAsObjects());
    }


    // Object.

    @Override
    public String toString()
    {
        return getText();
    }

    @Override
    public int hashCode()
    {
        return (ObjectUtils.hashCode(getMessage())+
                ArrayUtils.hashCode(getParams()));
    }

    @Override
    public boolean equals
        (Object other)
    {
        if (this==other) {
            return true;
        }
        if ((other==null) || !getClass().equals(other.getClass())) {
            return false;
        }
        I18NBoundMessageBase<?> o=(I18NBoundMessageBase<?>)other;
        return (ObjectUtils.equals(getMessage(),o.getMessage()) &&
                ArrayUtils.isEquals(getParams(),o.getParams()));
    }
}
