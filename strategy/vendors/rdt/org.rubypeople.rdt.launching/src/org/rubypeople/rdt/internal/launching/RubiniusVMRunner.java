package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.VMRunnerConfiguration;

public class RubiniusVMRunner extends StandardVMRunner {
	
	@Override
	protected void addStreamSync(List<String> arguments) {
		// do nothing
	}
	
	@Override
	protected List<String> constructProgramString(VMRunnerConfiguration config) throws CoreException {
		List<String> string = new ArrayList<String>();
		if (!Platform.getOS().equals(Platform.OS_WIN32) && config.isSudo()) {
			string.add("sudo");
			String password = Sudo.getPassword(config.getSudoMessage());
			if (password != null && password.trim().length() > 0) {
				string.add("-S");
				string.add(password);
			}
		}
		
		// Look for the user-specified ruby executable command
		String command= null;
		Map map= config.getVMSpecificAttributesMap();
		if (map != null) {
			command = (String)map.get(IRubyLaunchConfigurationConstants.ATTR_RUBY_COMMAND);
		}
		
		// If no ruby command was specified, use default executable
		if (command == null) {
			File exe = fVMInstance.getVMInstallType().findExecutable(fVMInstance.getInstallLocation());
			if (exe == null) {
				abort(MessageFormat.format(LaunchingMessages.StandardVMRunner_Unable_to_locate_executable_for__0__1, fVMInstance.getName()), null, IRubyLaunchConfigurationConstants.ERR_INTERNAL_ERROR); 
			}
			string.add(exe.getAbsolutePath());
			return string;
		}
				
		// Build the path to the ruby executable.
		String installLocation = fVMInstance.getInstallLocation().getAbsolutePath() + File.separatorChar;
		File exe = new File(installLocation + "bin" + File.separatorChar + command); //$NON-NLS-1$ 		
		if (fileExists(exe)){
			string.add(exe.getAbsolutePath());
			return string;
		}
		exe = new File(exe.getAbsolutePath() + ".exe"); //$NON-NLS-1$
		if (fileExists(exe)){
			string.add(exe.getAbsolutePath());
			return string;
		}
		// HACK FIXME This is just to allow for jruby!
		String path = installLocation + "bin" + File.separatorChar + "j" + command; //$NON-NLS-1$  //$NON-NLS-2$
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			exe = new File(path + ".bat"); //$NON-NLS-1$ 	
			if (fileExists(exe)){
				string.add(exe.getAbsolutePath());
				return string;
			}
		} else {
			exe = new File(path);
			if (fileExists(exe)){
				string.add(exe.getAbsolutePath());
				return string;
			}
		}
		
		// not found
		abort(MessageFormat.format(LaunchingMessages.StandardVMRunner_Specified_executable__0__does_not_exist_for__1__4, command, fVMInstance.getName()), null, IRubyLaunchConfigurationConstants.ERR_INTERNAL_ERROR); 
		// NOTE: an exception will be thrown - null cannot be returned
		return null;		
	}

	@Override
	protected String[] getEnvironment(VMRunnerConfiguration config) {
		return config.getEnvironment();
	}
}
