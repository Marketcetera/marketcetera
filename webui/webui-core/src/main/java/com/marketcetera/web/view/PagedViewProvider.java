package com.marketcetera.web.view;

import java.util.List;

import com.vaadin.data.sort.SortOrder;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;

/* $License$ */

/**
 * Provides the controls for a paged view.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface PagedViewProvider
{
    /**
     * Get the page size widget.
     *
     * @return a <code>ComboBox</code> value
     */
    ComboBox getPageSizeSelect();
    /**
     * Get the first page widget.
     *
     * @return a <code>Button</code> value
     */
    Button getFirstPageButton();
    /**
     * Get the previous page widget.
     *
     * @return a <code>Button</code> value
     */
    Button getPrevPageButton();
    /**
     * Get the next page widget.
     *
     * @return a <code>Button</code> value
     */
    Button getNextPageButton();
    /**
     * Get the last page widget.
     *
     * @return a <code>Button</code> value
     */
    Button getLastPageButton();
    /**
     * Get the current page label widget.
     *
     * @return a <code>Label</code> value
     */
    Label getCurrentPageLabel();
    /**
     * Get the total items label widget.
     *
     * @return a <code>Label</code> value
     */
    Label getTotalItemsLabel();
    /**
     * Get the current sort order.
     *
     * @return a <code>List&lt;SortOrder&gt;</code> value
     */
    List<SortOrder> getSortOrder();
}
