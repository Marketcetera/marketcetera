package org.marketcetera.photon.commons.emf;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides support for saving and restoring a list of {@link EObject EObjects}
 * to a file.
 * <p>
 * Warning: EMF generated objects are not typically thread safe. Clients are
 * responsible for guaranteeing that the objects are visible to the thread
 * calling {@link #save(Collection)}. Likewise, clients are responsible for safe
 * publication or EObjects returned from {@link #restore()}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class EMFFilePersistence implements IEMFPersistence {

    private final Resource mResource;

    /**
     * Constructor.
     * 
     * @param file
     *            the file to use for persistence
     * @throws IllegalArgumentException
     *             if file is null
     */
    public EMFFilePersistence(File file) {
        Validate.notNull(file, "file"); //$NON-NLS-1$
        mResource = new XMIResourceImpl(URI.createFileURI(file.getPath()));
    }

    @Override
    public void save(Collection<? extends EObject> objects) throws IOException {
        Validate.noNullElements(objects, "objects"); //$NON-NLS-1$
        ImmutableList<EObject> copy = ImmutableList.copyOf(objects);
        synchronized (mResource) {
            mResource.getContents().clear();
            for (EObject object : copy) {
                mResource.getContents().add(object);
            }
            mResource.save(null);
        }
    }

    @Override
    public List<EObject> restore() throws IOException {
        synchronized (mResource) {
            mResource.load(null);
            // make a copy and debug log the contents
            List<EObject> list = Lists.newArrayList();
            for (EObject object : mResource.getContents()) {
                SLF4JLoggerProxy.debug(this, "Deserialized object: {}.", //$NON-NLS-1$
                        object);
                list.add(object);
            }
            return list;
        }
    }
}
