package org.rubypeople.rdt.internal.core.search.indexing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.internal.compiler.util.SimpleLookupTable;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.index.Index;
import org.rubypeople.rdt.internal.core.search.BasicSearchEngine;
import org.rubypeople.rdt.internal.core.search.ERBSearchDocument;
import org.rubypeople.rdt.internal.core.search.RubySearchDocument;
import org.rubypeople.rdt.internal.core.search.processing.JobManager;
import org.rubypeople.rdt.internal.core.util.Util;

public class AddExternalFolderToIndex extends IndexRequest {

	public AddExternalFolderToIndex(IPath containerPath, IndexManager manager) {
		super(containerPath, manager);
	}

	public boolean execute(IProgressMonitor progressMonitor) {

		if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled())
			return true;

		try {
			// if index is already cached, then do not perform any check
			// MUST reset the IndexManager if a jar file is changed
			Index index = this.manager.getIndexForUpdate(this.containerPath, false, /* do not reuse index file */false /* do not create if none */);
			if (index != null) {
				if (JobManager.VERBOSE)
					org.rubypeople.rdt.internal.core.util.Util.verbose("-> no indexing required (index already exists) for " + this.containerPath); //$NON-NLS-1$
				return true;
			}

			index = this.manager.getIndexForUpdate(this.containerPath, true, /* reuse index file */true /* create if none */);
			if (index == null) {
				if (JobManager.VERBOSE)
					org.rubypeople.rdt.internal.core.util.Util.verbose("-> index could not be created for " + this.containerPath); //$NON-NLS-1$
				return true;
			}
			ReadWriteMonitor monitor = index.monitor;
			if (monitor == null) {
				if (JobManager.VERBOSE)
					org.rubypeople.rdt.internal.core.util.Util.verbose("-> index for " + this.containerPath + " just got deleted"); //$NON-NLS-1$//$NON-NLS-2$
				return true; // index got deleted since acquired
			}
			File file = null;
			try {
				monitor.enterWrite(); // ask permission to write

				if (RubyModelManager.ZIP_ACCESS_VERBOSE)
					System.out.println("(" + Thread.currentThread() + ") [AddJarFileToIndex.execute()] Creating ZipFile on " + this.containerPath); //$NON-NLS-1$	//$NON-NLS-2$
				// external file -> it is ok to use toFile()
				file = this.containerPath.toFile();
				// path is already canonical since coming from a library
				// classpath entry

				if (this.isCancelled) {
					if (JobManager.VERBOSE)
						org.rubypeople.rdt.internal.core.util.Util.verbose("-> indexing of " + file.getName() + " has been cancelled"); //$NON-NLS-1$ //$NON-NLS-2$
					return false;
				}

				if (JobManager.VERBOSE)
					org.rubypeople.rdt.internal.core.util.Util.verbose("-> indexing " + file.getName()); //$NON-NLS-1$
				long initialTime = System.currentTimeMillis();

				String[] paths = index.queryDocumentNames(""); // all file
																// names
																// //$NON-NLS-1$
				if (paths != null) {
					int max = paths.length;
					/*
					 * check integrity of the existing index file if the length
					 * is equal to 0, we want to index the whole jar again If
					 * not, then we want to check that there is no missing
					 * entry, if one entry is missing then we recreate the index
					 */
					String EXISTS = "OK"; //$NON-NLS-1$
					String DELETED = "DELETED"; //$NON-NLS-1$
					SimpleLookupTable indexedFileNames = new SimpleLookupTable(max == 0 ? 33 : max + 11);
					for (int i = 0; i < max; i++)
						indexedFileNames.put(paths[i], DELETED);
					
					addDirectorysChildren(file, EXISTS, indexedFileNames);
					boolean needToReindex = indexedFileNames.elementSize != max; // a
																					// new
																					// file
																					// was
																					// added
					if (!needToReindex) {
						Object[] valueTable = indexedFileNames.valueTable;
						for (int i = 0, l = valueTable.length; i < l; i++) {
							if (valueTable[i] == DELETED) {
								needToReindex = true; // a file was deleted so
														// re-index
								break;
							}
						}
						if (!needToReindex) {
							if (JobManager.VERBOSE)
								org.rubypeople.rdt.internal.core.util.Util.verbose("-> no indexing required (index is consistent with library) for " //$NON-NLS-1$
										+ file.getName() + " (" //$NON-NLS-1$
										+ (System.currentTimeMillis() - initialTime) + "ms)"); //$NON-NLS-1$
							this.manager.saveIndex(index); // to ensure its
															// placed into the
															// saved state
							return true;
						}
					}
				}

				// Index the jar for the first time or reindex the jar in case
				// the previous index file has been corrupted
				// index already existed: recreate it so that we forget about
				// previous entries
				SearchParticipant participant = BasicSearchEngine.getDefaultSearchParticipant();
				index = manager.recreateIndex(this.containerPath);
				if (index == null) {
					// failed to recreate index, see 73330
					manager.removeIndex(this.containerPath);
					return false;
				}

				if (!indexFiles(index, file, participant)) return false;
				this.manager.saveIndex(index);
				if (JobManager.VERBOSE)
					org.rubypeople.rdt.internal.core.util.Util.verbose("-> done indexing of " //$NON-NLS-1$
							+ file.getName() + " (" //$NON-NLS-1$
							+ (System.currentTimeMillis() - initialTime) + "ms)"); //$NON-NLS-1$
			} finally {
				monitor.exitWrite(); // free write lock
			}
		} catch (IOException e) {
			if (JobManager.VERBOSE) {
				org.rubypeople.rdt.internal.core.util.Util.verbose("-> failed to index " + this.containerPath + " because of the following exception:"); //$NON-NLS-1$ //$NON-NLS-2$
				e.printStackTrace();
			}
			manager.removeIndex(this.containerPath);
			return false;
		}
		return true;
	}

	private boolean indexFiles(Index index, File file, SearchParticipant participant) throws FileNotFoundException, IOException {
		File[] children = file.listFiles();
		if (children == null) return true;
		for (int i = 0; i < children.length; i++) {
			if (this.isCancelled) {
				if (JobManager.VERBOSE)
					org.rubypeople.rdt.internal.core.util.Util.verbose("-> indexing of " + file.getName() + " has been cancelled"); //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			}
			String name = children[i].getName();
			if (children[i].isFile() && Util.isRubyOrERBLikeFileName(name)) {
				InputStream stream = new FileInputStream(children[i]);
				char[] contents = Util.getInputStreamAsCharArray(stream, -1, null);
				RubySearchDocument entryDocument;
				if (Util.isERBLikeFileName(name)) {
					entryDocument = new ERBSearchDocument(children[i].getAbsolutePath(), contents, participant);
				} else {
					entryDocument = new RubySearchDocument(children[i].getAbsolutePath(), contents, participant);
				}				
				this.manager.indexDocument(entryDocument, participant, index, this.containerPath);
			}
			if (!indexFiles(index, children[i], participant)) return false;
		}
		return true;
	}

	private void addDirectorysChildren(File file, String EXISTS, SimpleLookupTable indexedFileNames) {
		File[] children = file.listFiles();
		if (children == null) return;
		for (int i = 0; i < children.length; i++) {
			String name = children[i].getName();
			if (Util.isRubyOrERBLikeFileName(name)) {
				indexedFileNames.put(name, EXISTS);
			}
			addDirectorysChildren(children[i], EXISTS, indexedFileNames);
		}
	}
	
	protected Integer updatedIndexState() {
		return IndexManager.REBUILDING_STATE;
	}
	public String toString() {
		return "indexing " + this.containerPath.toString(); //$NON-NLS-1$
	}
	
	public boolean equals(Object o) {
		if (o instanceof AddExternalFolderToIndex) {
			if (this.containerPath != null)
				return this.containerPath.equals(((AddExternalFolderToIndex) o).containerPath);
		}
		return false;
	}
	public int hashCode() {
		if (this.containerPath != null)
			return this.containerPath.hashCode();
		return -1;
	}

}
