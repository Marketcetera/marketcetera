package com.aptana.rdt.core.gems;

public interface GemListener {
	public void gemsRefreshed();
	public void gemAdded(Gem gem);
	public void gemRemoved(Gem gem);
	public void managerInitialized();
}
