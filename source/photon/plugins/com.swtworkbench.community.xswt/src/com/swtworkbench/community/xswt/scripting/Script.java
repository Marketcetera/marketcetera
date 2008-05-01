package com.swtworkbench.community.xswt.scripting;

import java.text.ParseException;
import java.util.Iterator;

import org.eclipse.swt.widgets.Control;

public class Script implements Bindings, IScriptable {

	private String lang;
	private String source;
	
	public Script(Control control) {
	}

	/* (non-Javadoc)
	 * @see com.swtworkbench.community.xswt.scripting.IScriptable#getLang()
	 */
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

	private Bindings contextBindings;
	
	/* (non-Javadoc)
	 * @see com.swtworkbench.community.xswt.scripting.IScriptable#evaluateScript(com.swtworkbench.community.xswt.scripting.EvaluationContext)
	 */
	public void evaluateScript(EvaluationContext context) throws ParseException {
		contextBindings = context.evaluateScript(source);
	}
	
	public boolean has(String name) {
		return contextBindings.has(name);
	}
	public Object get(String name) {
		return contextBindings.get(name);
	}
	public void set(String name, Object value) {
		contextBindings.set(name, value);
	}
	public Iterator symbols() {
		return contextBindings.symbols();
	}

	public void addBindingsListener(BindingsListener listener) {
	}
	public void removeBindingsListener(BindingsListener listener) {
	}
}
