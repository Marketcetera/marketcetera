package org.marketcetera.util.except;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.log.I18NMessageProvider;

/**
 * An internationalized throwable. At creation, the message and
 * provider may be set; upon message retrieval, {@link #getMessage()}
 * and {@link #getLocalizedMessage()} behave exactly as mandated by
 * {@link Throwable}, and as implemented by JDK throwables. {@link
 * #getDetail()} and {@link #getLocalizedDetail()} return a raw and
 * localized message respectively that includes both the receiver's
 * message as well as the associated message of the underlying cause
 * (both combined; or either one; or null, if none is set). A raw
 * message comprises the provider, message, and entry IDs, as well as
 * the message parameters; a localized message is looked up via a
 * supplied message provider.
 *
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
interface I18NThrowable
{
    /**
     * Returns the receiver's raw message, as implemented by {@link
     * Throwable#getMessage()}.
     *
     * @return The message. It may be null.
     */

    String getMessage();

    /**
     * Returns the receiver's localized message, as implemented by
     * {@link Throwable#getLocalizedMessage()}.
     *
     * @return The message. It may be null.
     */

    String getLocalizedMessage();

    /**
     * Returns the receiver's underlying cause, as implemented by
     * {@link Throwable#getCause()}.
     *
     * @return The cause. It may be null.
     */

    Throwable getCause();

    /**
     * Returns the receiver's raw message, possibly combined with the
     * raw message of the receiver's underlying cause.
     *
     * @return The message. It may be null if there is no raw message
     * for the receiver, and no underlying cause message.
     */

    String getDetail();

    /**
     * Returns the receiver's localized message, possibly combined
     * with the localized message of the receiver's underlying cause.
     *
     * @return The message. It may be null if there is no localized
     * message for the receiver, and no underlying localized cause
     * message.
     */

    String getLocalizedDetail();

    /**
     * Returns the receiver's message provider.
     *
     * @return The message provider. It may be null.
     */

    I18NMessageProvider getI18NProvider();

    /**
     * Returns the receiver's message.
     *
     * @return The message. It may be null.
     */

    I18NMessage getI18NMessage();

    /**
     * Returns the receiver's message parameters.
     *
     * @return The parameters. It may be null.
     */

    Object[] getParams();
}
