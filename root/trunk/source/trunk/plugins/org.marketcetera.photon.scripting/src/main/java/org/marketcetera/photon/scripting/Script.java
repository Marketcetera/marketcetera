package org.marketcetera.photon.scripting;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

public class Script implements IScript {
	protected static final String RUBY_LANG_STRING = "ruby";
	private String script;
	private ScriptContext context;
	private String fileName = "<script>";
	private int lineNumber = 1;
	private int columnNumber = 1;

	public Script(String script) {
		super();
		this.script = script;
	}

	public Script(String script, ScriptContext context) {
		this(script);
		this.context = context;
	}
	
	public Script(String script, String fileName, int lineNo, int columnNo){
		this(script, null, fileName, lineNo, columnNo);
	}

	public Script(String script, ScriptContext context, String fileName, int lineNo, int columnNo){
		this(script, context);
		if (fileName != null){
			this.fileName = fileName;
		}
		this.lineNumber = lineNo;
		this.columnNumber = columnNo;
		
	}

	public void exec(BSFManager manager) throws BSFException {
		manager.exec(RUBY_LANG_STRING, getFileName(), getLineNumber(), getColumnNumber(), script);
	}
	
	public Object eval(BSFManager manager) throws BSFException {
		return manager.eval(RUBY_LANG_STRING, getFileName(), getLineNumber(), getColumnNumber(), script);
	}
	
	public String getScript(){
		return script;
	}

	public void setContext(ScriptContext ctxt) {
		context = ctxt;
	}


	public int getColumnNumber() {
		return columnNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public int getLineNumber() {
		return lineNumber;
	}
	
	protected ScriptContext getContext()
	{
		return context;
	}
}
