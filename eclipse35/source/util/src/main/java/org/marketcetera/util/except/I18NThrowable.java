package org.marketcetera.util.except;

import java.io.Serializable;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * An internationalized throwable. At creation, the message may be
 * set; upon message retrieval, {@link #getMessage()} and {@link
 * #getLocalizedMessage()} behave exactly as mandated by {@link
 * Throwable}, and as implemented by JDK throwables. {@link
 * #getDetail()} and {@link #getLocalizedDetail()} return a raw and
 * localized message respectively that includes both the receiver's
 * message as well as the associated message of the underlying cause
 * (both combined; or either one; or null, if none is set). A raw
 * message comprises the provider, message, and entry IDs, as well as
 * the message parameters; a localized message is looked up via a
 * message's associated provider.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface I18NThrowable
    extends Serializable
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
     * Returns the receiver's message.
     *
     * @return The message. It may be null.
     */

    I18NBoundMessage getI18NBoundMessage();
}
