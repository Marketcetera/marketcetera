package org.rubypeople.rdt.internal.core;

import org.rubypeople.rdt.core.ILoadpathAttribute;
import org.rubypeople.rdt.internal.core.util.Util;

public class LoadpathAttribute implements ILoadpathAttribute {

	private String name;
	private String value;
	
	public LoadpathAttribute(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof LoadpathAttribute)) return false;
		LoadpathAttribute other = (LoadpathAttribute) obj;
		return this.name.equals(other.name) && this.value.equals(other.value);
	}

    public String getName() {
		return this.name;
    }

    public String getValue() {
		return this.value;
    }
    
    public int hashCode() {
     	return Util.combineHashCodes(this.name.hashCode(), this.value.hashCode());
    }
    
    public String toString() {
    	return this.name + "=" + this.value; //$NON-NLS-1$
    }

}
