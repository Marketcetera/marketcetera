package com.swtworkbench.community.xswt.scripting;

import java.text.ParseException;

public interface EvaluationContext {
	public Object evaluateExpression(String source) throws ParseException, RuntimeException;
	public Bindings evaluateScript(String source) throws ParseException, RuntimeException;
}
