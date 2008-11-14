package org.rubypeople.rdt.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;

public class LogicalType extends RubyType implements IType {

	private IType[] types;

	public LogicalType(IType[] types) {
		super((RubyElement)types[0].getParent(), types[0].getElementName());
		this.types = types;
	}
	
	@Override
	public IRubyElement[] getChildren() throws RubyModelException {
		List<IRubyElement> children = new ArrayList<IRubyElement>();
		for (int i = 0; i < types.length; i++) {
			IRubyElement[] subchildren = types[i].getChildren();
			for (int j = 0; j < subchildren.length; j++) {
				if (subchildren[j] != null) children.add(subchildren[j]);
			}
		}
		return (IRubyElement[]) children.toArray(new IRubyElement[children.size()]);
	}
	
	@Override
	public boolean hasChildren() throws RubyModelException {
		for (int i = 0; i < types.length; i++) {
			if (types[i].hasChildren()) return true;
		}
		return false;
	}
	
	@Override
	public boolean isModule() {
		return types[0].isModule();
	}
	
	public IType[] getOriginalTypes() {
		return types;
	}
	
}
