package com.swtworkbench.community.xswt.scripting;

import com.swtworkbench.community.xswt.XSWT;

public interface ScriptingEngine {
	public EvaluationContext getEvaluationContext(String name, XSWT bindings);
}
