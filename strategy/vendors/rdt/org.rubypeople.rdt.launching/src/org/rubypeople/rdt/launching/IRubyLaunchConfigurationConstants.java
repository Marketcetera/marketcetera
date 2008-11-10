package org.rubypeople.rdt.launching;

import org.rubypeople.rdt.internal.launching.LaunchingPlugin;

public interface IRubyLaunchConfigurationConstants {
	
	/**
	 * Identifier for the Local Ruby Application launch configuration type
	 * (value <code>"org.rubypeople.rdt.launching.LaunchConfigurationTypeRubyApplication"</code>).
	 */
	public static final String ID_RUBY_APPLICATION = LaunchingPlugin.getUniqueIdentifier() + ".LaunchConfigurationTypeRubyApplication"; //$NON-NLS-1$
		
	/**
	 * Status code indicating a launch configuration does not
	 * specify a file to launch.
	 */
	public static final int ERR_UNSPECIFIED_FILE_NAME = 101;	
	
	/**
	 * Status code indicating a launch configuration does not
	 * specify a VM Install
	 */
	public static final int ERR_UNSPECIFIED_VM_INSTALL = 103;
	
	/**
	 * Status code indicating a launch configuration's VM install
	 * could not be found.
	 */
	public static final int ERR_VM_INSTALL_DOES_NOT_EXIST = 105;
	
	/**
	 * Status code indicating a VM runner could not be located
	 * for the VM install specified by a launch configuration.
	 */
	public static final int ERR_VM_RUNNER_DOES_NOT_EXIST = 106;	
	
	/**
	 * Status code indicating the project associated with
	 * a launch configuration is not a Ruby project.
	 */
	public static final int ERR_NOT_A_RUBY_PROJECT = 107;

	/**
	 * Status code indicating the specified working directory
	 * does not exist.
	 */
	public static final int ERR_WORKING_DIRECTORY_DOES_NOT_EXIST = 108;	
	
	/**
	 * Status code indicating that a free socket was not available to
	 * communicate with the VM.
	 */
	public static final int ERR_NO_SOCKET_AVAILABLE = 118;	
	
	/**
	 * Status code indicating that the debugger failed to connect
	 * to the VM.
	 */
	public static final int ERR_CONNECTION_FAILED = 120;	
	
	/**
	 * Status code indicating that the project referenced by a launch configuration
	 * is closed.
	 * 
	 * @since 0.9.0
	 */
	public static final int ERR_PROJECT_CLOSED = 124;	
	
	/**
	 * Status code indicating an unexpected internal error.
	 */
	public static final int ERR_INTERNAL_ERROR = 150;

	/**
	 * Identifier for the ruby process type, which is annotated on processes created
	 * by the local ruby application launch delegate.
	 * 
	 * (value <code>"ruby"</code>).
	 */
	public static final String ID_RUBY_PROCESS_TYPE = "ruby"; //$NON-NLS-1$ 

	/**
	 * Attribute key for VM specific attributes found in the
	 * <code>ATTR_VM_INSTALL_TYPE_SPECIFIC_ATTRS_MAP</code>. The value is a String,
	 * indicating the String to use to invoke the Ruby VM.
	 */
	public static final String ATTR_RUBY_COMMAND = LaunchingPlugin.getUniqueIdentifier() + ".RUBY_COMMAND";	 //$NON-NLS-1$

	/**
	 * Launch configuration attribute key. The value is a name of
	 * a Ruby project associated with a Ruby launch configuration.
	 */
	public static final String ATTR_PROJECT_NAME = LaunchingPlugin.getUniqueIdentifier() + ".PROJECT_NAME"; //$NON-NLS-1$
	

	/**
	 * Launch configuration attribute key. The value is a path identifying the RubyVM used
	 * when launching a local VM. The path is a loadpath container corresponding
	 * to the <code>RubyRuntime.RUBY_CONTAINER</code> loadpath container.
	 * <p>
	 * When unspecified the default RubyVM for a launch configuration is used (which is the
	 * RubyVM associated with the project being launched, or the workspace default RubyVM when
	 * no project is associated with a configuration). The default RubyVM loadpath container
	 * refers explicitly to the workspace default RubyVM.
	 * </p>
	 * @since 0.9.0
	 */
	public static final String ATTR_RUBY_CONTAINER_PATH = RubyRuntime.RUBY_CONTAINER;
	
	/**
	 * Launch configuration attribute key. The value is a name of a VM install
	 * to use when launching a local VM. This attribute must be qualified
	 * by a VM install type, via the <code>ATTR_VM_INSTALL_TYPE</code>
	 * attribute. When unspecified, the default VM is used.
	 * 
	 * @deprecated use <code>ATTR_RUBY_CONTAINER_PATH</code>
	 */
	public static final String ATTR_VM_INSTALL_NAME = LaunchingPlugin.getUniqueIdentifier() + ".VM_INSTALL_NAME"; //$NON-NLS-1$
		
	/**
	 * Launch configuration attribute key. The value is an identifier of
	 * a VM install type. Used in conjunction with a VM install name, to 
	 * specify the VM to use when launching a local Java application.
	 * The associated VM install name is specified via the attribute
	 * <code>ATTR_VM_INSTALL_NAME</code>.
	 * 
	 * @deprecated use <code>ATTR_RUBY_CONTAINER_PATH</code>
	 */
	public static final String ATTR_VM_INSTALL_TYPE = LaunchingPlugin.getUniqueIdentifier() + ".VM_INSTALL_TYPE_ID"; //$NON-NLS-1$

