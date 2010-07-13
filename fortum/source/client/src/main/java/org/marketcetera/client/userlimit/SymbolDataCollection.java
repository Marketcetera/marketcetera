package org.marketcetera.client.userlimit;

import java.util.*;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.client.Client;
import org.marketcetera.client.ClientInitException;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.Util;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Manages the set of {@link SymbolData} objects associated with
 * the current user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public class SymbolDataCollection
{
    public SymbolDataCollection()
        throws ClientInitException, ConnectionException
    {
        load();
    }
    public synchronized final void load()
            throws ClientInitException, ConnectionException
    {
        Client client = getClient();
        Properties userData = client.getUserData();
        populateInstrumentList(userData);
    }
    public Collection<SymbolData> getSymbols()
    {
        return Collections.unmodifiableCollection(symbolData.values());
    }
    public synchronized SymbolData getSymbolData(String inSymbol)
    {
        return symbolData.get(inSymbol);
    }
    public synchronized void add(SymbolData inData)
    {
        symbolData.put(inData.getSymbol(),
                       inData);
    }
    public synchronized SymbolData remove(SymbolData inData)
    {
        return symbolData.remove(inData.getSymbol());
    }
    public synchronized void store()
            throws ClientInitException, ConnectionException
    {
        Client client = getClient();
        Properties userData = client.getUserData();
        if(userData == null) {
            userData = new Properties();
        }
        String updatedData = toConsolidatedFormat();
        if(updatedData == null) {
            userData.remove(DATA_KEY);
        } else {
            userData.setProperty(DATA_KEY,
                                 toConsolidatedFormat());
        }
        client.setUserData(userData);
    }
    public synchronized String toConsolidatedFormat()
    {
        Properties data = new Properties();
        for(SymbolData symbol : symbolData.values()) {
            data.setProperty(symbol.getSymbol(),
                             symbol.toConsolidatedFormat());
        }
        return Util.propertiesToString(data);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("SymbolDataCollection [%s]", //$NON-NLS-1$
                             symbolData);
    }
    private synchronized void populateInstrumentList(Properties inProperties)
    {
        symbolData.clear();
        if(inProperties == null ||
           inProperties.isEmpty()) {
            return;
        }
        String consolidatedData = inProperties.getProperty(DATA_KEY);
        if(consolidatedData == null ||
           consolidatedData.isEmpty()) {
            return;
        }
        // parse the string into its components
        Properties symbols = Util.propertiesFromString(consolidatedData);
        for(Object symbolString : symbols.values()) {
            SymbolData data = new SymbolData((String)symbolString);
            symbolData.put(data.getSymbol(),
                           data);
        }
    }
    /**
     * 
     *
     *
     * @param inClient
     */
    static void setClient(Client inClient)
    {
        synchronized(SymbolDataCollection.class) {
            SLF4JLoggerProxy.debug(SymbolDataCollection.class,
                                   "Setting client implementation to {}", //$NON-NLS-1$
                                   inClient);
            client = inClient;
        }
    }
    /**
     * 
     *
     *
     * @return
     * @throws ClientInitException
     */
    private static Client getClient()
            throws ClientInitException
    {
        synchronized(SymbolDataCollection.class) {
            if(client == null) {
                setClient(ClientManager.getInstance());
            }
        }
        return client;
    }
    private final static String DATA_KEY = SymbolDataCollection.class.getCanonicalName();
    @GuardedBy("this")
    private final Map<String,SymbolData> symbolData = new TreeMap<String,SymbolData>();
    @GuardedBy("SymbolDataCollection.class")
    private static Client client = null;
}
