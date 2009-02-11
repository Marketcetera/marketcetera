package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;

import quickfix.Field;

/**
 * Represents "custom" fields (i.e. non-predetermined fields) that can show up in the list of orders These can appear only as integers, and their value are
 * treated as either ints, doubles or strings (catch-all) Ex: Price,OrderQty,1324,Account
 * 
 * @author Toli Kuznets
 * @author <a href="mailto:colin@whiskerfish.com">Colin DuPlantis</a>
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class CustomField<T>
    extends Field<T>
{
    private static final long serialVersionUID = -2296068855246553757L;    
    public CustomField(int i,
                       T inObject)
    {
        super(i,
              inObject);
    }
    @Override
    public String toString()
    {
        return new StringBuffer().append(getTag()).toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + getTag();
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final CustomField<?> other = (CustomField<?>)obj;
        if (getTag() != other.getTag())
            return false;
        return true;
    }
}
