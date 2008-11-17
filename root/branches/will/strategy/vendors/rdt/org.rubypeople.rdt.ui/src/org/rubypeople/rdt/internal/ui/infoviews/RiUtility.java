package org.rubypeople.rdt.internal.ui.infoviews;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.swt.SWT;
import org.rubypeople.rdt.core.IRubyInformation;
import org.rubypeople.rdt.internal.launching.LaunchingPlugin;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.rubypeople.rdt.ui.text.ansi.ANSIParser;
import org.rubypeople.rdt.ui.text.ansi.ANSIToken;

import com.aptana.rdt.AptanaRDTPlugin;

public class RiUtility implements IRubyInformation {
	
	private final static String HEADER = "<html><head></head><body>";
	private final static String TAIL = "</body></html>";
	
	private static final String FASTRI_INDEX = ".fastri-index";
	
	public String getDocs(String token) {
		List<String> args = new ArrayList<String>();
		args.add(token);
		return getRIContents(args);
	}
	
	public static String getRIContents(List<String> args) {
		File file = getFRIIndexFile();
    	if (!file.exists()) {
    		buildIndex();
    	}
    	args.add(0, "-L");
		return execAndReadOutput(getFastRiPath(), args);	
	}
	
	public static String getRIHTMLContents(List<String> args) {
		String result = getRIContents(args);
		if (result == null) return result;
		
		StringBuilder buffer = new StringBuilder();
		buffer.append(checkForANSIColors(escapeHTML(result)));
		buffer.insert(0, HEADER); // Put the header before all the contents
		buffer.append(TAIL); // Put the body and html close tags at end
		return buffer.toString();
	}
	
	private static String checkForANSIColors(String string) {
		StringBuilder buffer = new StringBuilder();
		List<ANSIToken> tokens = getParser(string).parse(string);
		for (ANSIToken token : tokens) {
			String endTag = "";
			if (token.hasFontStyle()) {					
				if (token.getFontStyle() == SWT.BOLD) {
					buffer.append("<b>");
					endTag = "</b>";
				} else if (token.getFontStyle() == SWT.ITALIC) {
					buffer.append("<i>");
					endTag = "</i>";
				}
			}
			if (token.hasForegroundColor()) {					
				buffer.append("<span style=\"color: #");
				buffer.append(pad(Integer.toHexString(token.getForegroundRGB().red)));
				buffer.append(pad(Integer.toHexString(token.getForegroundRGB().green)));
				buffer.append(pad(Integer.toHexString(token.getForegroundRGB().blue)));
				buffer.append("\">");
				endTag = "</span>" + endTag;
			}
			buffer.append(token.toString());
			buffer.append(endTag);
		}
		return buffer.toString();
	}

	private static ANSIParser getParser(String content) {
		if (content.indexOf(ANSIParser.ESC) != -1) return new ANSIParser();
		if (Platform.getOS().equals(Platform.OS_WIN32) && !RubyRuntime.currentVMIsCygwin() && !RubyRuntime.currentVMIsJRuby()) return new FastRIParser();
		return new ANSIParser();
	}

	private static String pad(String hexString) {
		if (hexString.length() == 1) return "0" + hexString;
		return hexString;
	}

	private static String escapeHTML(final String content) {
		String escaped = content.replace("&", "&amp;");
		escaped = escaped.replace("<", "&lt;");
		escaped = escaped.replace(">", "&gt;");
		escaped = escaped.replace("\r\n", "\n");
		escaped = escaped.replace("\r", "\n");
		escaped = escaped.replace("\n", "<br/>");
		return escaped;
	}

	static void rebuildIndex() {
		File file = getFRIIndexFile();
		file.delete();
		buildIndex();
	}
	
	static void buildIndex() {
		List<String> commands = new ArrayList<String>();
		commands.add("-b");
		String output = execAndReadOutput(getFastRiServerPath(), commands);
	}
	
	private static File getFRIIndexFile() {
		if (RubyRuntime.currentVMIsCygwin()) {
			return new File(RubyRuntime.getDefaultVMInstall().getInstallLocation(), FASTRI_INDEX);
		}
    	String homePath = System.getProperty("user.home");
    	return new File(homePath + File.separator + FASTRI_INDEX);
	}
	
	private static String getFastRiPath() {
		File file = LaunchingPlugin.getFileInPlugin(new Path("ruby/fri"));
		if (file == null || !file.exists() || !file.isFile()) return null;
		return file.getAbsolutePath();
	}
	    
