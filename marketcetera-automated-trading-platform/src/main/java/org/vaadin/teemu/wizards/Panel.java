package org.vaadin.teemu.wizards;

import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.Scroller.ScrollDirection;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SuppressWarnings("serial")
public class Panel
        extends VerticalLayout
{
    /**
     * Create a new Panel instance.
     */
    public Panel()
    {
        scroller = new Scroller(this);
        scroller.setScrollDirection(ScrollDirection.BOTH);
    }
    private Scroller scroller;
    /**
     *
     *
     * @param inScrollTop
     */
    public void setScrollTop(int inScrollTop)
    {
    }
    /**
     *
     *
     * @param inScrollLeft
     */
    public void setScrollLeft(int inScrollLeft)
    {
    }

    /**
     *
     *
     * @return
     */
    public int getScrollTop()
    {
        return -1;
    }

    /**
     *
     *
     * @return
     */
    public int getScrollLeft()
    {
        return -1;
    }
}
