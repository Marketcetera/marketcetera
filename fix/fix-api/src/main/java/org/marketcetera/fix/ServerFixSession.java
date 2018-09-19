package org.marketcetera.fix;

import java.util.List;
import java.util.Set;

import org.marketcetera.admin.User;
import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.core.DomainObject;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.DataDictionary;

/* $License$ */

/**
 * Provides a server-oriented view of a FIX session.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ServerFixSession
        extends DomainObject
{
    /**
     * Get the active FIX session value.
     *
     * @return an <code>ActiveFixSession</code> value
     */
    ActiveFixSession getActiveFixSession();
    /**
     * Get the userWhitelist value.
     *
     * @return a <code>Set&lt;User&gt;</code> value or <code>null</code>
     */
    Set<User> getUserWhitelist();
    /**
     * Get the userBlacklist value.
     *
     * @return a <code>Set&lt;User&gt;</code> value or <code>null</code>
     */
    Set<User> getUserBlacklist();
    /**
     * Get the underlying FIX message factory value.
     *
     * @return a <code>FIXMessageFactory</code> value
     */
    FIXMessageFactory getFIXMessageFactory();
    /**
     * Get the underlying data dictionary value.
     *
     * @return a <code>DataDictionary</code> value
     */
    DataDictionary getDataDictionary();
    /**
     * Get the underlying FIX data dictionary value.
     *
     * @return a <code>FIXDataDictionary</code> value
     */
    FIXDataDictionary getFIXDataDictionary();
    /**
     * Get the FIX version value.
     *
     * @return a <code>FIXVersion</code> value
     */
    FIXVersion getFIXVersion();
    /**
     * Get the order modifiers for this session.
     *
     * @return a <code>List&lt;MessageModifier&gt;</code> value
     */
    List<MessageModifier> getOrderModifiers();
    /**
     * Get the response modifiers for this session.
     *
     * @return a <code>List&lt;MessageModifier&gt;</code> value
     */
    List<MessageModifier> getResponseModifiers();
}
