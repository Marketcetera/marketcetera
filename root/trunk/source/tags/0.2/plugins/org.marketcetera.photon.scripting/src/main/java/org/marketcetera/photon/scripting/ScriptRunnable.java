package org.marketcetera.photon.scripting;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

public class ScriptRunnable implements Runnable {

	IScript script;
	CountDownLatch doneSignal = new CountDownLatch(1);
	private final BSFManager manager;
	
	public ScriptRunnable(IScript script, BSFManager manager) {
		super();
		this.script = script;
		this.manager = manager;
	}

	public void run() {
		try {
			script.exec(manager);
		} catch (BSFException e) {
			e.printStackTrace();
		} finally {
			doneSignal.countDown();
		}
	}
	
	public void join() throws InterruptedException
	{
		doneSignal.await();
	}
	
	public void join(long timeout, TimeUnit units) throws InterruptedException{
		doneSignal.await(timeout, units);
	}

}
