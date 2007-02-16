package org.marketcetera.photon.scripting;

import junit.framework.TestCase;

import org.apache.bsf.BSFManager;

public class JRubyBSFTest extends TestCase {

	// Sanity test to see that the scripting engine is working
	public void testJRubyBSF() throws Exception {
		BSFManager.registerScriptingEngine("ruby", "org.jruby.javasupport.bsf.JRubyEngine", new String [] {"rb"});
		BSFManager manager = new BSFManager();
		Integer foo = 7;
		manager.declareBean("foo", foo , Integer.class);
		Object result = manager.eval("ruby", "(java)", 1, 1, "$foo+1");
		long longResult = (Long) result;
		assertEquals(8, longResult);
	}
}
