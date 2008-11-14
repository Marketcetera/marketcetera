package org.rubypeople.rdt.internal.debug.core.model;

import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;

public class RubyVariable extends RubyDebugElement implements IVariable {

    private boolean isStatic;
    private boolean isLocal;
    private boolean isInstance;
    private boolean isConstant;
    private RubyStackFrame stackFrame;
    private String name;
    private String objectId;
    private IValue value;
    private RubyVariable parent;
	private boolean valueHasChanged = false;

    public RubyVariable(RubyStackFrame stackFrame, String name, String scope) {
    	super(stackFrame.getDebugTarget());
        this.initialize(stackFrame, name, scope, null, new RubyValue(this));
    }

    public RubyVariable(RubyStackFrame stackFrame, String name, String scope, String value, String type, boolean hasChildren, String objectId) {
    	super(stackFrame.getDebugTarget());
        this.initialize(stackFrame, name, scope, objectId, new RubyValue(this, value, type, hasChildren));
    }

    protected final void initialize(RubyStackFrame stackFrame, String name, String scope, String objectId, RubyValue value) {
        this.stackFrame = stackFrame;
        this.value = value;
        this.name = name;
        this.objectId = objectId;
        this.isStatic = scope.equals("class");
        this.isLocal = scope.equals("local");
        this.isInstance = scope.equals("instance");
        this.isConstant = scope.equals("constant");
    }

    /**
     * @see org.eclipse.debug.core.model.IVariable#getValue()
     */
    public IValue getValue() {
        return value;
    }

    /**
     * @see org.eclipse.debug.core.model.IVariable#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * @see org.eclipse.debug.core.model.IVariable#getReferenceTypeName()
     */
    public String getReferenceTypeName() {
        return "RefTypeName";
    }

    /**
     * @see org.eclipse.debug.core.model.IVariable#hasValueChanged()
     */
    public boolean hasValueChanged() throws DebugException {
        return valueHasChanged;
    }

    /**
     * @see org.eclipse.debug.core.model.IValueModification#setValue(String)
     */
    public void setValue(String expression) throws DebugException {
    	try {
    		String assignee = getName();
    		if (isHashValue()) {    			
    			assignee = parent.getName() + "[" + assignee + "]";
    		} else if (isArrayValue()) {
    			assignee = parent.getName() + assignee;
    		}
			RubyVariable var = getRubyDebuggerProxy().readInspectExpression(stackFrame, assignee + " = " + expression);
			this.value = var.getValue();
			this.valueHasChanged  = true;
			fireChangeEvent(DebugEvent.CONTENT);
		} catch (RubyProcessingException e) {
			throw new DebugException(new Status(Status.ERROR, RdtDebugCorePlugin.PLUGIN_ID, -1, e.getMessage(), e));
		}
    }

	public RubyDebuggerProxy getRubyDebuggerProxy() {
		return ((RubyDebugTarget) this.getDebugTarget()).getRubyDebuggerProxy();
	}

    /**
     * @see org.eclipse.debug.core.model.IValueModification#setValue(IValue)
     */
    public void setValue(IValue value) throws DebugException {
    	if (value instanceof RubyValue) {
    		RubyValue val = (RubyValue) value;
    		RubyVariable var = val.getOwner();
    		setValue(var.getName()); // just do a basic assignment
    	} else {
    		setValue(value.getValueString());
    	}
    }

    /**
     * @see org.eclipse.debug.core.model.IValueModification#supportsValueModification()
     */
    public boolean supportsValueModification() {
        return true;
    }

    /**
     * @see org.eclipse.debug.core.model.IValueModification#verifyValue(String)
     */
    public boolean verifyValue(String expression) throws DebugException {
    	try {
			RubyParser parser = new RubyParser();
			parser.parse(expression);
		} catch (SyntaxException e) {
			return false;
		}
        return true;
    }

    /**
     * @see org.eclipse.debug.core.model.IValueModification#verifyValue(IValue)
     */
    public boolean verifyValue(IValue value) throws DebugException {
        return false;
    }

    public String toString() {
        if (this.isHashValue()) {
            return this.getName() + " => " + this.getValue();
        }
		return this.getName() + " = " + this.getValue();

    }

    public RubyStackFrame getStackFrame() {
        return stackFrame;
    }

    public RubyVariable getParent() {
        return parent;
    }

    public void setParent(RubyVariable parent) {
        this.parent = parent;
    }

    public String getQualifiedName() {
        if (parent == null) {
            return this.getName();
        }
        if (this.isHashValue()) {
            if (((RubyValue) this.getValue()).getReferenceTypeName().equals("String")) {
                return parent.getQualifiedName() + "[" + this.getName() + "]";
            }
			return "[ObjectSpace._id2ref(" + this.getObjectId() + ")]";
        }
        if (this.getName().startsWith("[")) {
            // Array
            return parent.getQualifiedName() + this.getName();
        }
        return parent.getQualifiedName() + "." + this.getName();
    }

    public boolean isInstance() {
        return isInstance;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public String getObjectId() {
        return objectId;
    }

    public boolean isHashValue() {
        if (parent == null) return false;
        String type = ((RubyValue) parent.getValue()).getReferenceTypeName();
        return type.equals("Hash") || type.equals("HashWithIndifferentAccess") || type.equals("ActionController::Flash::FlashHash");
    }
    
    private boolean isArrayValue() {
    	if (parent == null) return false;
        String type = ((RubyValue) parent.getValue()).getReferenceTypeName();
        return type.equals("Array");
	}

}
