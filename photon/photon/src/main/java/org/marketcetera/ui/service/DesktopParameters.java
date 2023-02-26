package org.marketcetera.ui.service;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/* $License$ */

/**
 * Manages the desktop viewable area parameters.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DesktopParameters
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
        // note that there can be more than one screen if you have multiple monitors. this doesn't handle multiple monitors
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        bottom = (int)bounds.getHeight();
        right = (int)bounds.getWidth();
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
    private int top = 0;
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
}
