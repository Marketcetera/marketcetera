package org.marketcetera.symbology;

import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

/**
 * @author Graham Miller
 * @version $Id$
 */
public class ExchangeMap {
    private SymbolScheme scheme;
    private Map<String, Exchange> schemeToStandardTranslation;
    private Map<String, String> standardToSchemeTranslation = new HashMap<String, String>();

    protected ExchangeMap()
    {
    }

    public ExchangeMap(SymbolScheme scheme, Map<String, Exchange> schemeToStandardTranslation) {
        this.scheme = scheme;
        init(schemeToStandardTranslation);
    }

    protected void init(Map<String, Exchange> schemeToStandardTranslation) {
        this.schemeToStandardTranslation = schemeToStandardTranslation;
        for (Iterator iterator = schemeToStandardTranslation.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            standardToSchemeTranslation.put(schemeToStandardTranslation.get(key).getMarketIdentifierCode(), key);
        }
    }


    public SymbolScheme getScheme() {
        return scheme;
    }

    public Exchange getExchange(String schemeName){
        return schemeToStandardTranslation.get(schemeName);
    }

    public String getSchemeName(Exchange exch){
        return standardToSchemeTranslation.get(exch.getMarketIdentifierCode());
    }

    public String getSchemeName(String marketIdentifierCode){
        return standardToSchemeTranslation.get(marketIdentifierCode);
    }
}
