/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.mina.transport.socket.nio;

import org.apache.mina.common.IoConnector;
import org.apache.mina.common.support.DelegatedIoConnector;
import org.apache.mina.transport.socket.nio.support.DatagramConnectorDelegate;
import org.apache.mina.util.NewThreadExecutor;
import java.util.concurrent.Executor;

/**
 * {@link IoConnector} for datagram transport (UDP/IP).
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 451854 $, $Date: 2006-10-02 11:30:11 +0900 (월, 02 10월 2006) $
 */
public class DatagramConnector extends DelegatedIoConnector
{
    /**
     * Creates a new instance using a NewThreadExecutor 
     */
    public DatagramConnector()
    {
        init( new DatagramConnectorDelegate( this, new NewThreadExecutor( ) ) );
    }

    /**
     * Creates a new instance.
     * 
     * @param executor Executor to use for launching threads
     */
    public DatagramConnector( Executor executor )
    {
        init( new DatagramConnectorDelegate( this, executor ) );
    }
    
    /**
     * Sets the default config this connector should use.
     * 
     * @param defaultConfig the default config.
     * @throws NullPointerException if the specified value is <code>null</code>.
     */
    public void setDefaultConfig( DatagramConnectorConfig defaultConfig )
    {
        ( ( DatagramConnectorDelegate ) delegate ).setDefaultConfig( defaultConfig );
    }
}
