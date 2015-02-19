package org.marketcetera.marketdata.core.webservice;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates the desired page for a multi-record request.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="pageRequest")
@ClassVersion("$Id$")
public class PageRequest
        implements Serializable
{
    /**
     * Create a new MyPageRequest instance.
     *
     * @param inPage an <code>int</code> value
     * @param inSize an <code>int</code> value
     */
    public PageRequest(int inPage,
                       int inSize)
    {
        page = inPage;
        size = inSize;
    }
    /**
     * Get the page value.
     *
     * @return an <code>int</code> value
     */
    public int getPage()
    {
        return page;
    }
    /**
     * Sets the page value.
     *
     * @param inPage an <code>int</code> value
     */
    public void setPage(int inPage)
    {
        page = inPage;
    }
    /**
     * Get the size value.
     *
     * @return an <code>int</code> value
     */
    public int getSize()
    {
        return size;
    }
    /**
     * Sets the size value.
     *
     * @param inSize an <code>int</code> value
     */
    public void setSize(int inSize)
    {
        size = inSize;
    }
    /**
     * Create a new PageRequest instance.
     */
    @SuppressWarnings("unused")
    private PageRequest() {}
    /**
     * page number, 1-indexed
     */
    @XmlAttribute
    private int page;
    /**
     * page size
     */
    @XmlAttribute
    private int size;
    private static final long serialVersionUID = -1300192846281822973L;
}
