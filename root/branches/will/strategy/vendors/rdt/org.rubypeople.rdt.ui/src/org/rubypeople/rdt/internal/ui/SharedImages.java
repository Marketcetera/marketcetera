package org.rubypeople.rdt.internal.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.rubypeople.rdt.ui.ISharedImages;

public class SharedImages implements ISharedImages {
	public SharedImages() {}

	/*
	 * (Non-Javadoc) Method declared in ISharedImages
	 */
	public Image getImage(String key) {
		return RubyPluginImages.get(key);
	}

	/*
	 * (Non-Javadoc) Method declared in ISharedImages
	 */
	public ImageDescriptor getImageDescriptor(String key) {
		return RubyPluginImages.getDescriptor(key);
	}
}
