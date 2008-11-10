package org.rubypeople.rdt.internal.debug.core.parsing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.model.IVariable;
import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.rubypeople.rdt.internal.debug.core.model.RubyProcessingException;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;
import org.xmlpull.v1.XmlPullParser;

public class VariableReader extends XmlStreamReader {

	private RubyStackFrame stackFrame;
	private RubyVariable parent;
	private List<IVariable> variables;
	private String exceptionMessage;
	private String exceptionType;
	
	public VariableReader(XmlPullParser xpp) {
		super(xpp);
	}

	public VariableReader(AbstractReadStrategy readStrategy) {
		super(readStrategy);
	}

	public RubyVariable[] readVariables(RubyVariable variable) throws RubyProcessingException {
		return readVariables(variable.getStackFrame(), variable);
	}

	public RubyVariable[] readVariables(RubyStackFrame stackFrame) throws RubyProcessingException {
		return readVariables(stackFrame, null);
	}
		
	public RubyVariable[] readVariables(RubyStackFrame stackFrame, RubyVariable parent) throws RubyProcessingException {
		this.stackFrame = stackFrame;
		this.parent = parent;
		this.variables = new ArrayList<IVariable>();
		try {			
			this.read();
		} catch (Exception ex) {
			RdtDebugCorePlugin.log(ex);
			return new RubyVariable[0];
		}
		if (exceptionMessage != null) {
			throw new RubyProcessingException(exceptionType, exceptionMessage);		
		} else  if (isWaitTimeExpired()) {
			throw new RubyProcessingException("Timeout: Could not read result.");
		}
		RubyVariable[] variablesArray = new RubyVariable[variables.size()];
		variables.toArray(variablesArray);
		return variablesArray ;		
	}


	protected boolean processStartElement(XmlPullParser xpp) {
		String name = xpp.getName();
		if (name.equals("variables")) {
			return true;
		}
		if (name.equals("variable")) {
			String varName = xpp.getAttributeValue("", "name");
			String varValue = xpp.getAttributeValue("", "value");
			String kind = xpp.getAttributeValue("", "kind");			
			RubyVariable newVariable;
			if (varValue == null) {
				newVariable = new RubyVariable(stackFrame, varName, kind);
			}
			else {
			String typeName = xpp.getAttributeValue("", "type");
			    boolean hasChildren = xpp.getAttributeValue("", "hasChildren").equals("true");
			    String objectId = xpp.getAttributeValue("", "objectId");
				newVariable = new RubyVariable(stackFrame, varName, kind, varValue, typeName, hasChildren, objectId);			
			}
			newVariable.setParent(parent);
			variables.add(newVariable);						
			return true;
		}
		if (name.equals("processingException")) {
			exceptionMessage = xpp.getAttributeValue("", "message");
			exceptionType = xpp.getAttributeValue("", "type");
			return true;					
		}
		return false;
	}

	protected boolean processEndElement(XmlPullParser xpp) {		
		return !xpp.getName().equals("variable");
	}
}
