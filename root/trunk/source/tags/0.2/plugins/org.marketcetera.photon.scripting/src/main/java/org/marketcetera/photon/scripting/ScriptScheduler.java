package org.marketcetera.photon.scripting;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.bsf.BSFManager;

public class ScriptScheduler {
	ExecutorService pool = Executors.newFixedThreadPool(1);
	BSFManager manager = new BSFManager();
	
	
	public void submitScript(IScript script)
	{
		pool.execute(new ScriptRunnable(script, manager));
	}
	
	public void shutdown(){
		pool.shutdown();
	}
}
