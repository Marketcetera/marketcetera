package org.marketcetera.util.except;

import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.LogUtils;
import org.marketcetera.util.misc.ClassVersion;

/**
 * An internationalized runtime exception.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: I18NRuntimeException.java 17411 2017-04-28 14:50:38Z colin $
 */

/* $License$ */

@ClassVersion("$Id: I18NRuntimeException.java 17411 2017-04-28 14:50:38Z colin $")
public class I18NRuntimeException
    extends RuntimeException
    implements I18NThrowable
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // INSTANCE DATA.

    private I18NBoundMessage mMessage;
    

    // CONSTRUCTORS.

    /**
     * Constructs a new throwable without a message or an underlying
     * cause.
     */

    public I18NRuntimeException() {}

    /**
     * Constructs a new throwable without a message, but with the
     * given underlying cause.
     *
     * @param cause The cause.
     */

    public I18NRuntimeException
        (Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs a new throwable with the given message, but without
     * an underlying cause.
     *
     * @param message The message.
     */

    public I18NRuntimeException
        (I18NBoundMessage message)
    {
        super(LogUtils.getSimpleMessage(message));
        mMessage=message;
    }

    /**
     * Constructs a new throwable with the given message and
     * underlying cause.
     *
     * @param cause The cause.
     * @param message The message.
     */

    public I18NRuntimeException
        (Throwable cause,
         I18NBoundMessage message)
    {
        super(LogUtils.getSimpleMessage(message),cause);
        mMessage=message;
    }
    @Override
    public String getLocalizedMessage()
    {
        if(getI18NBoundMessage() == null) {
            return super.getMessage();
        }
        return getI18NBoundMessage().getText();
    }
    /* (non-Javadoc)
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage()
    {
        return getLocalizedMessage();
    }
    @Override
    public String getDetail()
    {
        return I18NExceptUtils.getDetail(this);
    }

    @Override
    public String getLocalizedDetail()
    {
        return I18NExceptUtils.getLocalizedDetail(this);
    }

    @Override
    public I18NBoundMessage getI18NBoundMessage()
    {
        return mMessage;
    }


    // Object.

    @Override
    public int hashCode()
    {
        return ExceptUtils.getHashCode(this);
    }

    @Override
    public boolean equals
        (Object other)
    {
        return ExceptUtils.areEqual(this,other);
    }
}
