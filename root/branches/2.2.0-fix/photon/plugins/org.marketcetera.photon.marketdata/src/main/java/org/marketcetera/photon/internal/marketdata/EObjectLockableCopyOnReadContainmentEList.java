package org.marketcetera.photon.internal.marketdata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an <code>EList</code> implementation that provides limited concurrent-safe operations.
 * 
 * <p>This collection provides concurrent-safe <code>Iterator</code> operations:
 * <ul>
 *   <li>{@link #basicIterator()}</li>
 *   <li>{@link #basicListIterator()</li>
 *   <li>{@link #basicListIterator(int)</li>
 *   <li>{@link #iterator()</li>
 *   <li>{@link #listIterator()</li>
 *   <li>{@link #listIterator(int)</li>
 * </ul></p>
 * 
 * <p>These iterator operations provide a snapshot of the collection contents upon invocation.
 * It is safe to traverse these iterators regardless of modifications to the original list.
 * Operations conducted using the iterators, however, will not be reflected in the original list.</p>
 * 
 * <p>Callers must execute operations on this list using the provided {@link #doWriteOperation(Callable)} and
 * {@link #doReadOperation(Callable)} methods. Note that intrinsic list operations not covered in the list above
 * are not guaranteed to be concurrent. Such list operations must be manually locked using one of the lock
 * operation methods.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.2.0
 */
@ClassVersion("$Id$")
public class EObjectLockableCopyOnReadContainmentEList<E>
        extends EObjectContainmentEList<E>
        implements LockableEList<E>
{
    /**
     * Create a new EObjectCopyOnReadContainmentEList instance.
     *
     * @param inDataClass a <code>Class&lt;E&gt;</code> value
     * @param inOwner an <code>InternalEObject</code> value
     * @param inFeatureID an <code>int</code> value
     */
    public EObjectLockableCopyOnReadContainmentEList(Class<E> inDataClass,
                                                     InternalEObject inOwner,
                                                     int inFeatureID)
    {
        super(inDataClass,
              inOwner,
              inFeatureID);
    }
    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.util.NotifyingInternalEListImpl#basicIterator()
     */
    @Override
    public Iterator<E> basicIterator()
    {
        return doReadOperation(new Callable<Iterator<E>>() {
            @Override
            public Iterator<E> call()
                    throws Exception
            {
                return new ArrayList<E>(EObjectLockableCopyOnReadContainmentEList.this).iterator();
            }
        });
    }
    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.util.NotifyingInternalEListImpl#basicListIterator()
     */
    @Override
    public ListIterator<E> basicListIterator()
    {
        return doReadOperation(new Callable<ListIterator<E>>() {
            @Override
            public ListIterator<E> call()
                    throws Exception
            {
                return new ArrayList<E>(EObjectLockableCopyOnReadContainmentEList.this).listIterator();
            }
        });
    }
    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.util.NotifyingInternalEListImpl#basicListIterator(int)
     */
    @Override
    public ListIterator<E> basicListIterator(final int inIndex)
    {
        return doReadOperation(new Callable<ListIterator<E>>() {
            @Override
            public ListIterator<E> call()
                    throws Exception
            {
                return new ArrayList<E>(EObjectLockableCopyOnReadContainmentEList.this).listIterator(inIndex);
            }
        });
    }
    /* (non-Javadoc)
     * @see org.eclipse.emf.common.util.AbstractEList#iterator()
     */
    @Override
    public Iterator<E> iterator()
    {
        return doReadOperation(new Callable<Iterator<E>>() {
            @Override
            public Iterator<E> call()
                    throws Exception
            {
                return new ArrayList<E>(EObjectLockableCopyOnReadContainmentEList.this).iterator();
            }
        });
    }
    /* (non-Javadoc)
     * @see org.eclipse.emf.common.util.AbstractEList#listIterator()
     */
    @Override
    public ListIterator<E> listIterator()
    {
        return doReadOperation(new Callable<ListIterator<E>>() {
            @Override
            public ListIterator<E> call()
                    throws Exception
            {
                return new ArrayList<E>(EObjectLockableCopyOnReadContainmentEList.this).listIterator();
            }
        });
    }
    /* (non-Javadoc)
     * @see org.eclipse.emf.common.util.AbstractEList#listIterator(int)
     */
    @Override
    public ListIterator<E> listIterator(final int inIndex)
    {
        return doReadOperation(new Callable<ListIterator<E>>() {
            @Override
            public ListIterator<E> call()
                    throws Exception
            {
                return new ArrayList<E>(EObjectLockableCopyOnReadContainmentEList.this).listIterator(inIndex);
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.internal.marketdata.LockableEList#doReadOperation(java.util.concurrent.Callable)
     */
    @Override
    public <T> T doReadOperation(Callable<T> inOperation)
    {
        Lock iteratorLock = lock.readLock();
        boolean locked = false;
        try {
            iteratorLock.lockInterruptibly();
            locked = true;
            return inOperation.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(locked) {
                iteratorLock.unlock();
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.internal.marketdata.LockableEList#doWriteOperation(java.util.concurrent.Callable)
     */
    @Override
    public <T> T doWriteOperation(Callable<T> inOperation)
    {
        Lock iteratorLock = lock.writeLock();
        boolean locked = false;
        try {
            iteratorLock.lockInterruptibly();
            locked = true;
            return inOperation.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(locked) {
                iteratorLock.unlock();
            }
        }
    }
    /**
     * lock used to guard access
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final long serialVersionUID = 1L;
}
