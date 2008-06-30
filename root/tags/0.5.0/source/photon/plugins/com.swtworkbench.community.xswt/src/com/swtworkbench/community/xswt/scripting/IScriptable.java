package com.swtworkbench.community.xswt.scripting;

import java.text.ParseException;

public interface IScriptable {

	public abstract String getLang();
	public abstract void setLang(String lang);

	public abstract String getSource();
	public abstract void setSource(String source);

	public abstract void evaluateScript(EvaluationContext context) throws ParseException;
}
