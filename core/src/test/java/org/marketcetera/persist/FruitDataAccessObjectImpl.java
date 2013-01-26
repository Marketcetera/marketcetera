package org.marketcetera.persist;

import java.util.List;

import javax.persistence.Query;

import org.marketcetera.persist.Fruit.Type;
import org.springframework.stereotype.Repository;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Repository
public class FruitDataAccessObjectImpl
        extends AbstractNamedDataAccessObject<Fruit>
        implements FruitDataAccessObject
{
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractNamedDataAccessObject#getByName(java.lang.String)
     */
    @Override
    public Fruit getByName(String inName)
    {
        return super.getByName(inName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractDataAccessObject#getById(long)
     */
    @Override
    public Fruit getById(long inId)
    {
        return super.getById(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractDataAccessObject#getAll()
     */
    @Override
    public List<Fruit> getAll()
    {
        return super.getAll();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractDataAccessObject#getAll(int, int, java.lang.String)
     */
    @Override
    public List<Fruit> getAll(int inMaxResults,
                              int inFirstResult,
                              String inOrderBy)
    {
        return super.getAll(inMaxResults,
                            inFirstResult,
                            inOrderBy);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractDataAccessObject#save(org.marketcetera.persist.EntityBase)
     */
    @Override
    public void save(Fruit inData)
    {
        super.save(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractDataAccessObject#add(org.marketcetera.persist.EntityBase)
     */
    @Override
    public void add(Fruit inData)
    {
        if(exceptionBefore != null) {
            throw exceptionBefore;
        }
        super.add(inData);
        if(exceptionAfter != null) {
            throw exceptionAfter;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractDataAccessObject#delete(org.marketcetera.persist.EntityBase)
     */
    @Override
    public void delete(Fruit inData)
    {
        super.delete(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractDataAccessObject#getCount()
     */
    @Override
    public int getCount()
    {
        return super.getCount();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.FruitDataAccessObject#getByType(org.marketcetera.persist.Fruit.Type)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Fruit> getByType(Type inType)
    {
        Query query = getEntityManager().createNamedQuery("Fruit.byType");
        return query.getResultList();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.FruitDataAccessObject#setExceptionBefore(java.lang.RuntimeException)
     */
    @Override
    public void setExceptionBefore(RuntimeException inException)
    {
        exceptionBefore = inException;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.FruitDataAccessObject#setExceptionAfter(java.lang.RuntimeException)
     */
    @Override
    public void setExceptionAfter(RuntimeException inException)
    {
        exceptionAfter = inException;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.FruitDataAccessObject#reset()
     */
    @Override
    public void reset()
    {
        exceptionAfter = null;
        exceptionBefore = null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractDataAccessObject#getDataType()
     */
    @Override
    protected Class<? extends Fruit> getDataType()
    {
        return Fruit.class;
    }
    private RuntimeException exceptionAfter;
    private RuntimeException exceptionBefore;
}
