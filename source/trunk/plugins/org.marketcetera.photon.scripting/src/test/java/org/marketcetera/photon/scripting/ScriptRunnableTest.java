package org.marketcetera.photon.scripting;

import junit.framework.TestCase;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

public class ScriptRunnableTest extends TestCase {
	
	public void testScriptRunnable() throws InterruptedException, BSFException {
		IScript script = new Script("$foo='bar'; puts 'hello'");
		BSFManager manager = new BSFManager();
		ScriptRunnable runnable = new ScriptRunnable(script, manager);
		runnable.run();
		
		// note this should return immediately
		runnable.join();
		Object fooObj = new Script("$foo").eval(manager);
		
		assertEquals(String.class, fooObj.getClass());
		assertEquals("bar", ((String)fooObj));
	}

	public void testScriptRunnableOtherThread() throws InterruptedException, BSFException {
		IScript script = new Script("$foo='bar'; puts 'hello'");
		BSFManager manager = new BSFManager();
		ScriptRunnable runnable = new ScriptRunnable(script, manager);
		Thread thread = new Thread(runnable);
		thread.start();
		
		// note this should return immediately
		runnable.join();
		Object fooObj = new Script("$foo").eval(manager);
		
		assertEquals(String.class, fooObj.getClass());
		assertEquals("bar", ((String)fooObj));
	}
}
