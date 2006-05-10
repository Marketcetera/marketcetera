package org.marketcetera.photon.model;

import org.eclipse.core.runtime.PlatformObject;

public abstract class PositionProgress extends PlatformObject {
	
	public abstract String getName();

	public abstract Portfolio getParent();
	
	public abstract double getProgress();
	
}
