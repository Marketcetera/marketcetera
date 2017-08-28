/* $License$ */
/**
 * The client for communicating with remote instances of a strategy agent.
 * <p>
 * All the communication with remote strategy agents is accomplished via
 * {@link org.marketcetera.strategyengine.client.SEClient} interface. Instances of
 * this client can be created via
 * {@link org.marketcetera.saclient.SEClientFactoryImpl#create(org.marketcetera.saclient.SAClientParameters)}.
 * <p>
 * The client provides facilitites to deploy / manage strategies onto
 * the remote strategy agent and receive the data emitted by them.
 * See {@link org.marketcetera.strategyengine.client.SEClient} documentation for details.
 *
 * @author anshul@marketcetera.com
 * @version $Id: package-info.java 17242 2016-09-02 16:46:48Z colin $
 * @since 2.0.0
 */
@XmlSchema(namespace = "http://marketcetera.org/types/saclient")
package org.marketcetera.saclient;

import javax.xml.bind.annotation.XmlSchema;