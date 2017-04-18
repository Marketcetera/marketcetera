package org.marketcetera.tensorflow.converters;

import java.util.List;

import org.marketcetera.module.StopDataFlowException;
import org.marketcetera.tensorflow.Messages;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.tensorflow.Tensor;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides conversion services from multiple converters to a Tensor.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AggregateTensorFromObjectConverter
        implements TensorFromObjectConverter<Object>
{
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.converters.TensorFromObjectConverter#convert(java.lang.Object)
     */
    @Override
    public Tensor convert(Object inData)
    {
        for(TensorFromObjectConverter<?> converter : converters) {
            Class<?> converterClass = converter.getType();
            if(converterClass.isAssignableFrom(inData.getClass())) {
                return converter.convert(inData);
            }
        }
        throw new StopDataFlowException(new I18NBoundMessage2P(Messages.INVALID_DATA_TYPE,
                                                               inData.getClass().getSimpleName(),
                                                               description));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.converters.TensorFromObjectConverter#getType()
     */
    @Override
    public Class<Object> getType()
    {
        return Object.class;
    }
    /**
     * Get the converters value.
     *
     * @return a <code>List&lt;TensorFromObjectConverter&lt;?&gt;&gt;</code> value
     */
    public List<TensorFromObjectConverter<?>> getConverters()
    {
        return converters;
    }
    /**
     * Sets the converters value.
     *
     * @param inConverters a <code>List&lt;TensorFromObjectConverter&lt;?&gt;&gt;</code> value
     */
    public void setConverters(List<TensorFromObjectConverter<?>> inConverters)
    {
        converters = inConverters;
    }
    /**
     * Get the description value.
     *
     * @return a <code>String</code> value
     */
    public String getDescription()
    {
        return description;
    }
    /**
     * Sets the description value.
     *
     * @param inDescription a <code>String</code> value
     */
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /**
     * converters available to convert to tensors
     */
    private List<TensorFromObjectConverter<?>> converters = Lists.newArrayList();
    /**
     * description of domain handled by this aggregate converter
     */
    private String description = "multiple types";
}
