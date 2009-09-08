/* $License$ */
/**
 * The client for communicating with remote instances of a strategy agent.
 * <p>
 * All the communication with remote strategy agents is accomplished via
 * {@link org.marketcetera.saclient.SAClient} interface. Instances of
 * this client can be created via
 * {@link org.marketcetera.saclient.SAClientFactory#create(org.marketcetera.saclient.SAClientParameters)}.
 * <p>
 * The client provides facilitites to deploy / manage strategies onto
 * the remote strategy agent and receive the data emitted by them.
 * See {@link org.marketcetera.saclient.SAClient} documentation for details.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@XmlSchema(namespace = "http://marketcetera.org/types/saclient")
package org.marketcetera.saclient;

import javax.xml.bind.annotation.XmlSchema;