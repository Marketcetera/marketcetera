package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.io.File;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.rubypeople.rdt.core.LocalFileStorage;

/**
 * @since 3.0
 */
public class ExternalRubyFileEditorInput implements IStorageEditorInput, ILocationProvider, IPersistableElement {

	private LocalFileStorage storage;

	public ExternalRubyFileEditorInput(File file) {
		storage = new LocalFileStorage(file);
	}
	
	public ExternalRubyFileEditorInput(LocalFileStorage file) {
		storage = file;
	}

	public boolean exists() {
		return storage.getFile().exists();
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return storage.getFile().getName();
	}

	public void saveState(IMemento memento) {
		memento.putString(RubyExternalEditorFactory.MEMENTO_ABSOLUTE_PATH_KEY, storage.getFile().getAbsolutePath()); //$NON-NLS-1$
	}

	public String getFactoryId() {
		return RubyExternalEditorFactory.FACTORY_ID;
	}

	public IStorage getStorage() {
		return storage;
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return this;
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return storage.getFile().getAbsolutePath();
	}

	/*
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 *
	public Object getAdapter(Class adapter) {
		if (adapter.equals(File.class)) return storage.getFile();
		return null;
	}
	*/
	
	public Object getAdapter(Class adapter) {
		if (ILocationProvider.class.equals(adapter)) return this;
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/*
	 * @see org.eclipse.ui.editors.text.ILocationProvider#getPath(java.lang.Object)
	 */
	public IPath getPath(Object element) {
		if (element instanceof ExternalRubyFileEditorInput) {
			ExternalRubyFileEditorInput input = (ExternalRubyFileEditorInput) element;
			return new Path(input.getFilesystemFile().getAbsolutePath());
		}
		return null;
	}



	public File getFilesystemFile() {
		return this.storage.getFile();
	}

	public boolean equals(Object object) {
		return object instanceof ExternalRubyFileEditorInput && getStorage().equals(((ExternalRubyFileEditorInput) object).getStorage());
	}

	public int hashCode() {
		return getStorage().hashCode();
	}

}