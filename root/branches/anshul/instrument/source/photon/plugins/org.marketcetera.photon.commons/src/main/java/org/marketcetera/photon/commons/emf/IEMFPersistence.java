package org.marketcetera.photon.commons.emf;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface for objects that can save and restore {@link EObject EObjects}. The
 * methods {@link #save(Collection)} and {@link #restore()} can be called multiple
 * times, but every save overwrites any existing saved state.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface IEMFPersistence {

    /**
     * Restores EObjects last saved by {@link #save(Collection)}.
     * 
     * @return the saved EObjects
     * @throws IOException
     *             if the restore fails
     */
    List<? extends EObject> restore() throws IOException;

    /**
     * Saves the provided EObjects in a way that can later be restored using
     * {@link #restore()}. This will overwrite any previously saved state.
     * 
     * @param objects
     *            the EObjects to save
     * @throws IOException
     *             if the save fails
     * @throws IllegalArgumentException
     *             if objects is null or has null elements
     */
    void save(Collection<? extends EObject> objects) throws IOException;

}