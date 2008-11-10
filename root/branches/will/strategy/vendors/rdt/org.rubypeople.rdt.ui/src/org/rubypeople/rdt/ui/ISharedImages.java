package org.rubypeople.rdt.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;

public interface ISharedImages {
	/**
	 * Key to access the shared image or image descriptor for a library (load path container).
	 * @since 0.9.0
	 */
	public static final String IMG_OBJS_LIBRARY= RubyPluginImages.IMG_OBJS_LIBRARY;

	/**
	 * Key to access the shared image or image descriptor for a source folder root.
	 * @since 1.0.0
	 */
	public static final String IMG_OBJS_SOURCE_FOLDER_ROOT= RubyPluginImages.IMG_OBJS_SOURCE_FOLDER_ROOT;
	
	/**
	 * Key to access the shared image or image descriptor for a loadpath variable entry.
	 * @since 1.0.0
	 */
	public static final String IMG_OBJS_LOADPATH_VAR_ENTRY= RubyPluginImages.IMG_OBJS_ENV_VAR;
	
	/** 
	 * Key to access the shared image or image descriptor for external archives with source.
	 * @since 1.0.0
	 */
	public static final String IMG_OBJS_EXTERNAL_ARCHIVE_WITH_SOURCE= RubyPluginImages.IMG_OBJS_EXTJAR_WSRC;
	
	/** 
	 * Key to access the shared image or image descriptor for correction changes.
	 * @since 1.0.0
	 */
	public static final String IMG_OBJS_CORRECTION_CHANGE= RubyPluginImages.IMG_OBJS_CORRECTION_CHANGE;

	public static final String IMG_OBJS_CLASS = RubyPluginImages.IMG_OBJS_CLASS;

	public static final String IMG_OBJS_SOURCE_FOLDER = RubyPluginImages.IMG_OBJS_SOURCE_FOLDER;

	public static final String IMG_MISC_PUBLIC_METHOD = RubyPluginImages.IMG_MISC_PUBLIC;

	
	Image getImage(String key);

	ImageDescriptor getImageDescriptor(String key);

}
