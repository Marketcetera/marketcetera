/**
 * 
 */
package org.rubypeople.rdt.ui;

/**
 * Externally facing interface holding constants for various UI elements. Helps
 * people extend our plugin!
 * 
 */
public interface IRubyConstants {

	/**
	 * @deprecated Please use RubyUI.ID_RUBY_EDITOR
	 */
	public static final String EDITOR_ID = RubyUI.ID_RUBY_EDITOR; //$NON-NLS-1$
	
	/**
	 * @deprecated Please use RubyUI.ID_EXTERNAL_EDITOR
	 */
	public static final String EXTERNAL_FILES_EDITOR_ID = RubyUI.ID_EXTERNAL_EDITOR; //$NON-NLS-1$
	
	public static final String RI_VIEW_ID = "org.rubypeople.rdt.ui.views.RIView"; //$NON-NLS-1$
	
	public static final String ID_NEW_CLASS_WIZARD = "org.rubypeople.rdt.ui.wizards.RubyNewClassWizard"; //$NON-NLS-1$

}
