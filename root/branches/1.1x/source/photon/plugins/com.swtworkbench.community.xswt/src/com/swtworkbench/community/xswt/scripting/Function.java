package com.swtworkbench.community.xswt.scripting;

public interface Function {
	public String getName();
	public int arity();
	public Class argumentType(int i);
	public Object invoke(Object[] args) throws Exception;
}
