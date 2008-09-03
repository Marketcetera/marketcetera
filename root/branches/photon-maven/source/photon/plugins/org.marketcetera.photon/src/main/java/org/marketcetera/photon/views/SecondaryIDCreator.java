package org.marketcetera.photon.views;

/**
 * Create secondary IDs for use in IWorkbenchPage.showView()
 */
public class SecondaryIDCreator {
	private int nextSecondaryID = 0;
	
	public String getNextSecondaryID() {
		return Integer.toString(nextSecondaryID++);
	}
}
