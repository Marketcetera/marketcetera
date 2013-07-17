package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

import java.util.Date;

/* $License$ */
/**
 * Helper class to test multi query ordering behavior for string attributes
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
class MultiQueryOrderLastUpdatedTestHelper<C extends EntityBase,
        S extends SummaryEntityBase> extends
        MultiQueryOrderComparableTestHelper<C, S, Date> {
    /**
     * Creates an instance to test ordering of string attribute fields
     *
     * @param order         The entity order instance.
     * @param clazz         The entity class
     * @param attributeName The attribute names
     * @param query The query instance
     * 
     * @throws Exception if there's an error
     */
    public MultiQueryOrderLastUpdatedTestHelper(EntityOrder order,
                                           Class<C> clazz,
                                           String attributeName,
                                           MultipleEntityQuery query)
            throws Exception {
        super(order, clazz, attributeName,query);
    }

    protected int getNumInstances() {
        //pick any number.
        return 13;
    }

    protected Object generateFieldValue(int idx) {
        //the value is auto generated.
        return null;
    }

    protected void setOrderField(C c, int idx) throws Exception {
        //Sleep to make sure that we have differences in
        //last updated time stamp
        //the value is auto generated on save
        Thread.sleep(2000);
    }
}
