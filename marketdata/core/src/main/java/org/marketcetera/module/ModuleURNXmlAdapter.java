package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/* $License$ */
/**
 * An XML adapter to serialize ModuleURN into XML and back.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
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
