package org.rubypeople.rdt.internal.launching;

import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;


/**
 * @deprecated Please use the externally visible IRubyLaunchConfigurationConstants
 * 
 */
public interface RubyLaunchConfigurationAttribute {

	/**
	 * @deprecated Please use <code>IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME</code>.
	 */
	static final String SELECTED_INTERPRETER = IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME;

	/**
	 * @deprecated Please use <code>IRubyLaunchConfigurationConstants.ATTR_DEFAULT_LOADPATH</code>
	 */
	static final String USE_DEFAULT_LOAD_PATH = IRubyLaunchConfigurationConstants.ATTR_DEFAULT_LOADPATH;
	
	
	static final String USE_DEFAULT_WORKING_DIRECTORY = LaunchingPlugin.PLUGIN_ID + ".USE_DEFAULT_WORKING_DIRECTORY";
}