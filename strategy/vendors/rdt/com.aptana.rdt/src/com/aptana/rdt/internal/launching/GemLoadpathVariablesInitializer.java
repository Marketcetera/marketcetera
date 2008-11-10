package com.aptana.rdt.internal.launching;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.rubypeople.rdt.core.LoadpathVariableInitializer;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.util.Util;
import org.rubypeople.rdt.internal.launching.LaunchingPlugin;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallChangedListener;
import org.rubypeople.rdt.launching.PropertyChangeEvent;
import org.rubypeople.rdt.launching.RubyRuntime;

import com.aptana.rdt.AptanaRDTPlugin;
import com.aptana.rdt.core.gems.IGemManager;
import com.aptana.rdt.launching.IGemRuntime;

public class GemLoadpathVariablesInitializer extends LoadpathVariableInitializer implements IVMInstallChangedListener {

	private static final String LEOPARD_GEM_PATH_1 = "/System/Library/Frameworks/Ruby.framework/Versions/1.8/usr/lib/ruby/gems/1.8";
	private static final String LEOPARD_GEM_PATH_2 = "/Library/Ruby/Gems/1.8";
	private IProgressMonitor fMonitor;
	
	public GemLoadpathVariablesInitializer() {
		RubyRuntime.addVMInstallChangedListener(this);
	}

	@Override
	public void initialize(final String variable) {	
		if (!variable.equals(IGemRuntime.GEMLIB_VARIABLE)) return;		
		IVMInstall vmInstall = RubyRuntime.getDefaultVMInstall();
		if (vmInstall != null) {
			// Return a "quick 'n dirty" guess, then run a job that sets the real value later
			setQuickNDirtyPaths(variable, vmInstall);

			Job realJob = new Job("") {
			
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					IGemManager gemManager = AptanaRDTPlugin.getDefault().getGemManager();
					List<IPath> gemPaths = null;
					int tries = 3; // Try to grab install path up to 3 times
					while (tries > 0) {
						gemPaths = gemManager.getGemInstallPaths();
						if (gemPaths != null) break;
						tries--;
					}
					if (gemPaths == null) {
						gemPaths = loadCachedValue();
					} else {
						saveValue(gemPaths);
					}
					if (gemPaths == null) {
						return new Status(Status.ERROR, AptanaRDTPlugin.PLUGIN_ID, -1, "Unable to retrieve gem install paths", null);
					}
					IPath[] paths = new IPath[gemPaths.size()];
					int i = 0;
					for (IPath path : gemPaths) {
						paths[i++] = path.append("gems");
					}
					// Fix this for cygwin (need to resolve location to actual filesystem)
					if (RubyRuntime.currentVMIsCygwin()) {
						File home = RubyRuntime.getDefaultVMInstall().getInstallLocation();
						for (int x = 0; x < paths.length; x++) {
							// if starts with /usr/lib, convert to lib
							// TODO Run "mount" to figure out mappings?
							String portablePath = paths[x].toOSString();
							if (portablePath.startsWith("\\usr\\lib")) {
								portablePath = portablePath.substring(4);
							}
							String cygwinConverted = home.getAbsolutePath() + portablePath;
							IPath path = new Path(cygwinConverted);
							paths[x] = path;
						}
					}
					setVariable(variable, paths);					
					return Status.OK_STATUS;
				}

				private void saveValue(List<IPath> gemPaths) {
					File file = getCacheFile();					
					FileWriter writer = null;
					try {
						if (!file.exists()) file.createNewFile();
						writer = new FileWriter(file);
						for (IPath gemPath : gemPaths) {
							writer.write(gemPath.toPortableString() + "\n");
						}
					} catch (IOException e) {
						AptanaRDTPlugin.log(e);
					} finally {
						try {
							if (writer != null)
								writer.close();
						} catch (IOException e) {
							// ignore
						}
					}					
				}

				private File getCacheFile() {
					IPath path = AptanaRDTPlugin.getDefault().getStateLocation().append(RubyRuntime.getDefaultVMInstall().getId() + "_gem_install_paths.txt");
					return path.toFile();
				}

				private List<IPath> loadCachedValue() {
					List<IPath> paths = new ArrayList<IPath>();
					try {
						String contents = new String(Util.getFileCharContent(getCacheFile(), null));
						String[] rawPaths = contents.split("\n");
						for (int i = 0; i < rawPaths.length; i++) {
							paths.add(Path.fromPortableString(rawPaths[i]));
						}
					} catch (IOException e) {
						AptanaRDTPlugin.log(e);
					}
					return paths;
				}
			
			};
			realJob.setSystem(true);
			realJob.setPriority(Job.LONG);
			realJob.schedule();			
		}
	}

	private void setQuickNDirtyPaths(final String variable, IVMInstall vmInstall) {
		if (Platform.getOS().equals(Platform.OS_MACOSX) && !RubyRuntime.currentVMIsJRuby()) { // Special quick and dirty paths for Mac OSX Leopard!
			File dir = new File(LEOPARD_GEM_PATH_2);
			if (dir.exists()) {
				setVariable(variable, new IPath[] {new Path(LEOPARD_GEM_PATH_1), new Path(LEOPARD_GEM_PATH_2)});
				return;
			}
		}
		IPath quickNDirty = new Path(vmInstall.getInstallLocation().getAbsolutePath()).append("lib").append("ruby").append("gems").append("1.8").append("gems");
		setVariable(variable, new IPath[] {quickNDirty});
	}

	private void setVariable(String variable, IPath newPath[]) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceDescription wsDescription = workspace
				.getDescription();
		boolean wasAutobuild = wsDescription.isAutoBuilding();
		try {
			setAutobuild(workspace, false);
			setRubyVMVariable(newPath, variable);
		} catch (CoreException ce) {
			LaunchingPlugin.log(ce);
			return;
		} finally {
			try {
				setAutobuild(workspace, wasAutobuild);
			} catch (CoreException ce) {
				LaunchingPlugin.log(ce);
			}
		}
	}

	private void setRubyVMVariable(IPath[] newPath, String var)
			throws CoreException {
		RubyCore.setLoadpathVariable(var, newPath, getMonitor());
	}

	private boolean setAutobuild(IWorkspace ws, boolean newState)
			throws CoreException {
		IWorkspaceDescription wsDescription = ws.getDescription();
		boolean oldState = wsDescription.isAutoBuilding();
		if (oldState != newState) {
			wsDescription.setAutoBuilding(newState);
			ws.setDescription(wsDescription);
		}
		return oldState;
	}

	protected IProgressMonitor getMonitor() {
		if (fMonitor == null) {
			return new NullProgressMonitor();
		}
		return fMonitor;
	}

	public void defaultVMInstallChanged(IVMInstall previous, IVMInstall current) {
		initialize(IGemRuntime.GEMLIB_VARIABLE);		
	}

	public void vmAdded(IVMInstall newVm) {		
	}

	public void vmChanged(PropertyChangeEvent event) {		
	}

	public void vmRemoved(IVMInstall removedVm) {		
	}

}
