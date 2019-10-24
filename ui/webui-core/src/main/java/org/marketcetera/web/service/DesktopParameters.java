package org.marketcetera.web.service;

import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.vaadin.server.Page;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;

/* $License$ */

/**
 * Manages the desktop viewable area parameters.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DesktopParameters
        implements BrowserWindowResizeListener
{
    /**
     * Get the top value.
     *
     * @return a <code>int</code> value
     */
    public int getTop()
    {
        return top;
    }
    /**
     * Sets the top value.
     *
     * @param inTop a <code>int</code> value
     */
    public void setTop(int inTop)
    {
        top = inTop;
    }
    /**
     * Get the left value.
     *
     * @return a <code>int</code> value
     */
    public int getLeft()
    {
        return left;
    }
    /**
     * Sets the left value.
     *
     * @param inLeft a <code>int</code> value
     */
    public void setLeft(int inLeft)
    {
        left = inLeft;
    }
    /**
     * Get the bottom value.
     *
     * @return a <code>int</code> value
     */
    public int getBottom()
    {
        return bottom;
    }
    /**
     * Sets the bottom value.
     *
     * @param inBottom a <code>int</code> value
     */
    public void setBottom(int inBottom)
    {
        bottom = inBottom;
    }
    /**
     * Get the right value.
     *
     * @return a <code>int</code> value
     */
    public int getRight()
    {
        return right;
    }
    /**
     * Sets the right value.
     *
     * @param inRight a <code>int</code> value
     */
    public void setRight(int inRight)
    {
        right = inRight;
    }
    /**
     * Recalculate the dynamic parameters.
     */
    public void recalculate()
    {
        bottom = Page.getCurrent().getBrowserWindowHeight();
        right = Page.getCurrent().getBrowserWindowWidth();
    }
    /* (non-Javadoc)
     * @see com.vaadin.server.Page.BrowserWindowResizeListener#browserWindowResized(com.vaadin.server.Page.BrowserWindowResizeEvent)
     */
    @Override
    public void browserWindowResized(BrowserWindowResizeEvent inEvent)
    {
        recalculate();
        SLF4JLoggerProxy.trace(this,
                               "Browser resize: {} -> {}",
                               inEvent,
                               this);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DesktopParameters [top=").append(top).append(", left=").append(left).append(", bottom=")
                .append(bottom).append(", right=").append(right).append("]");
        return builder.toString();
    }
    /**
     * desktop viewable area top edge coordinate
     */
    private int top = topPad;
    /**
     * desktop viewable area left edge coordinate
     */
    private int left = 0;
    /**
     * desktop viewable area bottom edge coordinate
     */
    private int bottom;
    /**
     * desktop viewable area right edge coordinate
     */
    private int right;
    /**
     * hardcoded value to add a pad at the top edge to prevent overlapping of the tool bar
     */
    private static final int topPad = 100;
    private static final long serialVersionUID = 3607021713755459085L;
}