	private static String getFastRiServerPath() {
		File file = LaunchingPlugin.getFileInPlugin(new Path("ruby/fastri-server"));
		if (file == null || !file.exists() || !file.isFile()) return null;
		return file.getAbsolutePath();
	}
	
	private static ILaunchConfigurationType getRubyApplicationConfigType() {
		return getLaunchManager().getLaunchConfigurationType(
				IRubyLaunchConfigurationConstants.ID_RUBY_APPLICATION);
	}

	private static ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}
    
	private synchronized static String execAndReadOutput(String file, List<String> commands) {
		ILaunchConfiguration config = createConfiguration(file, listToCommandLine(commands));
		File output = RubyPlugin.getDefault().getStateLocation().append("fastri_output.txt").toFile();
		return launchInBackgroundAndRead(config, output);		
	}

	private static String listToCommandLine(List<String> commands) {
		String arguments = "";
		for (String command : commands) {
			arguments += command;
			arguments += " ";
		}
		if (arguments.length() > 0)
			arguments = arguments.substring(0, arguments.length() - 1);
		return arguments;
	}

	private static ILaunchConfiguration createConfiguration(String file, String arguments) {
		ILaunchConfiguration config = null;
		try {
			ILaunchConfigurationType configType = getRubyApplicationConfigType();
			ILaunchConfigurationWorkingCopy wc = configType
					.newInstance(null, getLaunchManager()
							.generateUniqueLaunchConfigurationNameFrom(file));
			wc.setAttribute(IRubyLaunchConfigurationConstants.ATTR_FILE_NAME,
					file);
			wc.setAttribute(
					IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME,
					RubyRuntime.getDefaultVMInstall().getName());
			wc.setAttribute(
					IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE,
					RubyRuntime.getDefaultVMInstall().getVMInstallType()
							.getId());
			wc.setAttribute(IRubyLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, arguments);
			wc.setAttribute(IRubyLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,"");
			Map<String, String> map = new HashMap<String, String>();
			map.put(IRubyLaunchConfigurationConstants.ATTR_RUBY_COMMAND, "ruby");
			wc
					.setAttribute(
							IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE_SPECIFIC_ATTRS_MAP,
							map);
			wc.setAttribute(IDebugUIConstants.ATTR_PRIVATE, true);
			wc.setAttribute(IRubyLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, LaunchingPlugin.getFileInPlugin(new Path("ruby")).getAbsolutePath());
			wc.setAttribute(IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND, false);
			config = wc.doSave();
		} catch (CoreException ce) {
			// ignore for now
		}
		return config;
	}
	
	private static String launchInBackgroundAndRead(ILaunchConfiguration config, File file) {
		final StringBuffer buf = new StringBuffer();
		try {
				ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
				wc.setAttribute(IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND, true);
				wc.setAttribute(IDebugUIConstants.ATTR_CAPTURE_IN_CONSOLE, false);
				wc.setAttribute(IDebugUIConstants.ATTR_CAPTURE_IN_FILE, file.getAbsolutePath());
				wc.setAttribute(IRubyLaunchConfigurationConstants.ATTR_FORCE_NO_CONSOLE, true);
				config = wc.doSave();
				
				ILaunch launch = config.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());
				IProcess iproc = launch.getProcesses()[0];

				IStreamMonitor stdOut = iproc.getStreamsProxy().getOutputStreamMonitor();
				stdOut.addListener(new IStreamListener() {

					public void streamAppended(final String text,
							IStreamMonitor monitor) {
						buf.append(text);
					}

				});				
				while (!launch.isTerminated()) {
					Thread.yield();
				}
				if (buf.toString().trim().length() == 0) { // if we didn't get anything out of the listener, try reading the output file
					buf.append(readFile(file));
				}			
				return buf.toString();
		} catch (Exception e) {
			AptanaRDTPlugin.log(e);
		}
		return null;
	}
	
	private static String readFile(File file) {
		StringBuffer buf = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buf.append(line);
				buf.append("\n");
			}
		} catch (FileNotFoundException e) {
			AptanaRDTPlugin.log(e);
		} catch (IOException e) {
			AptanaRDTPlugin.log(e);
		} finally {
			try {
				if (reader != null) reader.close();
			} catch (IOException e) {
				// ignore
			}
		}
		return buf.toString();
	}

}