	/**
	 * Launch configuration attribute key. The value is a string specifying
	 * program arguments for a Ruby launch configuration, as they should appear
	 * on the command line.
	 */
	public static final String ATTR_PROGRAM_ARGUMENTS = LaunchingPlugin.getUniqueIdentifier() + ".PROGRAM_ARGUMENTS"; //$NON-NLS-1$
		
	/**
	 * Launch configuration attribute key. The value is a string specifying
	 * VM arguments for a Ruby launch configuration, as they should appear
	 * on the command line.
	 */
	public static final String ATTR_VM_ARGUMENTS = LaunchingPlugin.getUniqueIdentifier() + ".INTERPRETER_ARGUMENTS";	 //$NON-NLS-1$
	
	/**
	 * Launch configuration attribute key. The value is a string specifying a
	 * path to the working directory to use when launching a local VM.
	 * When specified as an absolute path, the path represents a path in the local
	 * file system. When specified as a full path, the path represents a workspace
	 * relative path. When unspecified, the working directory defaults to the project
	 * associated with a launch configuration. When no project is associated with a
	 * launch configuration, the working directory is inherited from the current
	 * process.
	 */
	public static final String ATTR_WORKING_DIRECTORY = LaunchingPlugin.getUniqueIdentifier() + ".WORKING_DIRECTORY";	 //$NON-NLS-1$
	
	/**
	 * Launch configuration attribute key. The value is a Map of attributes specific
	 * to a particular VM install type, used when launching a local Ruby
	 * application. The map is passed to a <code>VMRunner</code> via a <code>VMRunnerConfiguration</code>
	 * when launching a VM. The attributes in the map are implementation dependent
	 * and are limited to String keys and values.
	 */
	public static final String ATTR_VM_INSTALL_TYPE_SPECIFIC_ATTRS_MAP = LaunchingPlugin.getUniqueIdentifier() + "VM_INSTALL_TYPE_SPECIFIC_ATTRS_MAP"; //$NON-NLS-1$

	/**
	 * Launch configuration attribute key. The value is a fully qualified name
	 * of a file to launch.
	 */
	public static final String ATTR_FILE_NAME = LaunchingPlugin.getUniqueIdentifier() + ".FILE_NAME";	 //$NON-NLS-1$

	/**
	 * Launch configuration attribute key. The value is an identifier of a
	 * loadpath provider extension used to compute the loadpath
	 * for a launch configuration. When unspecified, the default loadpath
	 * provider is used - <code>StandardLoadpathProvider</code>.
	 */
	public static final String ATTR_LOADPATH_PROVIDER = LaunchingPlugin.getUniqueIdentifier() + ".LOADPATH_PROVIDER";	 //$NON-NLS-1$

	/**
	 * Launch configuration attribute key. The value is a boolean specifying
	 * whether a default loadpath should be used when launching a local
	 * Ruby application. When <code>false</code>, a loadpath must be specified
	 * via the <code>ATTR_LOADPATH</code> attribute. When <code>true</code> or
	 * unspecified, a loadpath is computed by the loadpath provider associated
	 * with a launch configuration.
	 */
	public static final String ATTR_DEFAULT_LOADPATH = LaunchingPlugin.getUniqueIdentifier() + ".USE_DEFAULT_LOAD_PATH"; //$NON-NLS-1$

	/**
	 * Launch configuration attribute key. The attribute value is an ordered list of strings
	 * which are mementos for runtime class path entries. When unspecified, a default
	 * loadpath is generated by the loadpath provider associated with a launch
	 * configuration (via the <code>ATTR_LOADPATH_PROVIDER</code> attribute).
	 */
	public static final String ATTR_LOADPATH = LaunchingPlugin.getUniqueIdentifier() + ".CUSTOM_LOAD_PATH";	 //$NON-NLS-1$	

	public static final String ATTR_IS_SUDO = LaunchingPlugin.getUniqueIdentifier() + ".IS_SUDO";	 //$NON-NLS-1$

	/**
	 * After we finish the process, do we need to refresh the project's contents?
	 */
	public static final String ATTR_REQUIRES_REFRESH = LaunchingPlugin.getUniqueIdentifier() + ".REQUIRES_REFRESH"; //$NON-NLS-1$
	
	/**
	 * Should we force a process console not to open?
	 */
	public static final String ATTR_FORCE_NO_CONSOLE = LaunchingPlugin.getUniqueIdentifier() + ".FORCE_NO_CONSOLE"; //$NON-NLS-1$
	
	/**
	 * Aattach the process to a given terminal by it's ID/type
	 */
	public static final String ATTR_USE_TERMINAL = LaunchingPlugin.getUniqueIdentifier() + ".USE_TERMINAL"; //$NON-NLS-1$

	/**
	 * The command to print out to the terminal command line
	 */
	public static final String ATTR_TERMINAL_COMMAND = LaunchingPlugin.getUniqueIdentifier() + ".TERMINAL_COMMAND"; //$NON-NLS-1$

	/**
	 * Explanatory text for password dialog that pops. (Password for sudo).
	 */
	public static final String ATTR_SUDO_MESSAGE = LaunchingPlugin.getUniqueIdentifier() + ".SUDO_MESSAGE";	 //$NON-NLS-1$
	
}
