package org.marketcetera.util.rpc;

import javax.xml.bind.JAXBException;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface RpcServerServices<SessionClazz>
{
    /**
     * 
     *
     *
     * @param inCredentials
     * @return
     */
    SessionId login(RpcCredentials inCredentials);
    /**
     * 
     *
     *
     * @param inSessionId
     */
    void logout(String inSessionId);
    /**
     * 
     *
     *
     * @param inSessionId
     * @return
     */
    SessionHolder<SessionClazz> validateAndReturnSession(String inSessionId);
    /**
     * 
     *
     *
     * @param inObject
     * @return
     * @throws JAXBException
     */
    String marshall(Object inObject)
            throws JAXBException;
    /**
     * 
     *
     *
     * @param inData
     * @return
     * @throws JAXBException
     */
    <Clazz> Clazz unmarshall(String inData)
            throws JAXBException;
}
