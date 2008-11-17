package org.rubypeople.rdt.internal.core.builder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IResourceDelta;

public class ResourceDeltaFormatter {

    public String format(IResourceDelta delta) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(kindAsString(delta.getKind()));
        buffer.append(flagsAsString(delta.getFlags()));
        buffer.append(delta.getFullPath());
        buffer.append("\n");
        
        IResourceDelta[] affectedChildren = delta. getAffectedChildren();
        for (int i = 0; i < affectedChildren.length; i++) {
            IResourceDelta childDelta = affectedChildren[i];
            buffer.append(format(childDelta));
        }
        return buffer.toString();
    }
    
    private String flagsAsString(int flags) {
        StringBuffer buffer = new StringBuffer();
        for (Iterator iter = flagMap.keySet().iterator(); iter.hasNext();) {
            Integer flag = (Integer) iter.next();
            if ((flags & flag.intValue()) != 0)
                buffer.append(flagMap.get(flag)+ " ");
            
        }
        return buffer.toString();
    }

    private String kindAsString(int kind) {
        switch(kind) {
        case IResourceDelta.ADDED:
            return "Added  ";
        case IResourceDelta.REMOVED:
            return "Removed";
        case IResourceDelta.CHANGED:
            return "Changed";
        case IResourceDelta.ADDED_PHANTOM:
            return "AddedPh";
        case IResourceDelta.REMOVED_PHANTOM:
            return "RemovPh";
            
        }
        return String.valueOf(kind);
    }

    static Map flagMap = new HashMap();
    static {
        putFlag(IResourceDelta.CONTENT,     "Content");
        putFlag(IResourceDelta.ENCODING,    "Encoding");
        putFlag(IResourceDelta.DESCRIPTION, "Description");
        putFlag(IResourceDelta.OPEN,        "Open");
        putFlag(IResourceDelta.TYPE,        "Type");
        putFlag(IResourceDelta.SYNC,        "Sync");
        putFlag(IResourceDelta.MARKERS,     "Markers");
        putFlag(IResourceDelta.MOVED_FROM,  "Moved-from");
        putFlag(IResourceDelta.MOVED_TO,    "Moved-to");
    }
    private static void putFlag(int flag, String description) {
        flagMap.put(new Integer(flag), description);
    }
}
