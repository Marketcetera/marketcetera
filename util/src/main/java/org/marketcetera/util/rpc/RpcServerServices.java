package org.marketcetera.util.rpc;

import javax.xml.bind.JAXBException;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 * Provides common RPC server services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface RpcServerServices<SessionClazz>
{
    /**
     * Authenticates the given credentials.
     *
     * @param inCredentials an <code>RpcCredentials</code> value
     * @return a <code>SessionId</code> value
     */
    SessionId login(RpcCredentials inCredentials);
    /**
     * Invalidates the given session.
     *
     * @param inSessionId a <code>String</code> value
     */
    void logout(String inSessionId);
    /**
     * Validates that the given session is active.
     *
     * @param inSessionId a <code>String</code> value
     * @return a <code>SessionHolder&lt;SessionClazz&gt;</code> value
     */
    SessionHolder<SessionClazz> validateAndReturnSession(String inSessionId);
    /**
     * Marshals the given object to XML.
     *
     * @param inObject an <code>Object</code> value
     * @return a <code>String</code> value
     * @throws JAXBException if an error occurs marshalling the object
     */
    String marshal(Object inObject)
            throws JAXBException;
    /**
     * Unmarshals the given XML stream. 
     *
     * @param inData a <code>String</code> value
     * @return a <code>Clazz</code> value
     * @throws JAXBException if an error occurs unmarshalling the stream
     */
    <Clazz> Clazz unmarshall(String inData)
            throws JAXBException;
}
