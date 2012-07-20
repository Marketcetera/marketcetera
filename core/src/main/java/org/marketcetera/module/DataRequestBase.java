package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.io.Serializable;

/* $License$ */
/**
 * The base class for data request instances. This class is
 * not meant to be used directly. It only unifies the common elements
 * from its subclasses.  
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public class DataRequestBase implements Serializable {
    /**
     * Creates an instance.
     *
     * @param inCoupling the data coupling value
     * @param inRequestURN the module instance URN
     */
    protected DataRequestBase(DataCoupling inCoupling,
                              ModuleURN inRequestURN) {
        mCoupling = inCoupling;
        mRequestURN = inRequestURN;
    }

    /**
     * The module URN that is used to identify the module instance for
     * this stage of the data flow.
     *
     * @return the module instance URN
     */
    public ModuleURN getRequestURN() {
        return mRequestURN;
    }

    /**
     * The coupling to use to use for this request.
     * The coupling value is relevant only if this request references
     * a module instance that will receive data. The coupling applies
     * to the data flow between this module and the module that emits
     * data to it.
     *
     * @return the coupling to use for this request.
     */
    public DataCoupling getCoupling() {
        return mCoupling;
    }

    protected final ModuleURN mRequestURN;
    protected final DataCoupling mCoupling;
    private static final long serialVersionUID = -4675728462938110941L;
}
