package org.marketcetera.photon.scripting;

import junit.framework.Test;
import junit.framework.TestCase;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFManager;
import org.jruby.exceptions.RaiseException;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MarketceteraTestSuite;

public class JRubyBSFTest extends TestCase {

	public static Test suite() {
		BSFManager.registerScriptingEngine("ruby", "org.jruby.javasupport.bsf.JRubyEngine", new String [] {"rb"});

		return new MarketceteraTestSuite(JRubyBSFTest.class);
	}
	
	// Sanity test to see that the scripting engine is working
	public void testJRubyBSF() throws Exception {
		BSFManager manager = new BSFManager();
		Integer foo = 7;
		manager.declareBean("foo", foo , Integer.class);
		Object result = manager.eval("ruby", "(java)", 1, 1, "$foo+1");
		long longResult = (Long) result;
		assertEquals(8, longResult);
	}
	
	
	public void testExceptionFromScriptSubclass() throws Exception {


		String script = "require 'java'\n" +
		"include_class \"org.marketcetera.photon.scripting.Calculator\"\n" +

		"class MyCalculator < Calculator\n" +
		"  def calculate_something\n" +
		"    9**15\n" +
		"  end\n" +
		"  def raise_something\n" +
		"    raise Exception.new\n" +
		"  end\n" +
		"end\n" +

		"MyCalculator.new()";

		BSFManager bsfManager = new BSFManager();

		BSFEngine bsfEngine = bsfManager.loadScriptingEngine("ruby");

//		 Execute the code and get the result
//		 of "MyCalculator.new()" as a Java object
		Object result = bsfEngine.eval("<java>", 1, 1, script);
		if (result instanceof Calculator){
			final Calculator calculator = (Calculator) result;
			assertEquals(205891132094649L, calculator.calculate_something());
			new ExpectedTestFailure(RaiseException.class) {
				@Override
				protected void execute() throws Throwable {
					calculator.raise_something();
				}
			}.run();
		} else {
			fail("Object of incorrect type returned from script");
		}
	}

}
