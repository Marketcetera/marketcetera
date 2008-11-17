package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.rubypeople.rdt.core.IRubyInformation;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.util.Util;
import org.rubypeople.rdt.launching.RubyRuntime;

class CoreStubDocInsertingJob extends Job {

	public CoreStubDocInsertingJob() {
		super("Add docs to core stubs");
	}

	protected IStatus run(IProgressMonitor monitor) {
		File file = getCoreStubsDir();
		if (file == null) return Status.CANCEL_STATUS;
		File[] scripts = file.listFiles();
		// TODO Traverse all the files and fiddle with the contents to
		// insert docs!
		for (int x = 0; x < scripts.length; x++) {
			insertDocs(scripts[x]);
		}
		return Status.OK_STATUS;
	}

	private void insertDocs(File file) {
		try {
			char[] contents = Util.getFileCharContent(file, null);
			String raw = new String(contents);
			StringBuffer modified = new StringBuffer(raw);
			Pattern p = Pattern.compile("class (\\w+)"); // FIXME This is picking up code samples and stuff!
			Matcher m = p.matcher(raw);
			while (m.find()) {
				String className = m.group(1);
				if (Character.isLowerCase(className.charAt(0))) continue;
				String originalName = file.getName().substring(0, file.getName().length() - 3);
				if (!className.toLowerCase().equals(originalName)) continue;
				int offset = m.start();
				String before = raw.substring(0, offset).trim();
				if (before.endsWith("=end")) continue; // Already has docs for type
				String docs = getRI(className);
				modified.insert(offset, "=begin\n" + docs + "=end\n");
//				System.out.println(docs);
			}
			if (modified.length() != raw.length())
				write(file, modified);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private void write(File file, StringBuffer modified) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(modified.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	private String getRI(String type) {
		String riResult = getRubyInformation().getDocs(type);
		if (riResult.trim().equals("nil")) return null;		
		return riResult;
	}

	private IRubyInformation getRubyInformation() {
		return LaunchingPlugin.getRubyInformation();
	}

	private File getCoreStubsDir() {
		IPath[] paths = RubyCore.getLoadpathVariable(RubyRuntime.RUBYLIB_VARIABLE);
		for (int i = 0; i < paths.length; i++) {
			IPath path = paths[i];
			if (path.toPortableString().contains(".metadata")) {
				return path.toFile();
			}
		}
		return null;
	}
}
