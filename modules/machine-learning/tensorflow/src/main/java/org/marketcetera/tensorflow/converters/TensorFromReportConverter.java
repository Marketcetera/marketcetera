package org.marketcetera.tensorflow.converters;

import org.marketcetera.trade.ReportBase;
import org.tensorflow.Tensor;

/* $License$ */

/**
 * Translates {@link ReportBase} objects to tensors. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TensorFromReportConverter
        extends AbstractTensorFromObjectConverter<ReportBase>
{
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.TensorConverter#convert(java.lang.Object)
     */
    @Override
    public Tensor convert(ReportBase inType)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.TensorConverter#getType()
     */
    @Override
    public Class<ReportBase> getType()
    {
        return ReportBase.class;
    }
}
