package org.marketcetera.photon.scripting;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.jruby.bsf.JRubyPlugin;
import org.marketcetera.core.MMapEntry;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.preferences.ListEditorUtil;
import org.marketcetera.photon.preferences.ScriptRegistryPage;
import org.springframework.beans.factory.InitializingBean;

import quickfix.Message;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;


/**
 * Script registry implementation.
 * 
 * @author andrei@lissovski.org
 * @author gmiller
 */
public class ScriptRegistry implements InitializingBean {

	private static final String MESSAGE_BEAN_NAME = "message";
	public static final String RUBY_LANG_STRING = "ruby";
	protected BSFManager bsfManager;
	protected BSFEngine engine;
	private Classpath additionalClasspath =  new Classpath();
	private Classpath currentClasspath;
	
	
	static String [] JRUBY_PLUGIN_PATH = {
		"lib/ruby/site_ruby/1.8",
		"lib/ruby/site_ruby/1.8/java",
		"lib/ruby/site_ruby", 
		"lib/ruby/1.8", 
		"lib/ruby/1.8/java",
		"lib/active_support",
		"lib/active_support/active_support",
		"lib/active_support/active_support/vendor"
	};
	
	static String [] JRUBY_WORKSPACE_PATH = {
		""
	};
	
	public ScriptRegistry() 
	{
	}

	
	private void initBSFManager() throws BSFException {
		currentClasspath = new Classpath();
		currentClasspath.add(EclipseUtils.getPluginPath(PhotonPlugin.getDefault()).append("src").append("main").append("resources"));
		currentClasspath.add(ResourcesPlugin.getWorkspace().getRoot().getProject(PhotonPlugin.DEFAULT_PROJECT_NAME).getLocation());

		
		currentClasspath.addAll(additionalClasspath);
		IPath jrubyPluginPath = EclipseUtils.getPluginPath(JRubyPlugin.getDefault());
		PhotonPlugin.getMainConsoleLogger().debug("Using base path of JRuby plugin: "+jrubyPluginPath.toString());
		updateClasspath(currentClasspath, jrubyPluginPath, JRUBY_PLUGIN_PATH);
		updateClasspath(currentClasspath, EclipseUtils.getWorkspacePath(), JRUBY_WORKSPACE_PATH);
		String classpathString = currentClasspath.toString();
		bsfManager = new BSFManager();
		bsfManager.setClassPath(classpathString);

		engine = bsfManager.loadScriptingEngine("ruby");

		Object result;
		result = engine.eval("<java>", 1, 1, "require 'active_support/core_ext'");
		result = engine.eval("<java>", 1, 1, "require 'active_support/dependencies'");
		result = engine.eval("<java>", 1, 1, "require 'photon'");
	}
	

	public void setAdditionalClasspath(List<IPath> pluginPath) {
		additionalClasspath.addAll(pluginPath);
	}


	private void updateClasspath(List<IPath> pathElements, IPath pluginPath, String[] pathStrings) {
		for (String aPath : pathStrings) {
			pathElements.add(pluginPath.append(aPath));
		}
	}


	public void afterPropertiesSet() throws Exception {
		initBSFManager();

	}

	public boolean isScript(IPath resourcePath) {
		return resourcePath.getFileExtension().equalsIgnoreCase("rb");  //$NON-NLS-1$
	}


	/**
	 * Note that this method expects a file name, formatted as an
	 * argument to the Ruby "require" method.  For example, the workspace
	 * path "/foo/bar.rb", would correspond to the require method parameter,
	 * "foo/bar".
	 * 
	 * @param requireString the argument to the Ruby require method
	 * @return
	 * @throws BSFException
	 */
	public boolean isRegistered(String requireString) throws BSFException {
		String evalString = "Photon.is_registered?('"+requireString+"')";
		return (Boolean) bsfManager.eval(RUBY_LANG_STRING, "<java>", 1, 1, evalString);
	}
	
	/**
	 * Note that this method expects a file name, formatted as an
	 * argument to the Ruby "require" method.  For example, the workspace
	 * path "/foo/bar.rb", would correspond to the require method parameter,
	 * "foo/bar".
	 * 
	 * @param requireString the argument to the Ruby require method
	 * @return
	 * @throws BSFException
	 */
	public void unregister(String fileName) throws BSFException {
		String evalString = "Photon.unregister('"+fileName+"')";
		bsfManager.eval(RUBY_LANG_STRING, "<java>", 1, 1, evalString);
	}

	/**
	 * Note that this method expects a file name, formatted as an
	 * argument to the Ruby "require" method.  For example, the workspace
	 * path "/foo/bar.rb", would correspond to the require method parameter,
	 * "foo/bar".
	 * 
	 * @param requireString the argument to the Ruby require method
	 * @return
	 * @throws BSFException
	 */
	public void register(String fileName) throws BSFException {
		String evalString = "Photon.register('"+fileName+"')";
		bsfManager.eval(RUBY_LANG_STRING, "<java>", 1, 1, evalString);
	}

	/**
	 * Note that this method expects a file name, formatted as an
	 * argument to the Ruby "require" method.  For example, the workspace
	 * path "/foo/bar.rb", would correspond to the require method parameter,
	 * "foo/bar".
	 * 
	 * @param requireString the argument to the Ruby require method
	 * @return
	 * @throws BSFException
	 */
	public void scriptChanged(String fileName) throws BSFException {
		boolean reregister = isRegistered(fileName);
		String evalString = "Dependencies.loaded.reject! {|s| s.ends_with?('"+fileName+"')}";
		bsfManager.eval(RUBY_LANG_STRING, "<java>", 1, 1, evalString);
		evalString = "require_dependency '"+fileName+"'";
		bsfManager.eval(RUBY_LANG_STRING, "<java>", 1, 1, evalString);
		unregister(fileName);
		if (reregister){
			register(fileName);
		}
		
	}

	public void onEvent(Message message) throws BSFException {
		bsfManager.undeclareBean(MESSAGE_BEAN_NAME);
		bsfManager.declareBean(MESSAGE_BEAN_NAME, message, message.getClass());
		bsfManager.exec(RUBY_LANG_STRING, "<java>", 1, 1, "Photon.on_message($message)");
	}

	public void projectAdded(String absolutePath) {
		currentClasspath.add(0, absolutePath);
		bsfManager.setClassPath(currentClasspath.toString());
	}


	public void projectRemoved(String absolutePath) {
		currentClasspath.remove(absolutePath);
		bsfManager.setClassPath(currentClasspath.toString());
	}

		
}
