package org.marketcetera.photon.scripting;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

public class Script implements IScript {
	private String script;
	private String fileName = "<script>";
	private int lineNumber = 1;
	private int columnNumber = 1;

	public Script(String script) {
		super();
		this.script = script;
	}

	public Script(String script, String fileName, int lineNo, int columnNo){
		this(script);
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


	public int getColumnNumber() {
		return columnNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public int getLineNumber() {
		return lineNumber;
	}
	

	public String getID() {
		return getFileName();
	}

	@Override
	public boolean equals(Object otherScriptObj) {
		boolean equals = true;
		if (otherScriptObj instanceof IScript) {
			IScript otherScript = (IScript) otherScriptObj;
			String otherScriptString = otherScript.getScript();
			if (script == null){
				equals &= (otherScriptString == null);
			} else {
				equals &= script.equals(otherScriptString);
			}
			String otherID = otherScript.getID();
			if (getID() == null){
				equals &= otherID == null;
			} else {
				equals &= otherID.equals(getID());
			}
		}
		if (otherScriptObj instanceof Script) {
			Script otherScript = (Script) otherScriptObj;
			equals &= otherScript.getLineNumber()==lineNumber && otherScript.getColumnNumber() == columnNumber;
		}
		return equals;
	}
}
