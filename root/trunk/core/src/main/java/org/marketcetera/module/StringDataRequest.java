package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.beans.ConstructorProperties;

/* $License$ */
/**
 * A JMX friendly version of the {@link DataRequest} type. Instances
 * of this class are used to report data requests instead of
 * <code>DataRequest</code> as it complies with the Open MBean data types.
 *
 * Consult {@link DataRequest} documentation for details on Data Requests.
 *
 * Do note that this class is only used for reporting data request details.
 * It's not meant to be used when creating data flows. Only {@link DataRequest}
 * instances can be used to issue create data flow requests.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public class StringDataRequest extends DataRequestBase {
    /**
     * Creates an instance.
     *
     * @param inRequestURN the instance URN
     * @param inCoupling the coupling to use
     * @param inData the request parameter
     */
    @ConstructorProperties({
            "requestURN",     //$NON-NLS-1$
            "coupling",       //$NON-NLS-1$
            "data"            //$NON-NLS-1$
            })
    public StringDataRequest(ModuleURN inRequestURN,
                       DataCoupling inCoupling,
                       String inData) {
        super(inCoupling, inRequestURN);
        mData = inData;
    }

    /**
     * The request parameter converted to a string value via
     * {@link Object#toString()}.
     *
     * @return the request parameter
     */
    public String getData() {
        return mData;
    }

    private final String mData;
    private static final long serialVersionUID = 7808821329666070735L;
}