package org.marketcetera.symbology;

import org.marketcetera.core.ClassVersion;

import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class PropertiesExchangeMap extends ExchangeMap {
    public PropertiesExchangeMap(Properties props) {
        init(props);
    }

    public PropertiesExchangeMap(String resourceName) throws IOException {
        URL url = Exchanges.class.getClassLoader().getResource(resourceName);
        InputStream in = url.openStream();
        Properties props = new Properties();
        props.load(in);
        init(props);
   }

    private void init(Properties props) {
        Map<String, Exchange> map = new HashMap<String, Exchange>();
        for (Object o : props.keySet()) {
            String key = (String) o;
            String marketIdentifierCode = (String) props.get(key);
            Exchange exch = Exchanges.getExchange(marketIdentifierCode);
            if (exch == null) {
                Messages.ERROR_EXCHANGE_DNE.error(this, marketIdentifierCode);
            } else {
                map.put(key, exch);
            }
        }
        init(map);
    }


}
