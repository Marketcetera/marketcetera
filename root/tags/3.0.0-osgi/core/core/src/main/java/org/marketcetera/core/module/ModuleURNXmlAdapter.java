package org.marketcetera.core.module;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/* $License$ */
/**
 * An XML adapter to serialize ModuleURN into XML and back.
 *
 * @version $Id: ModuleURNXmlAdapter.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
class ModuleURNXmlAdapter extends XmlAdapter<String,ModuleURN> {
    @Override
    public ModuleURN unmarshal(String inValue) throws Exception {
        return new ModuleURN(inValue);
    }

    @Override
    public String marshal(ModuleURN inURN) throws Exception {
        return inURN.getValue();
    }
}
